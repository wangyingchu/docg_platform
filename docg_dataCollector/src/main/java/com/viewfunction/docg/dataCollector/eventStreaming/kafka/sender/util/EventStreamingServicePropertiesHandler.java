package com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.util;

import com.viewfunction.docg.dataCollector.eventStreaming.exception.ConfigurationErrorException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EventStreamingServicePropertiesHandler {

	private static Properties _properties;

	public static String PRODUCER_BOOTSTRAP_SERVERS="PRODUCER_BOOTSTRAP_SERVERS";
	public static String PRODUCER_SCHEMA_REGISTRY="PRODUCER_SCHEMA_REGISTRY";
	public static String PRODUCER_ACKS="PRODUCER_ACKS";
	public static String PRODUCER_RETRIES="PRODUCER_RETRIES";
	public static String PRODUCER_BATCH_SIZE="PRODUCER_BATCH_SIZE";
	public static String PRODUCER_LINGER_MS="PRODUCER_LINGER_MS";
	public static String PRODUCER_BUFFER_MEMORY="PRODUCER_BUFFER_MEMORY";
	public static String PRODUCER_MAX_REQUEST_SIZE="PRODUCER_MAX_REQUEST_SIZE";
	public static String SCHEMA_REGISTRY_URL="schema.registry.url";

	public static String DestinationTopic="DestinationTopic";
	public static String PayloadKey="PayloadKey";
	public static String SenderGroup="SenderGroup";
	public static String SenderCategory="SenderCategory";
	public static String PayloadType="PayloadType";
	public static String PayloadTypeDesc="PayloadTypeDesc";
	public static String PayloadProcessor="PayloadProcessor";
	public static String PayloadClassification="PayloadClassification";
	public static String ShowOperationMessage="ShowOperationMessage";

    public static String getPropertyValue(String resourceFileName) throws ConfigurationErrorException {
    	if(_properties == null){
			_properties=new Properties();
			try {
				InputStream inputStream = FileUtils.getInputStream("EventStreamingServiceCfg.properties");
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
