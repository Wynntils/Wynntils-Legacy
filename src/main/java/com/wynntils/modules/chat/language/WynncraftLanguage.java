/**
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.chat.language;

import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface WynncraftLanguage {

    public static final Pattern WYNNIC_NUMBERS = Pattern.compile("[\u2474-\u247F0-9]*$");

    public static final WynncraftLanguage NORMAL = (string, character) -> new Pair<>(string, character);
    public static final WynncraftLanguage WYNNIC = (string, character) -> {
        if ('a' <= character && character <= 'z') {
            character = (char) ((character) + 0x243B);
        } else if ('A' <= character && character <= 'Z') {
            character = (char) ((character) + 0x245B);
        } else if (character == '.') {
            character = '０';
        } else if (character == '!') {
            character = '１';
        } else if (character == '?') {
            character = '２';
        } else if ('0' <= character && character <= '9') {
            Matcher matcher = WYNNIC_NUMBERS.matcher(string);
            matcher.find();
            String originalNumber = matcher.group();
            boolean wynnic = false;
            boolean english = false;
            for (char digit : originalNumber.toCharArray()) {
                if ('\u2474' <= digit && digit <= '\u247F' && !english) {
                    wynnic = true;
                } else if ('0' <= digit && digit <= '9' && !wynnic) {
                    english = true;
                } else {
                    return new Pair<>(string, character);
                }
            }
            int number = 0;
            if (english) {
                try {
                    number = Integer.parseInt(originalNumber);
                } catch (NumberFormatException ex) {
                    return new Pair<>(string, character);
                }
            } else {
                number = StringUtils.translateNumberFromWynnic(originalNumber);
            }
            if (number >= 400) {
                return new Pair<>(string, character);
            }
            number *= 10;
            number += Integer.parseInt(String.valueOf(character));
            string = WYNNIC_NUMBERS.matcher(string).replaceAll("");
            if (character == '.' || number >= 400) {
                character = '\0';
                string += number;
                return new Pair<>(string, character);
            }

            if (1 <= number && number <= 9) {
                string += (char) (number + 0x2473);
            } else if (number == 10 || number == 50 || number == 100) {
                switch (number) {
                    case 10:
                        string += '⑽';
                        break;
                    case 50:
                        string += '⑾';
                        break;
                    case 100:
                        string += '⑿';
                        break;
                }
            } else if (1 <= number && number <= 399) {
                StringBuilder newNumber = new StringBuilder(string);
                int hundreds = number / 100;
                for (int hundred = 1; hundred <= hundreds; hundred++) {
                    newNumber.append('⑿');
                }

                int tens = (number % 100) / 10;
                if (1 <= tens && tens <= 3) {
                    for (int ten = 1; ten <= tens; ten++) {
                        newNumber.append('⑽');
                    }
                } else if (4 == tens) {
                    newNumber.append("⑽⑾");
                } else if (5 <= tens && tens <= 8) {
                    newNumber.append('⑾');
                    for (int ten = 1; ten <= tens - 5; ten++) {
                        newNumber.append('⑽');
                    }
                } else if (9 == tens) {
                    newNumber.append("⑽⑿");
                }

                int ones = number % 10;
                if (1 <= ones) {
                    newNumber.append((char) (ones + 0x2473));
                }
                string = newNumber.toString();
            }

            character = '\0';
        } else if (character == '\u0008') { // backspace
            Matcher matcher = WYNNIC_NUMBERS.matcher(string);
            matcher.find();
            String originalNumber = matcher.group();
            boolean wynnic = false;
            boolean english = false;
            for (char digit : originalNumber.toCharArray()) {
                if ('\u2474' <= digit && digit <= '\u247F' && !english) {
                    wynnic = true;
                } else if ('0' <= digit && digit <= '9' && !wynnic) {
                    english = true;
                } else {
                    return new Pair<>(string, character);
                }
            }
            int number = 0;
            if (!wynnic) {
                return new Pair<>(string, character);
            }
            number = StringUtils.translateNumberFromWynnic(originalNumber);
            if (number >= 400) {
                return new Pair<>(string, character);
            }
            number /= 10;
            string = WYNNIC_NUMBERS.matcher(string).replaceAll("");

            character = '\0';

            if (1 <= number && number <= 9) {
                string += (char) (number + 0x2473);
            } else if (number == 10 || number == 50 || number == 100) {
                switch (number) {
                    case 10:
                        string += '⑽';
                        break;
                    case 50:
                        string += '⑾';
                        break;
                    case 100:
                        string += '⑿';
                        break;
                }
            } else if (1 <= number && number <= 399) {
                StringBuilder newNumber = new StringBuilder(string);
                int hundreds = number / 100;
                for (int hundred = 1; hundred <= hundreds; hundred++) {
                    newNumber.append('⑿');
                }

                int tens = (number % 100) / 10;
                if (1 <= tens && tens <= 3) {
                    for (int ten = 1; ten <= tens; ten++) {
                        newNumber.append('⑽');
                    }
                } else if (4 == tens) {
                    newNumber.append("⑽⑾");
                } else if (5 <= tens && tens <= 8) {
                    newNumber.append('⑾');
                    for (int ten = 1; ten <= tens - 5; ten++) {
                        newNumber.append('⑽');
                    }
                } else if (9 == tens) {
                    newNumber.append("⑽⑿");
                }

                int ones = number % 10;
                if (1 <= ones) {
                    newNumber.append((char) (ones + 0x2473));
                }
                string = newNumber.toString();
            }
        }

        return new Pair<>(string, character);
    };

    public Pair<String, Character> replace(String string, char character);
}
