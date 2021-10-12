/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.helpers;

import com.wynntils.McIf;
import java.lang.Math;

public class AnimatedText {

    private static final String[] colors = new String[] {"§c", "§6", "§e", "§a", "§b", "§9", "§d"};

    /*
     * This math was taken from Avaritia code.
     * Special thanks for Morpheus1101 and SpitefulFox
     * Avaritia Repo: https://github.com/Morpheus1101/Avaritia
     */
    public static String makeRainbow(String input, boolean bold) {
        StringBuilder sb = new StringBuilder();

        int offset = (int) Math.floor(McIf.getSystemTime() / 80.0) % colors.length;

        for (int i = 0; i < input.length(); i++) {
            int color = (i + colors.length - offset) % colors.length;

            sb.append(colors[color]);
            if (bold) sb.append("§l");
            sb.append(input.charAt(i));
        }

        return sb.toString();
    }

    public static String makeDefective(String input, boolean bold) {
        StringBuilder sb = new StringBuilder();
        String resetString = "§r§4";
        if (bold) {resetString += "§l";}

        // obfuscationChance repesents percent chance to obfuscate a single character
        // Increase for more obfuscation & vice versa
        float obfuscationChance = 0.2f;
        boolean obfuscated = false;

        sb.append(resetString); // Initial reset

        // "Defective"
        for (char c : "Defective ".toCharArray()) {
             if (Math.random() < obfuscationChance && !obfuscated) {
                 sb.append("§k");
                 obfuscated = true;
             } else if (obfuscated) {
                 sb.append(resetString);
                 obfuscated = false;
             }
                sb.append(c);
        }

        // Actual item name
        for (char c : input.toCharArray()) {
            if (Math.random() < obfuscationChance && !obfuscated) {
                sb.append("§k");
                obfuscated = true;
            } else if (obfuscated) {
                sb.append(resetString);
                obfuscated = false;
            }
            sb.append(c);
        }

        return sb.toString();
    }

}
