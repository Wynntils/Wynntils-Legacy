/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.request.multipart;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MultipartFormFilePart implements IMultipartFormPart {

    private static final byte[] header1 = "Content-Disposition: form-data; name=\"".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] header2 = "\"; filename=\"".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] header3 = "\"\r\nContent-Type: ".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] header4 = "\r\n\r\n".getBytes(StandardCharsets.US_ASCII);

    byte[] name;
    byte[] contentType;
    File f;

    public MultipartFormFilePart(String name, File f) {
        this(name, f, "text/plain");
    }

    public MultipartFormFilePart(String name, File f, String contentType) {
        this.name = name.getBytes(StandardCharsets.US_ASCII);
        this.contentType = contentType.getBytes(StandardCharsets.US_ASCII);
        this.f = f;
    }

    @Override
    public int getLength() {
        return
            72 +  // len('Content-Disposition: form-data; name=""; filename=""\r\nContent-Type: \r\n\r\n')
            name.length +
            contentType.length +
            f.getName().length() +
            (int) f.length()
        ;
    }

    @Override
    public void write(OutputStream o) throws IOException {
        o.write(header1);
        o.write(name);
        o.write(header2);
        o.write(f.getName().getBytes(StandardCharsets.US_ASCII));
        o.write(header3);
        o.write(contentType);
        o.write(header4);
        Files.copy(f.toPath(), o);
    }

}
