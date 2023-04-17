/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.map.instances;

import com.google.gson.JsonObject;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.map.overlays.objects.MapWaypointIcon;

import javax.annotation.Nullable;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.wynntils.core.utils.EncodingUtils.*;

public class WaypointProfile {

    String name;
    double x, y, z;
    int zoomNeeded;
    CustomColor color;
    WaypointType type;
    WaypointType group = null;

    boolean showBeaconBeam;

    public WaypointProfile(String name, double x, double y, double z, CustomColor color, WaypointType type, int zoomNeeded, boolean showBeaconBeam) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.type = type;
        this.zoomNeeded = zoomNeeded;
        this.showBeaconBeam = showBeaconBeam;
    }
    public WaypointProfile(String name, double x, double y, double z, CustomColor color, WaypointType type, int zoomNeeded) {
        this(name, x, y, z, color, type, zoomNeeded, false);
    }

    public String getName() {
        return name;
    }

    public CustomColor getColor() {
        return color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean shouldShowBeaconBeam() {
        return showBeaconBeam;
    }

    public int getZoomNeeded() {
        return zoomNeeded;
    }

    public void setZoomNeeded(int zoomNeeded) {
        this.zoomNeeded = zoomNeeded;
    }

    public WaypointType getType() {
        if (type == null) type = WaypointType.FLAG;
        return type;
    }

    public @Nullable WaypointType getGroup() {
        return group;
    }

    // Remember to save settings after calling
    public void setGroup(@Nullable WaypointType group) {
        this.group = group;
    }

    public static final byte currentFormat = 2;

    /**
     * Returns an upper bound of the length (in bytes) encoding this waypoint will be (with the given format)
     */
    public int encodeLength(byte format) {
        assert 0 <= format && format <= currentFormat;

        int sizeofInt = 4;
        // int sizeofLong = 8;
        int sizeofFloat = 4;
        int sizeofDouble = 8;
        int sizeofWaypointType = 1;  // Hopefully not more than 255 waypoint types

        switch (format) {
            case 0: break;
            case 1:
            case 2:
                sizeofInt = 5;
                // sizeofLong = 10;
        }

        return (
            sizeofInt + StringUtils.utf8Length(name) +  // Length prefixed name
            3 * sizeofDouble +  // x, y, z
            sizeofInt +  // zoomNeeded
            4 * sizeofFloat +  // colour r, g, b, a
            2 * sizeofWaypointType  // type and group
        );
    }

    public void encodeTo(byte format, ByteBuffer buf) {
        assert 0 <= format && format <= currentFormat;

        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);

        switch (format) {
            case 0: buf.putInt(name.length); break;
            case 1:
            case 2: encodeInt(name.length, buf); break;
        }

        buf.put(name);

        switch (format) {
            case 0:
                buf.putDouble(x); buf.putDouble(y); buf.putDouble(z);
                buf.putInt(zoomNeeded);
                buf.putFloat(color.r); buf.putFloat(color.g); buf.putFloat(color.b); buf.putFloat(color.a);
                break;
            case 1:
            case 2:
                encodeDouble(x, buf); encodeDouble(y, buf); encodeDouble(z, buf);
                switch (zoomNeeded) {
                    case 0: buf.put((byte) 0); break;
                    case MapWaypointIcon.ANY_ZOOM: buf.put((byte) 1); break;
                    case MapWaypointIcon.HIDDEN_ZOOM: buf.put((byte) 2); break;
                    default: buf.put((byte) -1); buf.putInt(zoomNeeded); break;
                }
                buf.putFloat(color.r); buf.putFloat(color.g); buf.putFloat(color.b); buf.putFloat(color.a);
                break;
        }

        buf.put((byte) type.ordinal());
        if (group == null) {
            buf.put((byte) 0xFF);
        } else {
            buf.put((byte) group.ordinal());
        }
        buf.put(showBeaconBeam ? (byte) 1 : (byte) 0);
    }

    public String encode(byte format) {
        assert 0 <= format && format <= currentFormat;

        ByteBuffer buf = ByteBuffer.allocateDirect(encodeLength(format));
        encodeTo(format, buf);
        byte[] encoded = new byte[buf.position()];
        buf.rewind();
        buf.get(encoded);
        return Base64.getEncoder().encodeToString(encoded);
    }

    public static void encodeTo(List<WaypointProfile> list, byte format, ByteBuffer buf) {
        assert 0 <= format && format <= currentFormat;

        switch (format) {
            case 0:
                buf.putInt(0);
                buf.putInt(list.size());
                break;
            case 1:
            case 2:
                buf.put(format);
                encodeInt(list.size(), buf);
                break;
        }

        for (WaypointProfile wp : list) {
            wp.encodeTo(format, buf);
        }
    }

    public static String encode(List<WaypointProfile> list, byte format) {
        assert 0 <= format && format <= currentFormat;

        int size = 4 + 4 + list.stream().mapToInt(wp -> wp.encodeLength(format)).sum();
        ByteBuffer buf = ByteBuffer.allocateDirect(size);
        encodeTo(list, format, buf);
        byte[] encoded = new byte[buf.position()];
        buf.rewind();
        buf.get(encoded);
        return Base64.getEncoder().encodeToString(encoded);
    }

    public void decode(byte format, String base64) throws IllegalArgumentException {
        assert 0 <= format && format <= currentFormat;

        decode(format, Base64.getDecoder().decode(base64));
    }

    public void decode(byte format, byte[] data) throws IllegalArgumentException {
        try {
            decode(format, ByteBuffer.wrap(data));
        } catch (BufferUnderflowException e) {
            throw new IllegalArgumentException("Invalid waypoint: Not enough bytes");
        }
    }

    public void decode(byte format, ByteBuffer buf) throws IllegalArgumentException, BufferUnderflowException {
        assert 0 <= format && format <= currentFormat;

        int nameSize = 0;
        switch (format) {
            case 0: nameSize = buf.getInt(); break;
            case 1:
            case 2: nameSize = decodeInt(buf); break;
        }
        if (nameSize < 0) {
            throw new IllegalArgumentException(String.format("Invalid waypoint (format %d)\\nName size is negative", format));
        }
        if (nameSize > 1024) {
            throw new IllegalArgumentException(String.format("Invalid waypoint (format %d)\\nName size is too large", format));
        }
        byte[] name = new byte[nameSize];
        buf.get(name);
        this.name = new String(name, StandardCharsets.UTF_8);

        float r = -1; float g = -1; float b = -1; float a = -1;

        switch (format) {
            case 0:
                this.x = buf.getDouble(); this.y = buf.getDouble(); this.z = buf.getDouble();
                this.zoomNeeded = buf.getInt();
                r = buf.getFloat(); g = buf.getFloat(); b = buf.getFloat(); a = buf.getFloat();
                break;
            case 1:
            case 2:
                this.x = decodeDouble(buf); this.y = decodeDouble(buf); this.z = decodeDouble(buf);
                byte zoomNeeded = buf.get();
                switch (zoomNeeded) {
                    case 0: this.zoomNeeded = 0; break;
                    case 1: this.zoomNeeded = MapWaypointIcon.ANY_ZOOM; break;
                    case 2: this.zoomNeeded = MapWaypointIcon.HIDDEN_ZOOM; break;
                    case -1: this.zoomNeeded = buf.getInt(); break;
                    default: throw new IllegalArgumentException(String.format("Invalid waypoint (format %s)\\nIllegal waypoint zoomNeeded", format));
                }
                r = buf.getFloat(); g = buf.getFloat(); b = buf.getFloat(); a = buf.getFloat();
                break;
        }

        if (!(0 <= r && r <= 1 && 0 <= g && g <= 1 && 0 <= b && b <= 1 && 0 <= a && a <= 1)) {
            throw new IllegalArgumentException(String.format("Invalid waypoint (format %d)\\nColour out of range", format));
        }
        this.color = new CustomColor(r, g, b, a);

        int type = Byte.toUnsignedInt(buf.get());

        if (type >= WaypointType.values().length) {
            throw new IllegalArgumentException(String.format("Invalid waypoint (format %s)\\nWaypoint type out of range", format));
        }
        this.type = WaypointType.values()[type];

        int group = Byte.toUnsignedInt(buf.get());
        if (group == 0xFF) {
            this.group = null;
        } else if (group >= WaypointType.values().length) {
            throw new IllegalArgumentException(String.format("Invalid waypoint (format %s)\\nWaypoint group out of range", format));
        } else {
            this.group = WaypointType.values()[group];
        }

        if (format == 2) {
            this.showBeaconBeam = buf.get() == 1;
        }
    }

    public static List<WaypointProfile> decode(String base64) throws IllegalArgumentException {
        if (base64 == null) throw new IllegalArgumentException("Invalid waypoint list\\nWas null");
        return decode(Base64.getDecoder().decode(base64));
    }

    public static List<WaypointProfile> decode(byte[] data) throws IllegalArgumentException {
        if (data == null) throw new IllegalArgumentException("Invalid waypoint list\\nWas null");
        ByteBuffer buf = ByteBuffer.wrap(data);
        List<WaypointProfile> result;
        try {
            result = decode(buf);
        } catch (BufferUnderflowException e) {
            throw new IllegalArgumentException("Invalid waypoint list\\nNot enough bytes");
        }
        if (buf.position() != data.length) {
            throw new IllegalArgumentException(String.format("Invalid waypoint list\\nFound extra %s bytes", data.length - buf.position()));
        }
        return result;
    }

    public static List<WaypointProfile> decode(ByteBuffer buf) throws IllegalArgumentException, BufferUnderflowException {
        byte format = buf.get();
        int uformat = Byte.toUnsignedInt(format);
        if (!(0 <= uformat && uformat <= (int) currentFormat)) {
            throw new IllegalArgumentException(String.format("Invalid waypoint format (Found: %s)", format));
        }

        if (uformat == 0) {
            // First format was an int instead of a byte
            buf.position(buf.position() + 3);
        }


        int size = -1;
        switch (format) {
            case 0: size = buf.getInt(); break;
            case 1:
            case 2: size = decodeInt(buf); break;
        }
        if (size < 0 || size > 8192) {
            throw new IllegalArgumentException("Invalid waypoint list size");
        }
        List<WaypointProfile> result = new ArrayList<>(size);
        while (size-- > 0) {
            WaypointProfile wp = new WaypointProfile(null, 0, 0, 0, null, null, 0, false);
            wp.decode(format, buf);
            result.add(wp);
        }
        return result;
    }

    public JsonObject toArtemisObject() {
        JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("color", color.toHexString());

        String zoomNeededString = "default";
        int zoomNeeded = getZoomNeeded();
        if (zoomNeeded == -1000) {
            zoomNeededString = "always";
        } else if (zoomNeeded == -1) {
            zoomNeededString = "hidden";
        }

        json.addProperty("visibility", zoomNeededString);

        JsonObject location = new JsonObject();
        location.addProperty("x", (int)x);
        location.addProperty("y", (int)y);
        location.addProperty("z", (int)z);

        json.add("location", location);

        json.addProperty("icon", type.artemisName);

        return json;
    }

    public enum WaypointType {

        FLAG("Flag", "flag"),
        DIAMOND("Diamond", "diamond"),
        SIGN("Sign", "sign"),
        STAR("Star", "star"),
        // Artemis doesn't have a waypoint type for this, but has wall :)
        TURRET("Turret", "wall"),
        LOOTCHEST_T4("Chest (T4)", "chestT4"),
        LOOTCHEST_T3("Chest (T3)", "chestT3"),
        LOOTCHEST_T2("Chest (T2)", "chestT2"),
        LOOTCHEST_T1("Chest (T1)", "chestT1"),
        FARMING("Farming", "farming"),
        FISHING("Fishing", "fishing"),
        MINING("Mining", "mining"),
        WOODCUTTING("Woodcutting", "woodcutting");

        private String displayName;
        private String artemisName;

        WaypointType(String displayName, String artemisName) {
            this.displayName = displayName;
            this.artemisName = artemisName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
