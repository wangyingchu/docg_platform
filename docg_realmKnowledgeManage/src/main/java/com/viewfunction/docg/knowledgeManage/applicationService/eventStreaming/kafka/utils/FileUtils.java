package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileUtils {

    public static InputStream getInputStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            //非文件系统 可能在jar包内
        }
        return FileUtils.class.getClassLoader().getResourceAsStream(path);
    }
}
