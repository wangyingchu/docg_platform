package com.viewfunction.docg.coreRealm.realmServiceCore.util.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {

    private static Properties _properties;

    public static String CORE_REALM_STORAGE_IMPL_TECH = "CORE_REALM_STORAGE_IMPL_TECH";

    //Common configuration properties
    public static String COMMON_DATA_ORIGIN = "COMMON_DATA_ORIGIN";

    //Neo4j Implements configuration properties
    public static String NEO4J_SUPPORT_MULTI_GRAPH = "NEO4J_SUPPORT_MULTI_GRAPH";
    public static String NEO4J_URI = "NEO4J_URI";
    public static String NEO4J_USER = "NEO4J_USER";
    public static String NEO4J_PASSWORD = "NEO4J_PASSWORD";

    public static String getPropertyValue(String propertyName) {
        if(_properties == null){
            //String configPath= RuntimeEnvironmentHandler.getApplicationRootPath() + "InfoDiscoverEngineCfg.properties";
            String configPath = "CoreRealmCfg.properties";
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