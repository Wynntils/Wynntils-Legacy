package com.wynntils.modules.richpresence.discordgamesdk;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.wynntils.modules.richpresence.discordgamesdk.enums.EDiscordInputModeType;
import com.wynntils.modules.richpresence.discordgamesdk.options.DiscordGameSDKOptions;

import java.util.Arrays;
import java.util.List;

/**
 * <i>native declaration : line 355</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class DiscordInputMode extends Structure implements DiscordGameSDKOptions {

    /**
     * @see EDiscordInputModeType<br>
     *      C type : EDiscordInputModeType
     */
    public EDiscordInputModeType type;
    /** C type : char[256] */
    public byte[] shortcut = new byte[256];

    public DiscordInputMode() {
        super();
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("type", "shortcut");
    }

    /**
     * @param type     @see EDiscordInputModeType<br>
     *                 C type : EDiscordInputModeType<br>
     * @param shortcut C type : char[256]
     */
    public DiscordInputMode(EDiscordInputModeType type, byte shortcut[]) {
        super();
        this.type = type;
        if ((shortcut.length != this.shortcut.length))
            throw new IllegalArgumentException("Wrong array size !");
        this.shortcut = shortcut;
    }

    public DiscordInputMode(Pointer peer) {
        super(peer);
    }

    public static class ByReference extends DiscordInputMode implements Structure.ByReference {

    };

    public static class ByValue extends DiscordInputMode implements Structure.ByValue {

    };
}
