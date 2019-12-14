//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package javazoom.jl.player.advanced;

import javazoom.jl.decoder.*;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

import java.io.InputStream;

public class AdvancedPlayer {
    private Bitstream bitstream;
    private Decoder decoder;
    private AudioDevice audio;
    private boolean closed;
    private boolean complete;
    private int lastPosition;
    private PlaybackListener listener;

    public AdvancedPlayer(InputStream var1) throws JavaLayerException {
        this(var1, null);
    }

    public AdvancedPlayer(InputStream var1, AudioDevice var2) throws JavaLayerException {
        this.closed = false;
        this.complete = false;
        this.lastPosition = 0;
        this.bitstream = new Bitstream(var1);
        if (var2 != null) {
            this.audio = var2;
        } else {
            this.audio = FactoryRegistry.systemRegistry().createAudioDevice();
        }

        this.audio.open(this.decoder = new Decoder());
    }

    public void play() throws JavaLayerException {
        this.play(2147483647);
    }

    public AudioDevice getAudioDevice() {
        return audio;
    }

    public boolean play(int var1) throws JavaLayerException {
        boolean var2 = true;
        if (this.listener != null) {
            this.listener.playbackStarted(this.createEvent(PlaybackEvent.STARTED));
        }

        while(var1-- > 0 && var2) {
            var2 = this.decodeFrame();
        }

        AudioDevice var3 = this.audio;
        if (var3 != null) {
            var3.flush();
            synchronized (this) {
                this.complete = !this.closed;
                this.close();
            }

            if (this.listener != null) {
                this.listener.playbackFinished(this.createEvent(var3, PlaybackEvent.STOPPED));
            }
        }

        return var2;
    }

    public synchronized void close() {
        AudioDevice var1 = this.audio;
        if (var1 != null) {
            this.closed = true;
            this.audio = null;
            var1.close();
            this.lastPosition = var1.getPosition();

            try {
                this.bitstream.close();
            } catch (BitstreamException var3) {
                ;
            }
        }

    }

    protected boolean decodeFrame() throws JavaLayerException {
        try {
            AudioDevice var1 = this.audio;
            if (var1 == null) {
                return false;
            } else {
                Header var2 = this.bitstream.readFrame();
                if (var2 == null) {
                    return false;
                } else {
                    SampleBuffer var3 = (SampleBuffer)this.decoder.decodeFrame(var2, this.bitstream);
                    synchronized (this) {
                        var1 = this.audio;
                        if (var1 != null) {
                            var1.write(var3.getBuffer(), 0, var3.getBufferLength());
                        }
                    }

                    this.bitstream.closeFrame();
                    return true;
                }
            }
        } catch (RuntimeException var7) {
            throw new JavaLayerException("Exception decoding audio frame", var7);
        }
    }

    protected boolean skipFrame() throws JavaLayerException {
        Header var1 = this.bitstream.readFrame();
        if (var1 == null) {
            return false;
        } else {
            this.bitstream.closeFrame();
            return true;
        }
    }

    public boolean play(int var1, int var2) throws JavaLayerException {
        boolean var3 = true;

        for (int var4 = var1; var4-- > 0 && var3; var3 = this.skipFrame()) {
            ;
        }

        return this.play(var2 - var1);
    }

    private PlaybackEvent createEvent(int var1) {
        return this.createEvent(this.audio, var1);
    }

    private PlaybackEvent createEvent(AudioDevice var1, int var2) {
        return new PlaybackEvent(this, var2, var1.getPosition());
    }

    public void setPlayBackListener(PlaybackListener var1) {
        this.listener = var1;
    }

    public PlaybackListener getPlayBackListener() {
        return this.listener;
    }

    public void stop() {
        this.close();
    }
}
