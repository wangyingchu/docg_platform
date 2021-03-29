package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.SchemaFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContentType;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.InfoObjectsMessageTargetInfo;
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

    public void sendInfoObjectsMessage(CommonObjectsPayloadMetaInfo commonObjectsPayloadMetaInfo, CommonObjectsPayloadContent infoObjectsPayloadContent, InfoObjectsMessageTargetInfo infoObjectsMessageTargetInfo) throws SchemaFormatErrorException, MessageFormatErrorException, MessageHandleErrorException {
        AvroUtils.initPayloadSchemas();
        Schema payloadMetaInfoSchema= AvroUtils.getSchema(AvroUtils.InfoObjectsPayloadMetaInfoSchemaName);
        GenericRecord payloadMetaInfoRecord = new GenericData.Record(payloadMetaInfoSchema);
        Schema payloadContentSchema=AvroUtils.getSchema(AvroUtils.InfoObjectsPayLoadContentSchemaName);
        GenericRecord payloadContentRecord = new GenericData.Record(payloadContentSchema);
        Schema neuronGridPayloadSchema=AvroUtils.getSchema(AvroUtils.InfoObjectsPayLoadSchemaName);
        GenericRecord neuronGridPayloadRecord = new GenericData.Record(neuronGridPayloadSchema);
        neuronGridPayloadRecord.put(metaInfoProperty,payloadMetaInfoRecord);
        neuronGridPayloadRecord.put(payloadContentProperty,payloadContentRecord);
        //set payload content data
        CommonObjectsPayloadContentType includingContent= infoObjectsPayloadContent.getIncludingContent();
        switch(includingContent){
            case TEXT:
                payloadContentRecord.put("includingContent","TEXT");
                setPayloadContentForTextCase(payloadContentRecord, infoObjectsPayloadContent);
                break;
            case BINARY:
                payloadContentRecord.put("includingContent","BINARY");
                setPayloadContentForBinaryCase(payloadContentRecord, infoObjectsPayloadContent);
                break;
            case ALL:
                payloadContentRecord.put("includingContent","ALL");
                setPayloadContentForAllCase(payloadContentRecord, infoObjectsPayloadContent);
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
        pmi.setPayloadSchema(AvroUtils.InfoObjectsPayLoadSchemaName);
        pmi.setDestinationTopic(infoObjectsMessageTargetInfo.getDestinationTopic());
        pmi.setPayloadKey(infoObjectsMessageTargetInfo.getPayloadKey());

        this.sendAvroMessage(pmi,neuronGridPayloadRecord);
    }

    private static void setPayloadContentForTextCase(GenericRecord payloadContentRecord, CommonObjectsPayloadContent infoObjectsPayloadContent){
        String textContent= infoObjectsPayloadContent.getTextContent();
        payloadContentRecord.put("textContent",textContent);
        boolean textContentEncoded= infoObjectsPayloadContent.getTextContentEncoded();
        if(textContentEncoded){
            payloadContentRecord.put("textContentEncoded",true);
            String textContentEncodeAlgorithm= infoObjectsPayloadContent.getTextContentEncodeAlgorithm();
            payloadContentRecord.put("textContentEncodeAlgorithm",textContentEncodeAlgorithm);
        }else{
            payloadContentRecord.put("textContentEncoded",false);
        }
    }

    private static void setPayloadContentForBinaryCase(GenericRecord payloadContentRecord, CommonObjectsPayloadContent infoObjectsPayloadContent){
        ByteBuffer binaryContent= infoObjectsPayloadContent.getBinaryContent();
        payloadContentRecord.put("binaryContent",binaryContent);
    }

    private static void setPayloadContentForAllCase(GenericRecord payloadContentRecord, CommonObjectsPayloadContent infoObjectsPayloadContent){
        setPayloadContentForTextCase(payloadContentRecord, infoObjectsPayloadContent);
        setPayloadContentForBinaryCase(payloadContentRecord, infoObjectsPayloadContent);
    }
}
