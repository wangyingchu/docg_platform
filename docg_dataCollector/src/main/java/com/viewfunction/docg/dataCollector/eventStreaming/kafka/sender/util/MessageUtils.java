package com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.viewfunction.docg.dataCollector.eventStreaming.exception.MessageFormatErrorException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MessageUtils {

    public static GenericRecord generateGenericRecordWithSchema(Schema schema, JsonNode messageContent) throws MessageFormatErrorException {
        if(messageContent.isValueNode()){
            throw new MessageFormatErrorException();
        }else{
            if(messageContent.isArray()){
                throw new MessageFormatErrorException();
            }
        }
        GenericRecord acroRecord = new GenericData.Record(schema);
        Iterator<String> fields = messageContent.fieldNames();
        while(fields.hasNext()) {
            String fieldName = fields.next();
            JsonNode currentChildNode=messageContent.path(fieldName);
            setPrimitiveTypeProperty(acroRecord, fieldName, currentChildNode);
        }
        return acroRecord;
    }

    private static void setPrimitiveTypeProperty(GenericRecord containerGenericRecord, String propertyName, JsonNode propertyValue) throws MessageFormatErrorException {
        if(propertyValue.isValueNode()){
            if(propertyValue.isBinary()){
                ByteBuffer binaryBuffer= null;
                try {
                    binaryBuffer = ByteBuffer.wrap(propertyValue.binaryValue());
                } catch (IOException e) {
                    e.printStackTrace();
                    MessageFormatErrorException mfeException=new MessageFormatErrorException();
                    mfeException.initCause(e);
                    throw mfeException;
                }
                containerGenericRecord.put(propertyName, binaryBuffer);
            }
            if(propertyValue.isBoolean()){
                containerGenericRecord.put(propertyName,propertyValue.booleanValue());
            }
            if(propertyValue.isLong()){
                containerGenericRecord.put(propertyName,propertyValue.longValue());
            }
            if(propertyValue.isDouble()){
                containerGenericRecord.put(propertyName,propertyValue.doubleValue());
            }
            if(propertyValue.isFloatingPointNumber()){
                containerGenericRecord.put(propertyName,propertyValue.numberValue().floatValue());
            }
            if(propertyValue.isInt()){
                containerGenericRecord.put(propertyName,propertyValue.intValue());
            }
            if(propertyValue.isIntegralNumber()){
                containerGenericRecord.put(propertyName,propertyValue.intValue());
            }
            if(propertyValue.isNull()){
                containerGenericRecord.put(propertyName,null);
            }
            if(propertyValue.isTextual()){
                containerGenericRecord.put(propertyName,new Utf8(propertyValue.textValue()));
            }
            if(propertyValue.isBigDecimal()){
                containerGenericRecord.put(propertyName,propertyValue.decimalValue().doubleValue());
            }
            if(propertyValue.isBigInteger()){
                containerGenericRecord.put(propertyName,propertyValue.bigIntegerValue().intValue());
            }
        }else if(propertyValue.isArray()){
            ArrayNode arrayNode=(ArrayNode)propertyValue;
            if(arrayNode.size()==0){
                containerGenericRecord.put(propertyName,null);
            }else{
                JsonNode firstNode=arrayNode.get(0);
                if(!firstNode.isValueNode()){
                    throw new MessageFormatErrorException();
                }
                if(firstNode.isBinary()){
                    List<ByteBuffer> arrayDataList = new ArrayList<ByteBuffer>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isBinary()){
                            throw new MessageFormatErrorException();
                        }
                        ByteBuffer binaryBuffer= null;
                        try {
                            binaryBuffer = ByteBuffer.wrap(currentArrayValue.binaryValue());
                            arrayDataList.add(binaryBuffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                            MessageFormatErrorException mfeException=new MessageFormatErrorException();
                            mfeException.initCause(e);
                            throw mfeException;
                        }
                    }
                }
                if(firstNode.isBoolean()){
                    List<Boolean> arrayDataList = new ArrayList<Boolean>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isBoolean()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(currentArrayValue.booleanValue());
                    }
                }
                if(firstNode.isLong()){
                    List<Long> arrayDataList = new ArrayList<Long>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isLong()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(currentArrayValue.longValue());
                    }
                }
                if(firstNode.isDouble()){
                    List<Double> arrayDataList = new ArrayList<Double>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isDouble()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(currentArrayValue.doubleValue());
                    }
                }
                if(firstNode.isFloatingPointNumber()){
                    List<Float> arrayDataList = new ArrayList<Float>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isFloatingPointNumber()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(currentArrayValue.numberValue().floatValue());
                    }
                }
                if(firstNode.isInt()){
                    List<Integer> arrayDataList = new ArrayList<Integer>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isInt()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(currentArrayValue.intValue());
                    }
                }
                if(firstNode.isIntegralNumber()){
                    List<Integer> arrayDataList = new ArrayList<Integer>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isIntegralNumber()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(currentArrayValue.intValue());
                    }
                }
                if(firstNode.isTextual()){
                    List<Utf8> arrayDataList = new ArrayList<Utf8>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isTextual()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(new Utf8(currentArrayValue.textValue()));
                    }
                }
                if(firstNode.isBigDecimal()){
                    List<Double> arrayDataList = new ArrayList<Double>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isBigDecimal()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(currentArrayValue.decimalValue().doubleValue());
                    }
                }
                if(firstNode.isBigInteger()){
                    List<Integer> arrayDataList = new ArrayList<Integer>();
                    containerGenericRecord.put(propertyName,arrayDataList);
                    for(JsonNode currentArrayValue : propertyValue) {
                        if(!currentArrayValue.isBigInteger()){
                            throw new MessageFormatErrorException();
                        }
                        arrayDataList.add(currentArrayValue.bigIntegerValue().intValue());
                    }
                }
            }
        }else{
            //propertyValue.isContainerNode() but not propertyValue.isArray()
            /*
            //need getObjectTypeIdDefines child schema
            Schema childPropertySchema=null;
            GenericRecord childGenericRecord=new GenericData.Record(childPropertySchema);
            Iterator<String> childFields=propertyValue.getFieldNames();
            while(childFields.hasNext()){
                String fieldName=childFields.next();
                JsonNode currentChildNode=propertyValue.path(fieldName);
                setChildProperty(childGenericRecord,fieldName,currentChildNode);

            }
            containerGenericRecord.put(propertyName,childGenericRecord);
            */
        }
    }
}