package com.viewfunction.docg.analysisMotor.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyHandler {

    private static Properties globalConfigurationProperties;

    public static String getConfigPropertyValue(String resourceFileName){
        if(globalConfigurationProperties ==null){
            globalConfigurationProperties =new Properties();
            try {
                globalConfigurationProperties.load(new FileInputStream("analysisMotorConfiguration.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return globalConfigurationProperties.getProperty(resourceFileName);
    }
}
