package com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.dataSlicesSync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsReceivedMessage;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.CommonObjectsMessageHandler;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class GeneralDataSliceEntityValueOperationsMessageHandler extends CommonObjectsMessageHandler {

    private Map<Object,Object> commandContextDataMap;

    public GeneralDataSliceEntityValueOperationsMessageHandler(Map<Object,Object> commandContextDataMap){
        this.commandContextDataMap = commandContextDataMap;
    }

    @Override
    protected void operateRecord(Object recordKey, CommonObjectsReceivedMessage receivedMessage, long recordOffset) {
        long messageSendTime = receivedMessage.getMessageSendTime();
        long messageReceivedTime = receivedMessage.getMessageReceivedTime();

        CommonObjectsPayloadMetaInfo commonObjectsPayloadMetaInfo = receivedMessage.getMessageCommonObjectsPayloadMetaInfo();
        commonObjectsPayloadMetaInfo.getPayloadProcessor();
        commonObjectsPayloadMetaInfo.getPayloadType();
        commonObjectsPayloadMetaInfo.getPayloadClassification();
        commonObjectsPayloadMetaInfo.getPayloadTypeDesc();
        commonObjectsPayloadMetaInfo.getSenderCategory();
        commonObjectsPayloadMetaInfo.getSenderGroup();
        commonObjectsPayloadMetaInfo.getSenderId();
        commonObjectsPayloadMetaInfo.getSenderIP();

        CommonObjectsPayloadContent commonObjectsPayloadContent = receivedMessage.getCommonObjectsPayloadContent();
        commonObjectsPayloadContent.getTextContent();
        commonObjectsPayloadContent.getTextContentEncodeAlgorithm();
        commonObjectsPayloadContent.getTextContentEncoded();
        commonObjectsPayloadContent.getIncludingContent();

        System.out.println(" "+recordKey+receivedMessage.getMessageSendTime()+" "+recordOffset);

        if(commonObjectsPayloadContent.getTextContentEncoded()){
            if(commonObjectsPayloadContent.getTextContentEncodeAlgorithm().equals("BASE64")){
                byte[] decodedBytes = Base64.decodeBase64(commonObjectsPayloadContent.getTextContent().getBytes());
                System.out.println("TextContent: "+new String(decodedBytes));


                try {
                    ObjectNode node = (ObjectNode) new ObjectMapper().readTree(decodedBytes);

                    Iterator<String> fieldNames = node.fieldNames();

                    System.out.println("-----------------------------");

                    while(fieldNames.hasNext()){
                        String currentField = fieldNames.next();
                        System.out.println(currentField+" : "+node.get(currentField).textValue());
                    }






                } catch (IOException e) {
                    e.printStackTrace();
                }





            }else{
                //System.out.println("TextContent: "+ commonObjectsPayloadContent.getTextContent());
            }
        }







    }
}
