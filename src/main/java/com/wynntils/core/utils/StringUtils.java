/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.utils;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.helpers.MD5Verification;
import com.wynntils.modules.utilities.managers.LevelingManager;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

public class StringUtils {

    private static final Pattern STX_PATTERN = Pattern.compile("(\\.?\\d+\\.?\\d*)\\s*(s|stx|stacks)");
    private static final Pattern LE_PATTERN = Pattern.compile("(\\.?\\d+\\.?\\d*)\\s*(l|le)");
    private static final Pattern EB_PATTERN = Pattern.compile("(\\.?\\d+\\.?\\d*)\\s*(b|eb)");
    private static final Pattern E_PATTERN = Pattern.compile("(\\d+)($|\\s|\\s*e|\\s*em)(?![^\\d\\s-])");
    private static final Pattern RAW_PRICE_PATTERN = Pattern.compile("\\d+");

    /**
     * Removes the characters 'À' ('\u00c0') and '\u058e' that is sometimes added in Wynn APIs and
     * replaces '\u2019' (RIGHT SINGLE QUOTATION MARK) with '\'' (And trims)
     *
     * @param input string
     * @return the string without these two chars
     */
    public static String normalizeBadString(String input) {
        if (input == null) return "";
        return input
                .trim()
                .replace("ÀÀÀ", " ").replace("À", "").replace("\u058e", "")
                .replace('\u2019', '\'')
                .trim();
    }

    public static String parseGrindTime(int count) {
        if(count == 0) {
            if(LevelingManager.getXpPerMinute() == 0) {
                return "∞";
            }
            else {
                return "Less than a minute";
            }
        }
        if (count < 60) {
            return count + "s";
        } else if (count > 86400) {
            return count / 86400 + "d";
        } else if (count > 3600) {
            return count / 3600 + "h";
        } else {
            return count / 60 + "m";
        }
    }

    public static String firstCharToUpper(String[] array) {
        StringBuilder result = new StringBuilder();

        result.append(array[0].toLowerCase());

        for (int i = 1; i < array.length; i++) {
            result.append(capitalizeFirst(array[i]));
        }

        return result.toString();
    }

