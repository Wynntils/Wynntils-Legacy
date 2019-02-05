/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

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

    public String getMd5() {
        return md5;
    }

    public boolean equals(String other) {
        return getMd5().equals(other);
    }

}
