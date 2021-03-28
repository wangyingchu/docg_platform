package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaDataIntegrationPropertyHandler {

	private static Properties _properties;

	public static String PRODUCER_BOOTSTRAP_SERVERS="PRODUCER_BOOTSTRAP_SERVERS";
	public static String PRODUCER_SCHEMA_REGISTRY="PRODUCER_SCHEMA_REGISTRY";
	public static String PRODUCER_ACKS="PRODUCER_ACKS";
	public static String PRODUCER_RETRIES="PRODUCER_RETRIES";
	public static String PRODUCER_BATCH_SIZE="PRODUCER_BATCH_SIZE";
	public static String PRODUCER_LINGER_MS="PRODUCER_LINGER_MS";
	public static String PRODUCER_BUFFER_MEMORY="PRODUCER_BUFFER_MEMORY";
	public static String PRODUCER_MAX_REQUEST_SIZE="PRODUCER_MAX_REQUEST_SIZE";

    public static String CONSUMER_BOOTSTRAP_SERVERS="CONSUMER_BOOTSTRAP_SERVERS";
    public static String CONSUMER_SCHEMA_REGISTRY="CONSUMER_SCHEMA_REGISTRY";
    public static String CONSUMER_GROUP_ID="CONSUMER_GROUP_ID";
    public static String CONSUMER_ENABLE_AUTO_COMMIT="CONSUMER_ENABLE_AUTO_COMMIT";
    public static String CONSUMER_AUTO_COMMIT_INTERVAL_MS="CONSUMER_AUTO_COMMIT_INTERVAL_MS";
    public static String CONSUMER_SESSION_TIMEOUT_MS="CONSUMER_SESSION_TIMEOUT_MS";
    public static String CONSUMER_MAX_PARTITION_FETCH_BYTES="CONSUMER_MAX_PARTITION_FETCH_BYTES";
    public static String CONSUMER_POLL_MS="CONSUMER_POLL_MS";

    public static String SCHEMA_REGISTRY_URL="schema.registry.url";

    public static String getPerportyValue(String resourceFileName) throws ConfigurationErrorException {
    	if(_properties == null){
			_properties=new Properties();
			try {
				InputStream inputStream = FileUtils.getInputStream("config/confluent/DataExchangeCfg_confluent.properties");
				_properties.load(inputStream);
				inputStream.close();
			} catch (FileNotFoundException e) {
				ConfigurationErrorException cpe=new ConfigurationErrorException();
				cpe.initCause(e);
				throw cpe;
			} catch (IOException e) {
				ConfigurationErrorException cpe=new ConfigurationErrorException();
				cpe.initCause(e);
				throw cpe;
			}
    	}
		return _properties.getProperty(resourceFileName);
	}
}
