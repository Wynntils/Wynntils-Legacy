/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class ObjectHelper {

    /** byte[] objToByte
     * Converts {object} to a byte array
     *
     * @param object object to encode
     * @param compression should the result be ran through {{compress}}
     * @return byte array representation of object
     * @throws IOException .
     */
    public static byte[] objToByte(Object object, boolean compression) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
                objStream.writeObject(object);
                byte[] b = compression ? compress(byteStream.toByteArray()) : byteStream.toByteArray();
            objStream.close();
        byteStream.close();

        return b;
    }

    /** Object byteToObj
     * Converts {bytes} to an object
     *
     * @param bytes bytes to decode
     * @param compression should {bytes} be ran through {{decompress}}
     * @return Object result of decoded bytes
     * @throws IOException .
     * @throws ClassNotFoundException .
     * @throws DataFormatException .
     */
    public static Object byteToObj(byte[] bytes, boolean compression) throws IOException, ClassNotFoundException, DataFormatException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(compression ? decompress(bytes) : bytes);
            ObjectInputStream objStream = new ObjectInputStream(byteStream);
                Object o = objStream.readObject();
            objStream.close();
        byteStream.close();

        return o;
    }

    /** byte[] compress
     * Compresses {data}
     *
     * @param data byte array to be compressed
     * @return compressed version of {data}
     * @throws IOException .
     */
    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    /** byte[] decompress
     * Decompresses {data}
     *
     * @param data byte array to be compressed
     * @return decompressed version of {data}
     * @throws IOException .
     * @throws DataFormatException .
     */
    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    /** void byteToFile
     * Writes {bytes} to {saveFile}
     *
     * @param saveFile file to write upon(will overwrite!)
     * @param bytes bytes to write
     * @throws IOException .
     */
    public static void byteToFile(File saveFile, byte[] bytes) throws IOException{
        FileUtils.writeByteArrayToFile(saveFile,bytes);
    }

    /** byte[] fileToByte
     * Reads {file} into a byte array
     *
     * @param file file to read
     * @return file's contents
     * @throws IOException .
     */
    public static byte[] fileToByte(File file) throws IOException{
        return FileUtils.readFileToByteArray(file);
    }

    /** void objToFile
     * Writes an object to a file assuming the object and all its contents are either implementing Serializable, Externalizable or are primitives
     *
     * @param saveFile file to write the object to
     * @param object object to write in the file
     * @param compression should the written file be compressed
     * @throws IOException .
     */
    public static void objToFile(File saveFile, Object object, boolean compression) throws IOException {
        byteToFile(saveFile, objToByte(object, compression));
    }
    public static void objToFile(File saveFile, Object object) throws IOException { objToFile(saveFile,object,false); }

    /** Object fileToObj
     * Reads a file into an abstract Object(which can be casted)
     *
     * @param file file to read from
     * @param compression should the file be decompressed
     * @return {file}'s object
     * @throws IOException .
     * @throws DataFormatException .
     * @throws ClassNotFoundException .
     */
    public static Object fileToObj(File file, boolean compression) throws IOException, DataFormatException, ClassNotFoundException {
        return byteToObj(fileToByte(file),compression);
    }
    public static Object fileToObj(File file) throws ClassNotFoundException, IOException, DataFormatException { return fileToObj(file,false); }
}
