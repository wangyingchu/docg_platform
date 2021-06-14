package com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.util;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MemoryTableConfigurationHandler {

    private static Properties globalConfigurationProperties;

    public static String getIgniteConfigurationFilePath(){
        return "igniteConfig/cimIgnite.xml";
    }

    public static IgniteConfiguration getIgniteConfiguration(){
        ApplicationContext context = new FileSystemXmlApplicationContext(getIgniteConfigurationFilePath());
        Object configObject=context.getBean("configuration");
        IgniteConfiguration igniteConfiguration=(IgniteConfiguration)configObject;
        return igniteConfiguration;
    }

    /*
    public static String getConfigPropertyValue(String resourceFileName){
        if(globalConfigurationProperties ==null){
            globalConfigurationProperties =new Properties();
            try {
                globalConfigurationProperties.load(new FileInputStream("analysisEngineConfiguration.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return globalConfigurationProperties.getProperty(resourceFileName);
    }

     */
}
