package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ExternalDataExchangePropertiesHandler {

    private static Properties _properties;

    // -- Apache Doris configuration
    public static String APACHE_DORIS_HOST = "APACHE_DORIS_HOST";
    public static String APACHE_DORIS_PORT = "APACHE_DORIS_PORT";
    public static String APACHE_DORIS_USER = "APACHE_DORIS_USER";
    public static String APACHE_DORIS_PASSWD = "APACHE_DORIS_PASSWD";
    public static String APACHE_DORIS_INSERT_BATCH_SIZE = "APACHE_DORIS_INSERT_BATCH_SIZE";

    public static String getPropertyValue(String propertyName) {
        if(_properties == null){
            String configPath = "ExternalDataExchangeCfg.properties";
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