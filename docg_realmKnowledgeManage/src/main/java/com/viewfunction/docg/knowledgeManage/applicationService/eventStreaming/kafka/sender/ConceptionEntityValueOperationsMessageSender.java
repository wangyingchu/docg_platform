package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender;


import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.SchemaFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.AvroUtils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;

public class ConceptionEntityValueOperationsMessageSender  extends AvroMessageSender{

    private ObjectMapper mapper = new ObjectMapper();

    public ConceptionEntityValueOperationsMessageSender(MessageSentEventHandler messageSentEventHandler) throws ConfigurationErrorException {
        super(messageSentEventHandler);
    }

    public void sendInfoObjectValueOperationMessage(ConceptionEntityValueOperationContent conceptionEntityValueOperationContent,
                                                    ConceptionEntityValue conceptionEntityValue) throws SchemaFormatErrorException, MessageFormatErrorException, MessageHandleErrorException {
        AvroUtils.initPayloadSchemas();
        Schema payloadSchema= AvroUtils.getSchema(AvroUtils.ConceptionEntityValueOperationContentSchemaName);
        GenericRecord payloadRecord = new GenericData.Record(payloadSchema);
    }

    private void populatePayloadData(GenericRecord payloadRecord,ConceptionEntityValue conceptionEntityValue) {
        if (conceptionEntityValue != null) {
            try {
                Map<String, Object> entityAttributesValueMap = conceptionEntityValue.getEntityAttributesValue();
                if (entityAttributesValueMap != null) {
                    String baseDatasetJsonInString = this.mapper.writeValueAsString(entityAttributesValueMap);
                    //byte[] encodedBytes = Base64.encodeBase64(baseDatasetJsonInString.getBytes());

                    byte[] encodedBytes = Base64.encodeBase64(baseDatasetJsonInString.getBytes());


                    String baseDatasetValueString = new String(encodedBytes);
                    payloadRecord.put("entityAttributesValue", baseDatasetValueString);
                }

            } catch(IOException e){
                e.printStackTrace();
            }

        }
    }
}
