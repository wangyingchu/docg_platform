package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.objectStore;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.ExternalAttributesValueAccessProcessor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
        return List.of();
    }

    @Override
    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
    }

    @Override
    public Long deleteEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
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
