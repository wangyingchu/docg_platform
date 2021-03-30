package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.SchemaFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContentType;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsMessageTargetInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.AvroUtils;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.PayloadMetaInfo;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.nio.ByteBuffer;
import java.util.Date;

public class CommonObjectsMessageSender extends AvroMessageSender{

    private static final String metaInfoProperty="metaInfo";
    private static final String payloadContentProperty="payloadContent";

    public CommonObjectsMessageSender(MessageSentEventHandler messageSentEventHandler) throws ConfigurationErrorException {
        super(messageSentEventHandler);
    }

    public void sendInfoObjectsMessage(CommonObjectsPayloadMetaInfo commonObjectsPayloadMetaInfo, CommonObjectsPayloadContent commonObjectsPayloadContent, CommonObjectsMessageTargetInfo commonObjectsMessageTargetInfo) throws SchemaFormatErrorException, MessageFormatErrorException, MessageHandleErrorException {
        AvroUtils.initPayloadSchemas();
        Schema payloadMetaInfoSchema= AvroUtils.getSchema(AvroUtils.PayloadMetaInfoSchemaName);
        GenericRecord payloadMetaInfoRecord = new GenericData.Record(payloadMetaInfoSchema);
        Schema payloadContentSchema=AvroUtils.getSchema(AvroUtils.PayLoadContentSchemaName);
        GenericRecord payloadContentRecord = new GenericData.Record(payloadContentSchema);
        Schema neuronGridPayloadSchema=AvroUtils.getSchema(AvroUtils.PayLoadSchemaName);
        GenericRecord payloadRecord = new GenericData.Record(neuronGridPayloadSchema);
        payloadRecord.put(metaInfoProperty,payloadMetaInfoRecord);
        payloadRecord.put(payloadContentProperty,payloadContentRecord);
        //set payload content data
        CommonObjectsPayloadContentType includingContent= commonObjectsPayloadContent.getIncludingContent();
        switch(includingContent){
            case TEXT:
                payloadContentRecord.put("includingContent","TEXT");
                setPayloadContentForTextCase(payloadContentRecord, commonObjectsPayloadContent);
                break;
            case BINARY:
                payloadContentRecord.put("includingContent","BINARY");
                setPayloadContentForBinaryCase(payloadContentRecord, commonObjectsPayloadContent);
                break;
            case ALL:
                payloadContentRecord.put("includingContent","ALL");
                setPayloadContentForAllCase(payloadContentRecord, commonObjectsPayloadContent);
                break;
        }
        //set payload meta info data
        payloadMetaInfoRecord.put("sendTime",new Date().getTime());
        String senderId= commonObjectsPayloadMetaInfo.getSenderId();
        String senderGroup= commonObjectsPayloadMetaInfo.getSenderGroup();
        if(senderId==null||senderGroup==null){
            throw new MessageFormatErrorException();
        }else{
            payloadMetaInfoRecord.put("senderId",senderId);
            payloadMetaInfoRecord.put("senderGroup",senderGroup);
        }
        String senderCategory= commonObjectsPayloadMetaInfo.getSenderCategory();
        if(senderCategory!=null){
            payloadMetaInfoRecord.put("senderCategory",senderCategory);
        }
        String senderIP= commonObjectsPayloadMetaInfo.getSenderIP();
        if(senderIP!=null){
            payloadMetaInfoRecord.put("senderIP",senderIP);
        }
        String payloadType= commonObjectsPayloadMetaInfo.getPayloadType();
        if(payloadType==null){
            throw new MessageFormatErrorException();
        }else{
            payloadMetaInfoRecord.put("payloadType",payloadType);
        }
        String payloadTypeDesc= commonObjectsPayloadMetaInfo.getPayloadTypeDesc();
        if(payloadTypeDesc!=null){
            payloadMetaInfoRecord.put("payloadTypeDesc",payloadTypeDesc);
        }
        String payloadProcessor= commonObjectsPayloadMetaInfo.getPayloadProcessor();
        if(payloadProcessor!=null){
            payloadMetaInfoRecord.put("payloadProcessor",payloadProcessor);
        }
        String payloadClassification= commonObjectsPayloadMetaInfo.getPayloadClassification();
        if(payloadClassification!=null){
            payloadMetaInfoRecord.put("payloadClassification",payloadClassification);
        }

        PayloadMetaInfo pmi=new PayloadMetaInfo();
        pmi.setPayloadSchema(AvroUtils.PayLoadSchemaName);
        pmi.setDestinationTopic(commonObjectsMessageTargetInfo.getDestinationTopic());
        pmi.setPayloadKey(commonObjectsMessageTargetInfo.getPayloadKey());

        this.sendAvroMessage(pmi,payloadRecord);
    }

    private static void setPayloadContentForTextCase(GenericRecord payloadContentRecord, CommonObjectsPayloadContent commonObjectsPayloadContent){
        String textContent= commonObjectsPayloadContent.getTextContent();
        payloadContentRecord.put("textContent",textContent);
        boolean textContentEncoded= commonObjectsPayloadContent.getTextContentEncoded();
        if(textContentEncoded){
            payloadContentRecord.put("textContentEncoded",true);
            String textContentEncodeAlgorithm= commonObjectsPayloadContent.getTextContentEncodeAlgorithm();
            payloadContentRecord.put("textContentEncodeAlgorithm",textContentEncodeAlgorithm);
        }else{
            payloadContentRecord.put("textContentEncoded",false);
        }
    }

    private static void setPayloadContentForBinaryCase(GenericRecord payloadContentRecord, CommonObjectsPayloadContent commonObjectsPayloadContent){
        ByteBuffer binaryContent= commonObjectsPayloadContent.getBinaryContent();
        payloadContentRecord.put("binaryContent",binaryContent);
    }

    private static void setPayloadContentForAllCase(GenericRecord payloadContentRecord, CommonObjectsPayloadContent commonObjectsPayloadContent){
        setPayloadContentForTextCase(payloadContentRecord, commonObjectsPayloadContent);
        setPayloadContentForBinaryCase(payloadContentRecord, commonObjectsPayloadContent);
    }
}
