/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.Utils;

import javax.annotation.Nullable;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class WaypointProfile {

    String name;
    double x, y, z;
    int zoomNeeded;
    CustomColor color;
    WaypointType type;
    WaypointType group = null;

    public WaypointProfile(String name, double x, double y, double z, CustomColor color, WaypointType type, int zoomNeeded) {
        this.name = name; this.x = x; this.y = y; this.z = z; this.color = color; this.type = type; this.zoomNeeded = zoomNeeded;
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

    public int getZoomNeeded() {
        return zoomNeeded;
    }

    public WaypointType getType() {
        return type;
    }

    public @Nullable WaypointType getGroup() {
        return group;
    }

    // Remember to save settings after calling
    public void setGroup(@Nullable WaypointType group) {
        this.group = group;
    }

    public static final int currentFormat = 0;

    public int encodeLength(int format) {
        assert format == 0;

        final int sizeofInt = 4;
        final int sizeofFloat = 4;
        final int sizeofDouble = 8;
        final int sizeofWaypointType = 1;  // Hopefully not more than 255 waypoint types

        return (
            sizeofInt + Utils.utf8Length(name) +  // Length prefixed name
            3 * sizeofDouble +  // x, y, z
            sizeofInt +  // zoomNeeded
            4 * sizeofFloat +  // colour r, g, b, a
            2 * sizeofWaypointType  // type and group
        );
    }

    public void encodeTo(int format, ByteBuffer buf) {
        assert format == 0;

        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        buf.putInt(name.length);
        buf.put(name);

        buf.putDouble(x);
        buf.putDouble(y);
        buf.putDouble(z);

        buf.putInt(zoomNeeded);

        buf.putFloat(color.r);
        buf.putFloat(color.g);
        buf.putFloat(color.b);
        buf.putFloat(color.a);

        buf.put((byte) type.ordinal());
        if (group == null) {
            buf.put((byte) 0xFF);
        } else {
            buf.put((byte) group.ordinal());
        }
    }

    public String encode(int format) {
        assert format == 0;

        ByteBuffer buf = ByteBuffer.allocate(encodeLength(format));
        encodeTo(format, buf);
        return Base64.getEncoder().encodeToString(buf.array());
    }

    public static void encodeTo(List<WaypointProfile> list, int format, ByteBuffer buf) {
        assert format == 0;

        buf.putInt(format);
        buf.putInt(list.size());
        for (WaypointProfile wp : list) {
            wp.encodeTo(format, buf);
        }
    }

    public static String encode(List<WaypointProfile> list, int format) {
        assert format == 0;

        int size = 4 + 4 + list.stream().mapToInt(wp -> wp.encodeLength(format)).sum();
        ByteBuffer buf = ByteBuffer.allocate(size);
        encodeTo(list, format, buf);
        return Base64.getEncoder().encodeToString(buf.array());
    }

    public void decode(int format, String base64) throws IllegalArgumentException {
        assert format == 0;

        decode(format, Base64.getDecoder().decode(base64));
    }

    public void decode(int format, byte[] data) throws IllegalArgumentException {
        try {
            decode(format, ByteBuffer.wrap(data));
        } catch (BufferUnderflowException e) {
            throw new IllegalArgumentException("Invalid waypoint: Not enough bytes");
        }
    }

    public void decode(int format, ByteBuffer buf) throws IllegalArgumentException, BufferUnderflowException {
        assert format == 0;

        int nameSize = buf.getInt();
        if (nameSize < 0) {
            throw new IllegalArgumentException(String.format("Invalid waypoint (format %d)\\nName size is negative", format));
        }
        if (nameSize > 1024) {
            throw new IllegalArgumentException(String.format("Invalid waypoint (format %d)\\nName size is too large", format));
        }
        byte[] name = new byte[nameSize];
        buf.get(name);
        this.name = new String(name, StandardCharsets.UTF_8);

        this.x = buf.getDouble();
        this.y = buf.getDouble();
        this.z = buf.getDouble();

        this.zoomNeeded = buf.getInt();

        float r = buf.getFloat();
        float g = buf.getFloat();
        float b = buf.getFloat();
        float a = buf.getFloat();
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
    }

    public static ArrayList<WaypointProfile> decode(String base64) throws IllegalArgumentException {
        return decode(Base64.getDecoder().decode(base64));
    }

    public static ArrayList<WaypointProfile> decode(byte[] data) throws IllegalArgumentException {
        ByteBuffer buf = ByteBuffer.wrap(data);
        ArrayList<WaypointProfile> result;
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

    public static ArrayList<WaypointProfile> decode(ByteBuffer buf) throws IllegalArgumentException, BufferUnderflowException {
        int format = buf.getInt();
        if (!(0 <= format && format <= currentFormat)) {
            throw new IllegalArgumentException(String.format("Invalid waypoint format (Found: %s)", format));
        }
        assert format == 0;
        int size = buf.getInt();
        if (size < 0 || size > 1024) {
            throw new IllegalArgumentException("Invalid waypoint list size");
        }
        ArrayList<WaypointProfile> result = new ArrayList<>(size);
        while (size-- > 0) {
            WaypointProfile wp = new WaypointProfile(null, 0, 0, 0, null, null, 0);
            wp.decode(format, buf);
            result.add(wp);
        }
        return result;
    }

    public enum WaypointType {

        FLAG("Flag"),
        DIAMOND("Diamond"),
        SIGN("Sign"),
        STAR("Star"),
        TURRET("Turret"),
        LOOTCHEST_T4("Chest (T4)"),
        LOOTCHEST_T3("Chest (T3"),
        LOOTCHEST_T2("Chest (T2)"),
        LOOTCHEST_T1("Chest (T1)");

        private String displayName;

        WaypointType(String displayName){
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

    }
}