    public static String capitalizeFirst(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    public static String capitalizeFirsts(String input) {
        StringBuilder builder = new StringBuilder();
        String[] split = input.split(" ");

        for (String s : split) {
            builder.append(capitalizeFirst(s));
            builder.append(" ");
        }

        String result = builder.toString();

        return result.substring(0, result.length() - 1);
    }

    // ported from a really really old C# code because im lazy, don't judge -SHCM
    public static String getCutString(String inputIN, String startIN, String endIN, boolean keepStartAndEndIN) {
        StringBuilder returning = new StringBuilder();
        StringBuilder read = new StringBuilder();
        boolean collecting = false;

        for (char chr : inputIN.toCharArray())
            if (collecting) {
                returning.append(chr);
                if (returning.toString().endsWith(endIN)) {
                    return (keepStartAndEndIN ? (startIN + returning) : returning.toString().replace(endIN, ""));
                }
            } else {
                read.append(chr);
                if (read.toString().endsWith(startIN))
                    collecting = true;
            }
        return "";
    }

    public static String toMD5(String msg) {
        return new MD5Verification(msg.getBytes(StandardCharsets.UTF_8)).getMd5();
    }

    public static String[] wrapText(String s, int max) {
        String[] stringArray = s.split(" ");
        StringBuilder result = new StringBuilder();
        int length = 0;

        for (String string : stringArray) {
            String[] lines = string.split("\\\\n", -1);
            for (int i = 0; i < lines.length; i ++) {
                String line = lines[i];
                if (i > 0 || length + line.length() >= max) {
                    result.append('\n');
                    length = 0;
                }
                if (line.length() > 0) {
                    result.append(line).append(' ');
                    length += line.length() + 1;  // +1 for the space following
                }
            }
        }

        return result.toString().split("\n");
    }

    public static String[] wrapTextBySize(String s, int maxPixels) {
        SmartFontRenderer renderer = ScreenRenderer.fontRenderer;
        int spaceSize = renderer.getStringWidth(" ");

        String[] stringArray = s.split(" ");
        StringBuilder result = new StringBuilder();
        int length = 0;

        for (String string : stringArray) {
            String[] lines = string.split("\\\\n", -1);
            for (int i = 0; i < lines.length; i ++) {
                String line = lines[i];
                if (i > 0 || length + renderer.getStringWidth(line) >= maxPixels) {
                    result.append('\n');
                    length = 0;
                }
                if (line.length() > 0) {
                    result.append(line).append(' ');
                    length += renderer.getStringWidth(line) + spaceSize;
                }
            }
        }

        return result.toString().split("\n");
    }

    private static final Map<String, CustomColor> registeredColors = new HashMap<>();
    private static final Map<Integer, CustomColor> registeredHexColors = new HashMap<>();

    /**
     * Generates a Color based in the input string
     * The color will be always the same if the string is the same
     * (The alpha value will not be stable, so remember to setA())
     *
     * @param input the input stream
     * @return the color
     */
    public static CustomColor colorFromString(String input) {
        if (registeredColors.containsKey(input)) return registeredColors.get(input);

        CRC32 crc32 = new CRC32();
        crc32.update(input.getBytes(StandardCharsets.UTF_8));

        CustomColor color = CustomColor.fromInt(((int) crc32.getValue()) & 0xFFFFFF, 1);
        registeredColors.put(input, color);

        return color;
    }

    public static CustomColor colorFromHex(String hex) {
        int rgb = Integer.parseInt(hex.substring(1, 7), 16);
        if (registeredHexColors.containsKey(rgb)) return registeredHexColors.get(rgb);

        CustomColor color = CustomColor.fromInt(rgb, 1);
        registeredHexColors.put(rgb, color);

        return color;
    }

    public static String millisToString(long duration) {
        long millis = duration % 1000,
                second = (duration / 1000) % 60,
                minute = (duration / (1000 * 60)) % 60,
                hour = (duration / (1000 * 60 * 60));

        return String.format("%02d:%02d:%02d.%03d", hour, minute, second, millis);
    }

    public static String millisToLongString(long duration) {
        long minute = (duration / (1000 * 60)) % 60,
                hour = (duration / (1000 * 60 * 60));

        if (minute == 0 && hour == 0) return "Seconds Ago";

        return (hour != 0 ? hour + (hour != 1 ? " hours" : " hour") : "")
                + (minute != 0 && hour != 0 ? " and " : "")
                + (minute != 0 ? minute + (minute != 1 ? " minutes" : " minute") : "");
    }

    public static String timeLeft(long duration) {
        long minute = (duration / (1000 * 60)) % 60,
                second = (duration / 1000) % 60;

        return String.format("%02d:%02d", minute, second);
    }

    /**
     * @return `true` if `c` is a valid Unicode code point (in [0, 0x10FFFF] and not a surrogate)
     */
    public static boolean isValidCodePoint(int c) {
        /* low surrogates */             /* high surrogates */
        return 0 <= c && c <= 0x10FFFF && !(0xD800 <= c && c <= 0xDBFF) && !(0xDC00 <= c && c <= 0xDFFF);
    }

    private static final Pattern numberRegex = Pattern.compile("0|-?[1-9][0-9]*");

    /**
     * @return `true` if `s` is an integer string and can fit in an `int`
     */
    public static boolean isValidInteger(String s) {
        if (s == null || s.length() > 11 || !numberRegex.matcher(s).matches()) return false;
        if (s.length() < 10) return true;
        long parsed = Long.parseLong(s);
        return (int) parsed == parsed;
    }

    /**
     * @return `true` if `s` is an integer string and can fit in a `long`
     */
    public static boolean isValidLong(String s) {
        if (s == null || s.length() > 20 || !numberRegex.matcher(s).matches()) return false;
        if (s.length() < 19) return true;
        try {
            Long.parseLong(s);  // Could overflow
        } catch (NumberFormatException ignored) {
            return false;
        }
        return true;
    }

    /**
     * @return `s.getBytes("UTF-8").length`, but without encoding the string
     */
    public static int utf8Length(String s) {
        if (s == null) return 0;
        return s.codePoints().map(c -> c < 0x80 ? 1 : c < 0x800 ? 2 : c < 0x10000 ? 3 : 4).sum();
    }


    public static UUID uuidFromString(String s) {
        if (s.contains("-")) return UUID.fromString(s);
        if (s.length() != 32) throw new IllegalArgumentException("Invalid UUID string: " + s);

        try {
            return new UUID(Long.parseUnsignedLong(s.substring(0, 16), 16), Long.parseUnsignedLong(s.substring(16, 32), 16));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid UUID string: " + s);
        }
    }

    public static boolean isWynnic(int c) {
        return (
                (0x249C <= c && c <= 0x24B5) ||
                        (0x2474 <= c && c <= 0x247C) ||
                        (0x247D <= c && c <= 0x247F) ||
                        (0xFF10 <= c && c <= 0xFF12)
        );
    }

    public static boolean hasWynnic(String text) {
        return text.chars().anyMatch(StringUtils::isWynnic);
    }

    public static String translateCharacterFromWynnic(char wynnic) {
        return translateCharacterFromWynnic((int) wynnic);  // convert to code point
    }

    public static String translateCharacterFromWynnic(int wynnic) {
        if (0x249C <= wynnic && wynnic <= 0x24B5) {
            return Character.toString((char) ((wynnic) - 9275));
        } else if (0x2474 <= wynnic && wynnic <= 0x247C) {
            return Character.toString((char) ((wynnic) - 9283));
        }

        switch (wynnic) {
            case 0x247D:
                return "10";
            case 0x247E:
                return "50";
            case 0x247F:
                return "100";
            case 0xFF10:
                return ".";
            case 0xFF11:
                return "!";
            case 0xFF12:
                return "?";
            default:
                return "";
        }
    }

    public static int translateNumberFromWynnic(String wynnic) {
        int result = 0;
        int maxNumber = 3;
        int numbersRemaining = 3;
        PeekingIterator<Integer> characters = Iterators.peekingIterator(wynnic.chars().iterator());
        while (characters.hasNext()) {
            int character = characters.next();
            switch (character) {
                case 0x247F:
                    if (maxNumber < 3 || numbersRemaining == 0) {
                        return 0;
                    }

                    result += 100;
                    numbersRemaining--;
                    break;
                case 0x247E:
                    if (maxNumber < 3) {
                        return 0;
                    }

                    result += 50;

                    numbersRemaining = 3;
                    maxNumber = 2;
                    break;
                case 0x247D:
                    if (maxNumber < 2 || (maxNumber == 2 && numbersRemaining == 0)) {
                        return 0;
                    }

                    if (maxNumber > 2) {
                        numbersRemaining = 3;
                    }

                    if (characters.hasNext()) {
                        switch (characters.peek()) {
                            case 0x247F:
                                result += 90;
                                characters.next();
                                maxNumber = 1;
                                numbersRemaining = 1;
                                break;
                            case 0x247E:
                                result += 40;
                                characters.next();
                                maxNumber = 1;
                                numbersRemaining = 1;
                                break;
                            default:
                                result += 10;
                                numbersRemaining--;
                        }
                    } else {
                        result += 10;
                        numbersRemaining--;
                    }
                    break;
                default:
                    if (0x2474 <= character && character <= 0x247C) {
                        if (maxNumber == 1 && numbersRemaining == 0) {
                            return 0;
                        }

                        result += character - 0x2473;
                        maxNumber = 1;
                        numbersRemaining--;
                    }
            }
        }
        return result;
    }

    public static boolean isWynnicNumber(char character) {
        return (0x2474 <= character && character <= 0x247C)
                || (0x247D <= character && character <= 0x247F);
    }

    public static boolean isGavellian(int c) {
        return 0x24D0 <= c && c <= 0x24E9;
    }

    public static boolean hasGavellian(String text) {
        return text.chars().anyMatch(StringUtils::isGavellian);
    }

    public static String translateCharacterFromGavellian(char gavellian) {
        return translateCharacterFromGavellian((int) gavellian); // convert to code point
    }

    public static String translateCharacterFromGavellian(int gavellian) {
        return Character.toString((char) ((gavellian) - 9327));
    }

    public static String convertEmeraldPrice(String input) {
        Matcher rawMatcher = RAW_PRICE_PATTERN.matcher(input);
        if (rawMatcher.matches()) return "";

        input = input.toLowerCase();
        long emeralds = 0;

        try {
            // stx
            Matcher stxMatcher = STX_PATTERN.matcher(input);
            while (stxMatcher.find()) {
                emeralds += (long) (Double.parseDouble(stxMatcher.group(1)) * 64 * 64 * 64);
            }

            // le
            Matcher leMatcher = LE_PATTERN.matcher(input);
            while (leMatcher.find()) {
                emeralds += (long) (Double.parseDouble(leMatcher.group(1)) * 64 * 64);
            }

            // eb
            Matcher ebMatcher = EB_PATTERN.matcher(input);
            while (ebMatcher.find()) {
                emeralds += (long) (Double.parseDouble(ebMatcher.group(1)) * 64);
            }

            // standard numbers/emeralds
            Matcher eMatcher = E_PATTERN.matcher(input);
            while (eMatcher.find()) {
                emeralds += Long.parseLong(eMatcher.group(1));
            }

            // account for tax if flagged
            if (input.contains("-t")) {
                emeralds = Math.round(emeralds / 1.05F);
            }
        } catch (NumberFormatException e) {
            return ""; //
        }

        return (emeralds > 0) ? String.valueOf(emeralds) : "";
    }

    /**
     * @param count the number
     * @return The formatted shortened string
     */
    public static String integerToShortString(int count) {
        if (count < 1000) return Integer.toString(count);
        int exp = (int) (Math.log(count) / Math.log(1000));
        DecimalFormat format = new DecimalFormat("0.#");
        String value = format.format(count / Math.pow(1000, exp));
        return String.format("%s%c", value, "kMBTPE".charAt(exp - 1));
    }

    public static boolean fuzzyMatch(String toCheck, String searchText) {
        int i = 0, j = 0;
        char[] toCheckArray = toCheck.toCharArray();
        for (char c : searchText.toCharArray()) {
            while (i < toCheck.length()) {
                if (c == toCheckArray[i]) {
                    i++;
                    j++;
                    break;
                }
                i++;
            }
        }

        return j == searchText.length();
    }

    public static String quoteIfContainsSpace(String str) {
        return str.indexOf(' ') == -1 ? str : ('"' + str + '"');
    }

    private static final TreeMap<Integer, String> treemap = new TreeMap<Integer, String>();
    static {
        treemap.put(1000, "M");
        treemap.put(900, "CM");
        treemap.put(500, "D");
        treemap.put(400, "CD");
        treemap.put(100, "C");
        treemap.put(90, "XC");
        treemap.put(50, "L");
        treemap.put(40, "XL");
        treemap.put(10, "X");
        treemap.put(9, "IX");
        treemap.put(5, "V");
        treemap.put(4, "IV");
        treemap.put(1, "I");
    }

    public static String integerToRoman(int number) {
        int l = treemap.floorKey(number);
        if (number == l) {
            return treemap.get(number);
        }
        return treemap.get(l) + integerToRoman(number - l);
    }

    public static int parseIntOr(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Pattern compileCCRegex(String regex) {
        return Pattern.compile(regex.replace("§", "(?:§[0-9a-fklmnor])*"));
    }
}
