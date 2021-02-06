/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class EncodingUtils {

    /**
     * Encodes a float to a ByteBuffer, in a shorter format if it happens to be an integer.
     * The float cannot be NaN.
     */
    public static void encodeFloat(float f, ByteBuffer buf) {
        if (Float.isNaN(f)) {
            throw new IllegalArgumentException("Tried to encode NaN");
        }

        if (Float.isFinite(f) && Long.MIN_VALUE <= f && f <= Long.MAX_VALUE && f % 1.f == 0) {
            byte[] encodedAsLong = new byte[10];
            ByteBuffer lbuf = ByteBuffer.wrap(encodedAsLong);
            encodeLong((long) f, lbuf);
            if (lbuf.position() < 3) {
                buf.put((byte) 0xFF);
                buf.put(encodedAsLong, 0, lbuf.position());
                return;
            }
        }

        buf.putFloat(f);
    }

    /**
     * Encodes a double to a ByteBuffer, in a shorter format if it happens to be an integer.
     * The double cannot be NaN.
     */
    public static void encodeDouble(double d, ByteBuffer buf) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("Tried to encode NaN");
        }

        if (Double.isFinite(d) && Long.MIN_VALUE <= d && d <= Long.MAX_VALUE && d % 1. == 0) {
            // Set first 14 bits to 1, which could only encode
            // a NaN, which have already been filtered out,
            // and encode a short integer instead
            byte[] encodedAsLong = new byte[10];
            ByteBuffer lbuf = ByteBuffer.wrap(encodedAsLong);
            encodeLong((long) d, lbuf);
            if (lbuf.position() < 7) {
                buf.put((byte) 0xFF);
                buf.put(encodedAsLong, 0, lbuf.position());
                return;
            }
        }

        buf.putDouble(d);
    }

    /**
     * Encodes a int to a ByteBuffer using a possibly shorter encoding
     */
    public static void encodeInt(int i, ByteBuffer buf) {
        encodeLong(i, buf);
    }

    /**
     * Encodes a long to a ByteBuffer using a possibly shorter encoding
     */
    public static void encodeLong(long i, ByteBuffer buf) {
        if (i == 0) {
            buf.put((byte) 0);
            return;
        }
        int negativeMask = i < 0 ? 0b01000000 : 0;
        i = Math.abs(i);
        long last6bits = i & 0b00111111;
        i >>= 6;
        if (i == 0) {
            buf.put((byte) (negativeMask | last6bits));
            return;
        }
        buf.put((byte) (0b10000000 | negativeMask | last6bits));

        while (true) {
            long last7bits = i & 0b01111111;
            i >>= 7;
            if (i == 0) {
                // Last byte to encode
                buf.put((byte) last7bits);
                break;
            } else {
                // Remaining bytes. Set highest bit to 1.
                buf.put((byte) (0b10000000 | last7bits));
            }
        }
    }

    public static float decodeFloat(ByteBuffer buf) throws BufferUnderflowException {
        byte startByte = buf.get();
        if (startByte == (byte) 0xFF) {
            // Encoded as integer
            return decodeLong(buf);
        }
        // Regular float
        byte[] floatBytes = new byte[4];
        floatBytes[0] = startByte;
        buf.get(floatBytes, 1, 3);
        return ByteBuffer.wrap(floatBytes).getFloat();
    }

    public static double decodeDouble(ByteBuffer buf) throws BufferUnderflowException {
        byte startByte = buf.get();
        if (startByte == (byte) 0xFF) {
            // Encoded as integer
            return decodeLong(buf);
        }
        // Regular double
        byte[] doubleBytes = new byte[8];
        doubleBytes[0] = startByte;
        buf.get(doubleBytes, 1, 7);
        return ByteBuffer.wrap(doubleBytes).getDouble();
    }

    public static int decodeInt(ByteBuffer buf) throws BufferUnderflowException {
        return (int) decodeLong(buf, 5);
    }

    public static long decodeLong(ByteBuffer buf) throws BufferUnderflowException{
        return decodeLong(buf, 10);
    }

    private static long decodeLong(ByteBuffer buf, int maxBytes) throws BufferUnderflowException {
        long result = 0;
        int firstByte = Byte.toUnsignedInt(buf.get());
        if (firstByte == 0) {
            return 0;
        }
        boolean negative = (firstByte & 0b01000000) != 0;
        result |= firstByte & 0b00111111;
        if ((firstByte & 0b10000000) == 0) {
            return negative ? -result : result;
        }

        --maxBytes;
        long place = 6;
        while (maxBytes-- > 0) {
            long b = Byte.toUnsignedLong(buf.get());
            assert((result & (0b01111111L << place)) == 0);
            result |= (b & 0b01111111L) << place;
            place += 7;
            if ((b & 0b10000000) == 0) {
                return negative ? -result : result;
            }
        }
        throw new BufferUnderflowException();  // The result buffer underflowed
    }

    public static byte[] deflate(String s) {
        return deflate(s.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] deflate(byte[] b) {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        List<byte[]> chunks = new ArrayList<>(b.length / 1024);
        deflater.setInput(b);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            chunks.add(Arrays.copyOf(buffer, deflater.deflate(buffer)));
        }
        byte[] result = new byte[(int) deflater.getBytesWritten()];
        int i = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, result, i, chunk.length);
            i += chunk.length;
        }
        return result;
    }

    public static byte[] inflate(byte[] deflated) throws DataFormatException {
        Inflater inflater = new Inflater();
        List<byte[]> chunks = new ArrayList<>(deflated.length / 512);
        inflater.setInput(deflated);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            chunks.add(Arrays.copyOf(buffer, inflater.inflate(buffer)));
        }
        byte[] result = new byte[(int) inflater.getBytesWritten()];
        int i = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, result, i, chunk.length);
            i += chunk.length;
        }
        return result;
    }

    public static String inflateToString(byte[] deflated) throws DataFormatException {
        return new String(inflate(deflated), StandardCharsets.UTF_8);
    }

}
