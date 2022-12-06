package com.wynntils.modules.richpresence.discordgamesdk;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.wynntils.modules.richpresence.discordgamesdk.enums.EDiscordActivityType;
import com.wynntils.modules.richpresence.discordgamesdk.options.DiscordGameSDKOptions;

import java.util.Arrays;
import java.util.List;

/**
 * <i>native declaration : line 283</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class DiscordActivity extends Structure implements DiscordGameSDKOptions {


    /**
     * @see EDiscordActivityType<br>
     *      C type : EDiscordActivityType
     */
    public EDiscordActivityType type;
    public long application_id;
    /** C type : char[128] */
    public byte[] name = new byte[128];
    /** C type : char[128] */
    public byte[] state = new byte[128];
    /** C type : char[128] */
    public byte[] details = new byte[128];
    /** C type : DiscordActivityTimestamps */
    public DiscordActivityTimestamps timestamps;
    /** C type : DiscordActivityAssets */
    public DiscordActivityAssets assets;
    /** C type : DiscordActivityParty */
    public DiscordActivityParty party;
    /** C type : DiscordActivitySecrets */
    public DiscordActivitySecrets secrets;
    public byte instance;

    public DiscordActivity() {
        super();
        setAutoWrite(true);
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("type", "application_id", "name", "state", "details", "timestamps", "assets", "party", "secrets", "instance");
    }

    public DiscordActivity(Pointer peer) {
        super(peer);
        setAutoWrite(true);
    }

    public static class ByReference extends DiscordActivity implements Structure.ByReference {

    };

    public static class ByValue extends DiscordActivity implements Structure.ByValue {

    };
}
