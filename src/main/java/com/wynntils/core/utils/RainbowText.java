package com.wynntils.core.utils;

import net.minecraft.client.Minecraft;

public class RainbowText {

    private static final String[] colors = new String[] {"§c", "§6", "§e", "§a", "§b", "§9", "§d"};

    /*

        This math was token from Avaritia code.
        Special thanks for Morpheus1101 and SpitefulFox
        Avaritia Repo: https://github.com/Morpheus1101/Avaritia

     */
    public static String makeRainbow(String input, boolean bold) {
        StringBuilder sb = new StringBuilder();

        int offset = (int) Math.floor(Minecraft.getSystemTime() / 80.0) % colors.length;

        for(int i = 0; i < input.length(); i++) {
            int color = (i + colors.length - offset) % colors.length;

            sb.append(colors[color]);
            if(bold) sb.append("§l");
            sb.append(input.charAt(i));
        }

        return sb.toString();
    }

}
