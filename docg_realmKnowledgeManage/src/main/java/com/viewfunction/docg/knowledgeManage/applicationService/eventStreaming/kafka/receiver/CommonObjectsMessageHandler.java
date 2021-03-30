package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContentType;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsReceivedMessage;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.AvroUtils;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;

import java.nio.ByteBuffer;
import java.util.Date;

public abstract class CommonObjectsMessageHandler extends AvroMessageHandler {

    protected abstract void operateRecord(Object recordKey, CommonObjectsReceivedMessage receivedMessage, long recordOffset);

    @Override
    protected void operateRecord(Object recordKey, GenericRecord messageRecord, long recordOffset) {
        String messageSchemaFullName = messageRecord.getSchema().getFullName();
        if(messageSchemaFullName.equals(AvroUtils.PayLoadSchemaName)){
            CommonObjectsReceivedMessage commonObjectReceivedMessage = new CommonObjectsReceivedMessage();
            commonObjectReceivedMessage.setMessageReceivedTime(new Date().getTime());

            GenericRecord metaInfoRecord=(GenericRecord)messageRecord.get("metaInfo");
            Object sendTime=metaInfoRecord.get("sendTime");
            Object senderId=metaInfoRecord.get("senderId");
            Object senderGroup=metaInfoRecord.get("senderGroup");
            Object payloadType=metaInfoRecord.get("payloadType");
            if(sendTime == null || senderId == null || senderGroup == null || payloadType == null){
                return;
            }
            GenericRecord payloadContentRecord=(GenericRecord)messageRecord.get("payloadContent");
            commonObjectReceivedMessage.setMessageSendTime((Long)sendTime);
            CommonObjectsPayloadMetaInfo messageCommonObjectsPayloadMetaInfo = new CommonObjectsPayloadMetaInfo();
            commonObjectReceivedMessage.setMessageCommonObjectsPayloadMetaInfo(messageCommonObjectsPayloadMetaInfo);
            messageCommonObjectsPayloadMetaInfo.setSenderId(((Utf8)senderId).toString());
            messageCommonObjectsPayloadMetaInfo.setSenderGroup(((Utf8)senderGroup).toString());
            messageCommonObjectsPayloadMetaInfo.setPayloadType(((Utf8)payloadType).toString());
            if(metaInfoRecord.get("senderCategory") != null){
                messageCommonObjectsPayloadMetaInfo.setSenderCategory(((Utf8)metaInfoRecord.get("senderCategory")).toString());
            }
            if(metaInfoRecord.get("senderIP") != null){
                messageCommonObjectsPayloadMetaInfo.setSenderIP(((Utf8)metaInfoRecord.get("senderIP")).toString());
            }
            if(metaInfoRecord.get("payloadClassification") != null){
                messageCommonObjectsPayloadMetaInfo.setPayloadClassification(((Utf8)metaInfoRecord.get("payloadClassification")).toString());
            }
            if(metaInfoRecord.get("payloadTypeDesc") != null){
                messageCommonObjectsPayloadMetaInfo.setPayloadTypeDesc(((Utf8)metaInfoRecord.get("payloadTypeDesc")).toString());
            }
            if(metaInfoRecord.get("payloadProcessor") != null){
                messageCommonObjectsPayloadMetaInfo.setPayloadProcessor(((Utf8)metaInfoRecord.get("payloadProcessor")).toString());
            }

            CommonObjectsPayloadContent infoObjectsPayloadContent = new CommonObjectsPayloadContent();
            commonObjectReceivedMessage.setInfoObjectsPayloadContent(infoObjectsPayloadContent);

            String includingContentValue = ((Utf8)payloadContentRecord.get("includingContent")).toString();
            if(includingContentValue.equals("TEXT")){
                infoObjectsPayloadContent.setIncludingContent(CommonObjectsPayloadContentType.TEXT);
                setPayloadContentForTextCase(infoObjectsPayloadContent,payloadContentRecord);
            }
            if(includingContentValue.equals("BINARY")){
                infoObjectsPayloadContent.setIncludingContent(CommonObjectsPayloadContentType.BINARY);
                setPayloadContentForBinaryCase(infoObjectsPayloadContent, payloadContentRecord);
            }
            if(includingContentValue.equals("ALL")){
                infoObjectsPayloadContent.setIncludingContent(CommonObjectsPayloadContentType.ALL);
                setPayloadContentForAllCase(infoObjectsPayloadContent, payloadContentRecord);
            }
            operateRecord(recordKey,commonObjectReceivedMessage,recordOffset);
        }
    }

    private void setPayloadContentForTextCase(CommonObjectsPayloadContent commonObjectsPayloadContent, GenericRecord payloadContentRecord){
        Object textContent=payloadContentRecord.get("textContent");
        if(textContent!=null){
            commonObjectsPayloadContent.setTextContent(((Utf8)textContent).toString());
        }
        Object textContentEncodeAlgorithm=payloadContentRecord.get("textContentEncodeAlgorithm");
        if(textContentEncodeAlgorithm!=null){
            commonObjectsPayloadContent.setTextContentEncodeAlgorithm(((Utf8)textContentEncodeAlgorithm).toString());
        }
        Object textContentEncoded=payloadContentRecord.get("textContentEncoded");
        if(textContentEncoded!=null){
            commonObjectsPayloadContent.setTextContentEncoded((Boolean)textContentEncoded);
        }
    }
    private void setPayloadContentForBinaryCase(CommonObjectsPayloadContent commonObjectsPayloadContent, GenericRecord payloadContentRecord){
        Object binaryContent=payloadContentRecord.get("binaryContent");
        if(binaryContent!=null){
            commonObjectsPayloadContent.setBinaryContent((ByteBuffer)binaryContent);
        }
    }
    private void setPayloadContentForAllCase(CommonObjectsPayloadContent commonObjectsPayloadContent, GenericRecord payloadContentRecord){
        setPayloadContentForTextCase(commonObjectsPayloadContent,payloadContentRecord);
        setPayloadContentForBinaryCase(commonObjectsPayloadContent, payloadContentRecord);
    }
}
