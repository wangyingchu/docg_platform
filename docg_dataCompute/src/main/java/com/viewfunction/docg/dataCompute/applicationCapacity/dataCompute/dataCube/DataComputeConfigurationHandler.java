package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DataComputeConfigurationHandler {

    private static Properties _connectomeNodeProperties;

    public static String getConnectomeNodeConfigurationFilePath(){
        return "configurations/edgeCompute-ignite.xml";
    }

    public static IgniteConfiguration getConnectomeNodeConfiguration(){
        ApplicationContext context = new FileSystemXmlApplicationContext(getConnectomeNodeConfigurationFilePath());
        Object configObject=context.getBean("configuration");
        IgniteConfiguration igniteConfiguration=(IgniteConfiguration)configObject;
        return igniteConfiguration;
    }

    public static String getConfigPropertyValue(String resourceFileName){
        if(_connectomeNodeProperties==null){
            _connectomeNodeProperties=new Properties();
            try {
                _connectomeNodeProperties.load(new FileInputStream("configurations/edgeComputeResourceNodeCfg.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _connectomeNodeProperties.getProperty(resourceFileName);
    }
}
