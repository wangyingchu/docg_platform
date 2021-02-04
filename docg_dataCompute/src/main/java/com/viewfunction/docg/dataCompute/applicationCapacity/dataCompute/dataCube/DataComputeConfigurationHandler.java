package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DataComputeConfigurationHandler {

    private static Properties globalConfigurationProperties;

    public static String getDataComputeIgniteConfigurationFilePath(){
        return "configurations/dataCompute-ignite.xml";
    }

    public static IgniteConfiguration getDataComputeIgniteConfiguration(){
        ApplicationContext context = new FileSystemXmlApplicationContext(getDataComputeIgniteConfigurationFilePath());
        Object configObject=context.getBean("configuration");
        IgniteConfiguration igniteConfiguration=(IgniteConfiguration)configObject;
        return igniteConfiguration;
    }

    public static String getConfigPropertyValue(String resourceFileName){
        if(globalConfigurationProperties ==null){
            globalConfigurationProperties =new Properties();
            try {
                globalConfigurationProperties.load(new FileInputStream("configurations/dataComputeGlobalConfiguration.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return globalConfigurationProperties.getProperty(resourceFileName);
    }
}
