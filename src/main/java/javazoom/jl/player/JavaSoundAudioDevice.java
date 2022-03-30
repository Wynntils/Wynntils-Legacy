/*
 *  * Copyright © Wynntils - 2022.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package javazoom.jl.player;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;

public class JavaSoundAudioDevice extends AudioDeviceBase {

    private SourceDataLine source = null;
    private AudioFormat fmt = null;
    private byte[] byteBuf = new byte[4096];

    float initialVolume = 0;

    public JavaSoundAudioDevice() {
    }

    protected void setAudioFormat(AudioFormat var1) {
        this.fmt = var1;
    }

    protected AudioFormat getAudioFormat() {
        if (this.fmt == null) {
            Decoder var1 = this.getDecoder();
            this.fmt = new AudioFormat((float)var1.getOutputFrequency(), 16, var1.getOutputChannels(), true, false);
        }

        return this.fmt;
    }

    protected Info getSourceLineInfo() {
        AudioFormat var1 = this.getAudioFormat();
        Info var2 = new Info(SourceDataLine.class, var1);
        return var2;
    }

    public void open(AudioFormat var1) throws JavaLayerException {
        if (!this.isOpen()) {
            this.setAudioFormat(var1);
            this.openImpl();
            this.setOpen(true);
        }
    }

    protected void openImpl() throws JavaLayerException {
    }

    protected void createSource() throws JavaLayerException {
        Object var1 = null;

        try {
            Line var2 = AudioSystem.getLine(this.getSourceLineInfo());
            if (var2 instanceof SourceDataLine) {
                this.source = (SourceDataLine)var2;
                this.source.open(this.fmt);
                setLineGain(initialVolume);

                this.source.start();
            }
        } catch (RuntimeException var3) {
            var1 = var3;
        } catch (LinkageError var4) {
            var1 = var4;
        } catch (LineUnavailableException var5) {
            var1 = var5;
        }

        if (this.source == null) {
            throw new JavaLayerException("cannot obtain source audio line", (Throwable)var1);
        }
    }

    public int millisecondsToBytes(AudioFormat var1, int var2) {
        return (int)((double)((float)var2 * var1.getSampleRate() * (float)var1.getChannels() * (float)var1.getSampleSizeInBits()) / 8000.0D);
    }

    protected void closeImpl() {
        if (this.source != null) {
            this.source.close();
        }

    }

    protected void writeImpl(short[] var1, int var2, int var3) throws JavaLayerException {
        if (this.source == null) {
            this.createSource();
        }

        byte[] var4 = this.toByteArray(var1, var2, var3);
        this.source.write(var4, 0, var3 * 2);
    }

    protected byte[] getByteArray(int var1) {
        if (this.byteBuf.length < var1) {
            this.byteBuf = new byte[var1 + 1024];
        }

        return this.byteBuf;
    }

    protected byte[] toByteArray(short[] var1, int var2, int var3) {
        byte[] var4 = this.getByteArray(var3 * 2);

        short var6;
        for (int var5 = 0; var3-- > 0; var4[var5++] = (byte)(var6 >>> 8)) {
            var6 = var1[var2++];
            var4[var5++] = (byte)var6;
        }

        return var4;
    }

    protected void flushImpl() {
        if (this.source != null) {
            this.source.drain();
        }

    }

    public int getPosition() {
        int var1 = 0;
        if (this.source != null) {
            var1 = (int)(this.source.getMicrosecondPosition() / 1000L);
        }

        return var1;
    }

    public void setLineGain(float gain) {
        if (source != null && source.isOpen()) {
            FloatControl volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
            float newGain = Math.min(Math.max(gain, volControl.getMinimum()), volControl.getMaximum());

            volControl.setValue(newGain);
        }
    }

    public void setInitialVolume(float initialVolume) {
        this.initialVolume = initialVolume;
    }

    public void test() throws JavaLayerException {
        try {
            this.open(new AudioFormat(22050.0F, 16, 1, true, false));
            short[] var1 = new short[2205];
            this.write(var1, 0, var1.length);
            this.flush();
            this.close();
        } catch (RuntimeException var2) {
            throw new JavaLayerException("Device test failed: " + var2);
        }
    }

}
