package cf.wynntils.core.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ObjectHelper {
    public static byte[] objToByte(Object object, boolean compression) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
                objStream.writeObject(object);
                byte[] b = compression ? compress(byteStream.toByteArray()) : byteStream.toByteArray();
            objStream.close();
        byteStream.close();

        return b;
    }

    public static Object byteToObj(byte[] bytes, boolean compression) throws IOException, ClassNotFoundException, DataFormatException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(compression ? decompress(bytes) : bytes);
            ObjectInputStream objStream = new ObjectInputStream(byteStream);
                Object o = objStream.readObject();
            objStream.close();
        byteStream.close();

        return o;
    }

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

    public static void byteToFile(File saveFile, byte[] bytes) throws IOException{
        FileUtils.writeByteArrayToFile(saveFile,bytes);
    }

    public static byte[] fileToByte(File file) throws IOException{
        return FileUtils.readFileToByteArray(file);
    }

    public static void objToFile(File saveFile, Object object) throws IOException {
        objToFile(saveFile,object,true);
    }
    public static void objToFile(File saveFile, Object object, boolean compression) throws IOException {
        FileUtils.writeByteArrayToFile(saveFile, objToByte(object, compression));
    }

    public static Object fileToObj(File file) throws ClassNotFoundException, IOException, DataFormatException {
        return fileToObj(file,true);
    }
    public static Object fileToObj(File file, boolean compression) throws IOException, DataFormatException, ClassNotFoundException {
        return byteToObj(FileUtils.readFileToByteArray(file),compression);
    }
}
