/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.request.multipart;

import java.io.IOException;
import java.io.OutputStream;

public interface IMultipartFormPart {

    int getLength();
    void write(OutputStream o) throws IOException;

}
