package com.docg.ai.util.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {
    private static Properties _properties;
    public static String TEXT2CYPHER_TECHNOLOGY = "TEXT2CYPHER_TECHNOLOGY";
    public static String TEXT2CYPHER_MODEL_NAME = "TEXT2CYPHER_MODEL_NAME";
    public static String TEXT2CYPHER_MODEL_BASEURL = "TEXT2CYPHER_MODEL_BASEURL";
    public static String TEXT2CYPHER_MODEL_APIKEY = "TEXT2CYPHER_MODEL_APIKEY";

    public static String getPropertyValue(String propertyName) {
        if(_properties == null){
            String configPath = "AIComponentConfiguration.properties";
            _properties = new Properties();
            try {
                _properties.load(new FileInputStream(configPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _properties.getProperty(propertyName);
    }
}
