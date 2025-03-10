package com.viewfunction.docg.coreRealm.realmServiceCore.util.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {
    public static final String SYSTEM_RESOURCE_ROOT = "systemResource";

    private static Properties _properties;

    public static String CORE_REALM_STORAGE_IMPL_TECH = "CORE_REALM_STORAGE_IMPL_TECH";

    //Common configuration properties
    public static String COMMON_DATA_ORIGIN = "COMMON_DATA_ORIGIN";
    // -- Cache configuration
    public static String CACHE_ENABLE = "CACHE_ENABLE";
    public static String CLUSTER_RESOURCE_CACHE_ENABLE = "CLUSTER_RESOURCE_CACHE_ENABLE";
    public static String LOCAL_CACHE_TIME_TO_LIVE_SECONDS = "LOCAL_CACHE_TIME_TO_LIVE_SECONDS";
    public static String LOCAL_CACHE_ON_HEAP_ENTRY_SIZE = "LOCAL_CACHE_ON_HEAP_ENTRY_SIZE";
    public static String CLUSTER_CACHE_TIME_TO_LIVE_SECONDS = "CLUSTER_CACHE_TIME_TO_LIVE_SECONDS";
    public static String CLUSTER_CACHE_RESOURCE_POOL_SIZE_Mb = "CLUSTER_CACHE_RESOURCE_POOL_SIZE_Mb";
    public static String CLUSTER_RESOURCE_CACHE_SERVICE_LOCATION = "CLUSTER_RESOURCE_CACHE_SERVICE_LOCATION";
    public static String CLUSTER_RESOURCE_CACHE_SERVICE_DEFAULT_RESOURCE_ID = "CLUSTER_RESOURCE_CACHE_SERVICE_DEFAULT_RESOURCE_ID";
    public static String CLUSTER_RESOURCE_CACHE_SERVICE_SHARE_RESOURCE_ID = "CLUSTER_RESOURCE_CACHE_SERVICE_SHARE_RESOURCE_ID";

    //Neo4j Implements configuration properties
    public static String NEO4J_SUPPORT_MULTI_GRAPH = "NEO4J_SUPPORT_MULTI_GRAPH";
    public static String NEO4J_URI = "NEO4J_URI";
    public static String NEO4J_USER = "NEO4J_USER";
    public static String NEO4J_PASSWORD = "NEO4J_PASSWORD";
    public static String NEO4J_USING_CONNECTION_POOL = "NEO4J_USING_CONNECTION_POOL";
    public static String NEO4J_MAX_CONNECTION_POOL_SIZE = "NEO4J_MAX_CONNECTION_POOL_SIZE";
    public static String NEO4J_CONNECTION_ACQUISITION_TIMEOUT_SECONDS = "NEO4J_CONNECTION_ACQUISITION_TIMEOUT_SECONDS";

    //ArcadeDB Implements configuration properties
    public static String ARCADEDB_SERVER_ADDRESS = "ARCADEDB_SERVER_ADDRESS";
    public static String ARCADEDB_SERVER_PORT = "ARCADEDB_SERVER_PORT";
    public static String ARCADEDB_USER = "ARCADEDB_USER";
    public static String ARCADEDB_PASSWORD = "ARCADEDB_PASSWORD";

    //NebulaGraph Implements configuration properties
    public static String NEBULAGRAPH_SERVER_ADDRESS = "NEBULAGRAPH_SERVER_ADDRESS";
    public static String NEBULAGRAPH_SERVER_PORT = "NEBULAGRAPH_SERVER_PORT";
    public static String NEBULAGRAPH_USER = "NEBULAGRAPH_USER";
    public static String NEBULAGRAPH_PASSWORD = "NEBULAGRAPH_PASSWORD";
    public static String NEBULAGRAPH_PARTITION_NUMBER = "NEBULAGRAPH_PARTITION_NUMBER";
    public static String NEBULAGRAPH_REPLICA_FACTOR = "NEBULAGRAPH_REPLICA_FACTOR";

    //MemGraph Implements configuration properties
    public static String MEMGRAPH_SUPPORT_MULTI_GRAPH = "MEMGRAPH_SUPPORT_MULTI_GRAPH";

    //TuGraph Implements configuration properties
    public static String NEO4J_SUPPORT_EXPLICIT_TRANSACTION = "NEO4J_SUPPORT_EXPLICIT_TRANSACTION";
    public static String NEO4J_DEFAULT_DATABASE_NAME = "NEO4J_DEFAULT_DATABASE_NAME";

    public static String DEFAULT_REALM_NAME = "DEFAULT_REALM_NAME";

    public static String getPropertyValue(String propertyName) {
        if(_properties == null){
            //String configPath= RuntimeEnvironmentHandler.getApplicationRootPath() + "CoreRealmCfg.properties";
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