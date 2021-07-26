/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.visual.managers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.modules.visual.configs.VisualConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.ChunkPos;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CachedChunkManager {

    private static final int SERVER_RENDER_DISTANCE = 4;
    private static final int INJECTED_CHUNK_DISTANCE = 4090; // the real value is 4096

    private static final int VERSION = 2; // if you change the saved content update this number
    private static final File CACHE_FOLDER = new File(Reference.MOD_STORAGE_ROOT, "cachedChunks");
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
            .setNameFormat("Wynntils-CachedChunks-%d").build()
    );

    private static final Set<ChunkPos> ignoredChunks = new HashSet<>();
    private static boolean running = false;

    /**
     * Async schedules the provided chunk to be cached
     * @param data the chunk packet data
     */
    public static void asyncCacheChunk(SPacketChunkData data) {
        EXECUTOR.execute(() -> cacheChunk(data));
    }

    /**
     * Starts to load and unload cached chunks asynchronously
     */
    public static void startAsyncChunkLoader() {
        if (running) return;

        EXECUTOR.execute(CachedChunkManager::startChunkLoader);
    }

    /**
     * Deletes all available chunk cache
     */
    public static void deleteCache() {
        if (!CACHE_FOLDER.exists()) return;
        try {
            LOCK.writeLock().lock();

            for (File f : CACHE_FOLDER.listFiles()) {
                f.delete();
            }

            CACHE_FOLDER.delete();
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    /**
     * Caches the provided chunk into a file in the hard disk.
     * Cached files are located inside wynntils/cachedChunks.
     *
     * Each chunk is individually saved as a new file to increase the reading and decompression speed.
     * Every cached chunk has a header data that contains the folowing informations:
     *  - The Wynntils Chunk Version (used if we need to change something in the chunk format to discart old ones)
     *  - the original chunk size (used for decompression)
     *  - the compressed chunk size (used for reading data)
     *
     * This method is thread safe.
     *
     * @param data the chunk data
     */
    private static void cacheChunk(SPacketChunkData data) {
        if (!data.isFullChunk()) return;

        if (!CACHE_FOLDER.exists()) CACHE_FOLDER.mkdirs();

        try {
            LOCK.writeLock().lock();

            // Remove from ignored chunks
            ignoredChunks.remove(new ChunkPos(data.getChunkX(), data.getChunkZ()));

            // We are saving each chunk into a different file for faster reading!
            File chunk = new File(CACHE_FOLDER, data.getChunkX() + "_" + data.getChunkZ() + ".wchunk");
            if (!chunk.exists()) chunk.createNewFile();

            // Write the chunk data inside the buffer so we can compress
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            data.writePacketData(buffer);

            // Compress the packet data
            byte[] original = buffer.array();
            byte[] compressed = deflate(original);

            // Write the header that includes the original size and the compressed size
            PacketBuffer headed = new PacketBuffer(Unpooled.buffer());
            headed.writeVarInt(VERSION);
            headed.writeVarInt(original.length);
            headed.writeVarInt(compressed.length);
            headed.writeBytes(compressed);

            // Compress the data using ZSTD since it's way faster than ZLIB
            FileUtils.writeByteArrayToFile(chunk, headed.array());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    /**
     * Starts to unload and load required cached chunks into the game.
     * Chunks are loaded based on the player render distance.
     *
     * This method is thread safe
     */
    private static void startChunkLoader() {
        while (Reference.onWorld && VisualConfig.CachedChunks.INSTANCE.enabled) {
            // Sleep the thread for 1 second, we don't care about precision for this
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) { }

            int renderDistance = McIf.mc().gameSettings.renderDistanceChunks;
            ChunkPos player = new ChunkPos(McIf.player().chunkCoordX, McIf.player().chunkCoordZ);

            // Calculate which chunks needs to be loaded
            Set<ChunkPos> toLoad = new HashSet<>();
            for (int x = -renderDistance; x < renderDistance; x++) {
                for (int z = -renderDistance; z < renderDistance; z++) {
                    // Ignore the render distance this will be sent by the server
                    if (Math.abs(x) <= SERVER_RENDER_DISTANCE && Math.abs(z) <= SERVER_RENDER_DISTANCE) continue;

                    ChunkPos pos = new ChunkPos(player.x + x, player.z + z);

                    // Injected chunks are chunks dinamically alocated for for housing/war arenas
                    if (Math.abs(pos.x) >= INJECTED_CHUNK_DISTANCE && Math.abs(pos.z) >= INJECTED_CHUNK_DISTANCE) continue;
                    if (McIf.world().getChunkProvider().getLoadedChunk(pos.x, pos.z) != null) continue;

                    toLoad.add(pos);
                }
            }

            // Load required chunks
            if (toLoad.isEmpty()) continue;

            try {
                LOCK.readLock().lock();

                // Remove all chunks outside the range from ignored chunks so we don't build up a huge list
                ignoredChunks.removeIf(c -> !toLoad.contains(c));

                for (ChunkPos pos : toLoad) {
                    if (ignoredChunks.contains(pos)) continue;

                    File chunk = new File(CACHE_FOLDER, pos.x + "_" + pos.z + ".wchunk");

                    // We add it to ignored chunks if the file doesn't exists
                    // so we don't keep trying to load it over and over
                    if (!chunk.exists()) {
                        ignoredChunks.add(pos);
                        continue;
                    }

                    // Load the required data to be decompressed
                    byte[] data = FileUtils.readFileToByteArray(chunk);

                    // Load the header and find the offsets
                    ByteBuf original = Unpooled.buffer();
                    original.writeBytes(data);
                    original.readerIndex(0);

                    PacketBuffer originalBuffer = new PacketBuffer(original);

                    // Check if the version is valid
                    int chunkVersion = originalBuffer.readVarInt();
                    if (chunkVersion != VERSION) continue;

                    // Decompress the data
                    int originalSize = originalBuffer.readVarInt();
                    byte[] compressed = new byte[originalBuffer.readVarInt()];
                    original.readBytes(compressed);

                    byte[] decompressed = inflate(compressed, originalSize);

                    // Generate the buffer based on the bytes we had earlier
                    ByteBuf buffer = Unpooled.buffer();
                    buffer.writeBytes(decompressed);
                    buffer.readerIndex(0);

                    PacketBuffer packetBuffer = new PacketBuffer(buffer);

                    // Create the packet
                    SPacketChunkData packet = new SPacketChunkData();
                    packet.readPacketData(packetBuffer);

                    // Submit the packet to the client handler bypassing the packet sent
                    McIf.mc().addScheduledTask(() -> McIf.mc().getConnection().handleChunkData(packet));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                LOCK.readLock().unlock();
            }
        }

        // Whatever needs to be executed when the thread dies
        ignoredChunks.clear();
        running = false;
    }

    private static byte[] deflate(byte[] input) throws Exception {
        Deflater deflater = new Deflater(9);
        deflater.setInput(input);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);
        byte[] buffer = new byte[input.length];

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        outputStream.close();

        return outputStream.toByteArray();
    }

    private static byte[] inflate(byte[] input, int length) throws Exception {
        Inflater deflater = new Inflater();
        deflater.setInput(input);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);
        byte[] buffer = new byte[length];

        while (!deflater.finished()) {
            int count = deflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        outputStream.close();

        return outputStream.toByteArray();
    }

}
