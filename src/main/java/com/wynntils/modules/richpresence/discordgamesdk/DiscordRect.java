package com.wynntils.modules.richpresence.discordgamesdk;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.wynntils.modules.richpresence.discordgamesdk.options.DiscordGameSDKOptions;

import java.util.Arrays;
import java.util.List;

/**
 * <i>native declaration : line 324</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class DiscordRect extends Structure implements DiscordGameSDKOptions {

    public int left;
    public int top;
    public int right;
    public int bottom;

    public DiscordRect() {
        super();
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("left", "top", "right", "bottom");
    }

    public DiscordRect(int left, int top, int right, int bottom) {
        super();
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public DiscordRect(Pointer peer) {
        super(peer);
    }

    public static class ByReference extends DiscordRect implements Structure.ByReference {

    };

    public static class ByValue extends DiscordRect implements Structure.ByValue {

    };
}