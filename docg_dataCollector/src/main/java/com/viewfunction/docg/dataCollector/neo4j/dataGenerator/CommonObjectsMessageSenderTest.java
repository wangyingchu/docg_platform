package com.viewfunction.docg.dataCollector.neo4j.dataGenerator;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.viewfunction.docg.dataCollector.eventStreaming.exception.ConfigurationErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.exception.MessageFormatErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.exception.MessageHandleErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.exception.SchemaFormatErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.CommonObjectsMessageSender;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.MessageSentEventHandler;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload.CommonObjectsMessageTargetInfo;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload.CommonObjectsPayloadContentType;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload.CommonObjectsPayloadMetaInfo;
import org.apache.commons.codec.binary.Base64;

import java.util.Date;

public class CommonObjectsMessageSenderTest {

    public static void main(String[] args) throws SchemaFormatErrorException, MessageFormatErrorException, ConfigurationErrorException, MessageHandleErrorException {
        CommonObjectsPayloadMetaInfo commonObjectsPayloadMetaInfo =new CommonObjectsPayloadMetaInfo();
        commonObjectsPayloadMetaInfo.setSenderId("senderId001");
        commonObjectsPayloadMetaInfo.setSenderGroup("senderGroup001");
        commonObjectsPayloadMetaInfo.setPayloadType("payloadType001");

        commonObjectsPayloadMetaInfo.setSenderIP("127.0.0.1");
        commonObjectsPayloadMetaInfo.setSenderCategory("SenderCategory001");
        commonObjectsPayloadMetaInfo.setPayloadTypeDesc("PayloadTypeDesc001");
        commonObjectsPayloadMetaInfo.setPayloadProcessor("PayloadProcessor001ß");
        commonObjectsPayloadMetaInfo.setPayloadClassification("PayloadClassification001ß");

        CommonObjectsPayloadContent commonObjectsPayloadContent =new CommonObjectsPayloadContent();
        commonObjectsPayloadContent.setIncludingContent(CommonObjectsPayloadContentType.TEXT);
        commonObjectsPayloadContent.setTextContentEncoded(true);

        String textContent="Message 0123456789023456789中文Ωß∂ç√∂©©ƒƒß≈√ƒ";
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("field001",1);
        node.put("field002",new Date().getTime());
        node.put("field003",textContent);
        byte[] encodedBytes = Base64.encodeBase64(node.toString().getBytes());
        commonObjectsPayloadContent.setTextContentEncodeAlgorithm("BASE64");
        commonObjectsPayloadContent.setTextContent(new String(encodedBytes));

        CommonObjectsMessageTargetInfo commonObjectsMessageTargetInfo =new CommonObjectsMessageTargetInfo();
        commonObjectsMessageTargetInfo.setDestinationTopic("CommonObjectsTopic");
        commonObjectsMessageTargetInfo.setPayloadKey("payloadKey001");

        CommonObjectsMessageSender commonObjectsMessageSender =new CommonObjectsMessageSender(new MessageSentEventHandler(){
            @Override
            public void operateMetaData(long offset, long timestamp, String topic, int partition) {
                System.out.println(offset+" - "+timestamp+" - "+topic+" - "+partition);
            }
        });

        commonObjectsMessageSender.beginMessageSendBatch();
        for(int i=0;i<10;i++) {
            commonObjectsMessageSender.sendInfoObjectsMessage(commonObjectsPayloadMetaInfo, commonObjectsPayloadContent, commonObjectsMessageTargetInfo);
        }
        commonObjectsMessageSender.finishMessageSendBatch();
    }
}
