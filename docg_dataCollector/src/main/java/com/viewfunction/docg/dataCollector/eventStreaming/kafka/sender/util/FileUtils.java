package com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileUtils {

    public static InputStream getInputStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return FileUtils.class.getClassLoader().getResourceAsStream(path);
    }
}
