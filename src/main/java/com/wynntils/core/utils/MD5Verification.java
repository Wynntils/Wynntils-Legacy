/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.regex.Pattern;

public class MD5Verification {

    String md5;

    public MD5Verification(File f) {
        try{
            InputStream fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];
            MessageDigest md = MessageDigest.getInstance("MD5");
            int numRead = fis.read(buffer);

            while (numRead != -1) {
                if(numRead > 0) {
                    md.update(buffer, 0, numRead);
                }
                numRead = fis.read(buffer);
            }

            fis.close();

            byte[] result = md.digest();

            StringBuilder rr = new StringBuilder();
            for (byte b : result) {
                rr.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            md5 = rr.toString();
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    public MD5Verification(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            byte[] result = md.digest();

            StringBuilder rr = new StringBuilder();
            for (byte b : result) {
                rr.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            md5 = rr.toString();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public String getMd5() {
        return md5;
    }

    public boolean equals(String other) {
        return getMd5() != null && getMd5().equalsIgnoreCase(other);
    }

    private static final Pattern md5Regex = Pattern.compile("^[0-9a-fA-F]{32}$");

    public static boolean isMd5Digest(String s) {
        return s != null && s.length() == 32 && md5Regex.matcher(s).matches();
    }

}
