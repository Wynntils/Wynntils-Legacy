package com.wynntils.webapi.request.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MultipartFormDataPart implements IMultipartFormPart {

    private static final byte[] header1 = "Content-Disposition: form-data; name=\"".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] header2 = "\"\r\nContent-Type: ".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] header3 = "\r\n\r\n".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] header4 = "\"\r\n\r\n".getBytes(StandardCharsets.US_ASCII);

    byte[] name;
    byte[] contentType;
    byte[] data;

    public MultipartFormDataPart(String name, byte[] data) {
        this(name, data, null);
    }

    public MultipartFormDataPart(String name, byte[] data, String contentType) {
        this.name = name.getBytes(StandardCharsets.US_ASCII);
        this.data = data;
        this.contentType = contentType == null ? null : contentType.getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public int getLength() {
        if (contentType == null) {
            return
                43 +  // len('Content-Disposition: form-data; name=""\r\n\r\n')
                name.length +
                data.length
            ;
        }
        return
            59 +  // len('Content-Disposition: form-data; name=""\r\nContent-Type: \r\n\r\n')
            name.length +
            contentType.length +
            data.length
        ;
    }

    @Override
    public void write(OutputStream o) throws IOException {
        o.write(header1);
        o.write(name);
        if (contentType == null) {
            o.write(header4);
        } else {
            o.write(header2);
            o.write(contentType);
            o.write(header3);
        }
        o.write(data);
    }

}
