package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.objectStore;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.ExternalAttributesValueAccessProcessor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultObjectStoreExternalAttributesValueAccessProcessor implements ExternalAttributesValueAccessProcessor {

    public final String _ExternalObjectStore_Host = "DOCG_ExternalObjectStore_Host";
    public final String _ExternalObjectStore_Port = "DOCG_ExternalObjectStore_Port";
    public final String _ExternalObjectStore_UserName = "DOCG_ExternalObjectStore_UserName";
    public final String _ExternalObjectStore_UserPWD = "DOCG_ExternalObjectStore_UserPWD";
    public final String _ExternalObjectStore_StoreRoot = "DOCG_ExternalObjectStore_StoreRoot";
    public final String _ExternalObjectStore_BaseFolder = "DOCG_ExternalObjectStore_BaseFolder";

    //public final String _ExternalObjectStore_Endpoint = "DOCG_ExternalObjectStore_Endpoint";
    //public final String _ExternalObjectStore_AccessKey = "DOCG_ExternalObjectStore_AccessKey";
    //public final String _ExternalObjectStore_SecretKey = "DOCG_ExternalObjectStore_SecretKey";
    //public final String _ExternalObjectStore_Bucket = "DOCG_ExternalObjectStore_Bucket";
    //public final String _ExternalObjectStore_Prefix = "DOCG_ExternalObjectStore_Prefix";

    public final String _ExternalObjectStore_IsFolderObject = "DOCG_ExternalObjectStore_IsFolderObject";
    public final String _ExternalObjectStore_ObjectName = "DOCG_ExternalObjectStore_ObjectName";
    public final String _ExternalObjectStore_ObjectSize = "DOCG_ExternalObjectStore_ObjectSize";

    /*
    //MinIO configuration properties
    MINIO_ENDPOINT = "http://192.168.1.28:9000"
    MINIO_ACCESS_KEY = B5PVSeDBuzTBuIyk52RE
    MINIO_SECRET_KEY = DXzK7X02q9ow9OBl1qloWcXQvorXPLxjx5PeerXz
    */



    @Override
    public List<Map<String, Object>> getEntityExternalAttributesValues(AttributesViewKind attributesViewKind, QueryParameters queryParameters, List<AttributeValue> attributeValueList) {
        if(attributesViewKind != null){
            String host = null;
            String port = null;
            String userName = null;
            String userPWD = null;
            String storeRoot = null;
            String baseFolder = null;

            Map<String,Object> metaConfigItems = attributesViewKind.getMetaConfigItems();
            if(metaConfigItems.containsKey(_ExternalObjectStore_Host)){
                host = metaConfigItems.get(_ExternalObjectStore_Host).toString();
            }
            if(metaConfigItems.containsKey(_ExternalObjectStore_Port)){
                port = metaConfigItems.get(_ExternalObjectStore_Port).toString();
            }
            if(metaConfigItems.containsKey(_ExternalObjectStore_UserName)){
                userName = metaConfigItems.get(_ExternalObjectStore_UserName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalObjectStore_UserPWD)){
                userPWD = metaConfigItems.get(_ExternalObjectStore_UserPWD).toString();
            }
            if(metaConfigItems.containsKey(_ExternalObjectStore_StoreRoot)){
                storeRoot = metaConfigItems.get(_ExternalObjectStore_StoreRoot).toString();
            }
            if(metaConfigItems.containsKey(_ExternalObjectStore_BaseFolder)){
                baseFolder = metaConfigItems.get(_ExternalObjectStore_BaseFolder).toString();
            }

            List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();
            Map<String, AttributeDataType> attributeDataTypeMap = new HashMap<>();
            for(AttributeKind currentAttributeKind:attributeKindList){
                String attributeName = currentAttributeKind.getAttributeKindName();
                AttributeDataType attributeDataType = currentAttributeKind.getAttributeDataType();
                attributeDataTypeMap.put(attributeName,attributeDataType);
            }

            //if(!attributeKindList.isEmpty() && host != null && port != null && userName != null && userPWD != null && storeRoot != null){
            if(host != null && port != null && userName != null && userPWD != null && storeRoot != null){
                try {
                    // Create a minioClient with the MinIO server playground, its access key and secret key.
                    String endpoint = "http://"+host+":"+port;
                    MinioClient minioClient =
                            MinioClient.builder()
                                    .endpoint(endpoint)
                                    .credentials(userName, userPWD)
                                    .build();

                    boolean storeRootExist =
                            minioClient.bucketExists(BucketExistsArgs.builder().bucket(storeRoot).build());
                    if (!storeRootExist) {
                        return null;
                    }else {
                        List<Map<String, Object>> resultList = new ArrayList<>();
                        ListObjectsArgs listObjectsArgs = baseFolder != null ? ListObjectsArgs.builder().bucket(storeRoot).prefix(baseFolder).build() :
                                ListObjectsArgs.builder().bucket(storeRoot).build();
                        Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
                        for(Result<Item> result : results){
                            Map<String,Object> currentObjectResultMap = new HashMap<>();
                            Item item = result.get();
                            currentObjectResultMap.put(_ExternalObjectStore_ObjectName,item.objectName());
                            currentObjectResultMap.put(_ExternalObjectStore_ObjectSize,item.size());
                            currentObjectResultMap.put(_ExternalObjectStore_IsFolderObject,item.isDir());
                            resultList.add(currentObjectResultMap);
                            if(!attributeKindList.isEmpty()){
                                Map<String, String> userMetadata = minioClient.statObject(
                                        StatObjectArgs.builder().bucket(storeRoot).object(item.objectName()).build()
                                ).userMetadata();
                                for (Map.Entry<String, String> entry : userMetadata.entrySet()) {
                                    String attributeName = entry.getKey();
                                    if(attributeDataTypeMap.containsKey(attributeName)){
                                        currentObjectResultMap.put(attributeName,getExternalAttributeValue(attributeDataTypeMap.get(attributeName),entry.getValue()));
                                    }
                                }
                            }
                        }
                        return resultList;
                    }
                } catch (MinioException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    @Override
    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
    }

    @Override
    public Long deleteEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
    }

    private Object getExternalAttributeValue(AttributeDataType attributeDataType,String attributeValue){
        Object attributeObjectValue = null;
        switch(attributeDataType){
            case STRING -> attributeObjectValue = attributeValue;
            case INT -> attributeObjectValue = Integer.valueOf(attributeValue);
            case BOOLEAN -> attributeObjectValue = Boolean.valueOf(attributeValue);
            case LONG -> attributeObjectValue = Long.valueOf(attributeValue);
            case FLOAT -> attributeObjectValue = Float.valueOf(attributeValue);
            case DOUBLE -> attributeObjectValue = Double.valueOf(attributeValue);
            case BYTE -> attributeObjectValue = Byte.valueOf(attributeValue);
            case SHORT -> attributeObjectValue = Short.valueOf(attributeValue);
            case DECIMAL -> attributeObjectValue =  new BigDecimal(attributeValue);
            case DATE -> attributeObjectValue = LocalDate.parse(attributeValue); //LocalDate
            case TIME -> attributeObjectValue = LocalTime.parse(attributeValue); //LocalTime
            case DATETIME -> attributeObjectValue = LocalDateTime.parse(attributeValue); //LocalDateTime
            case TIMESTAMP -> attributeObjectValue = ZonedDateTime.parse(attributeValue); //ZonedDateTime
        }
        return attributeObjectValue;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // TODO Auto-generated method stub
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://192.168.1.28:9000")
                            .credentials("B5PVSeDBuzTBuIyk52RE", "DXzK7X02q9ow9OBl1qloWcXQvorXPLxjx5PeerXz")
                            .build();

            // Make 'asiatrip' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("asiatrip").build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("asiatrip").build());
            } else {
                System.out.println("Bucket 'asiatrip' already exists.");
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.

            Map<String,String> infoMap = new HashMap<>();
            infoMap.put("DOCG_p1", "video/mp4");
            infoMap.put("DOCG_p2", "attachment; filename=\"VID_20230825_132716.mp4\"");
            infoMap.put("DOCG_p3", "52428800");

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("asiatrip")
                            .object("VID_20230825_132716_INFO2.mp4")
                            .userMetadata(infoMap)
                            .filename("/home/wangychu/Downloads/VID_20230825_132716.mp4")
                            .build());




            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket("asiatrip").build());
            //Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket("asiatrip").prefix("/").build());
            for (Result<Item> result : results) {

                Item item = result.get();
                System.out.println(item.objectName());
                System.out.println(item.size());
                System.out.println(item.isDir());

                System.out.println(item.etag());
                System.out.println(item.userMetadata());
                System.out.println(item.userTags());



                System.out.println(item.owner());
                System.out.println(item.storageClass());
                System.out.println(item.storageClass());


                Map<String, String> userMetadata = minioClient.statObject(
                        StatObjectArgs.builder().bucket("asiatrip").object(item.objectName()).build()
                ).userMetadata();
                for (Map.Entry<String, String> entry : userMetadata.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }


                System.out.println("---------------------");
            }

/*
        System.out.println(
                "'/home/user/Photos/asiaphotos.zip' is successfully uploaded as "
                        + "object 'asiaphotos-2015.zip' to bucket 'asiatrip'.");
*/

            minioClient.close();

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
