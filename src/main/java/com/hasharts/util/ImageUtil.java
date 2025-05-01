package com.hasharts.util;

import io.ipfs.multibase.binary.Base64;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageUtil {

    public void base64ToFile(String path, String base64) {
        byte[] data = Base64.decodeBase64(base64);
        try (OutputStream stream = new FileOutputStream(path)) {
            stream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
