package com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileUtils {

    public static InputStream getInputStream(String path) {
        String filePathLocation = FileUtils.class.getClassLoader().getResource("").getPath();
        try {
            return new FileInputStream(filePathLocation+"/"+path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return FileUtils.class.getClassLoader().getResourceAsStream(path);
    }
}
