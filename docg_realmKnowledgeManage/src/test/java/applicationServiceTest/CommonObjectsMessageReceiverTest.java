package applicationServiceTest;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsReceivedMessage;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.CommonObjectsMessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.CommonObjectsMessageReceiver;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonObjectsMessageReceiverTest {

    public static void main(String[] args) throws ConfigurationErrorException {
        CommonObjectsMessageHandler commonObjectsMessageHandler = new CommonObjectsMessageHandler() {
            @Override
            protected void operateRecord(Object recordKey, CommonObjectsReceivedMessage receivedMessage, long recordOffset) {
                System.out.println(receivedMessage);
                CommonObjectsPayloadMetaInfo commonObjectsPayloadMetaInfo = receivedMessage.getMessageCommonObjectsPayloadMetaInfo();
                CommonObjectsPayloadContent commonObjectsPayloadContent = receivedMessage.getCommonObjectsPayloadContent();

                System.out.println("============================================");
                System.out.println("SendTime: "+new Date(receivedMessage.getMessageSendTime()));
                System.out.println("ReceivedTime: "+new Date(receivedMessage.getMessageReceivedTime()));
                System.out.println("----------");
                System.out.println("SenderIP： "+ commonObjectsPayloadMetaInfo.getSenderIP());
                System.out.println("SenderId： "+ commonObjectsPayloadMetaInfo.getSenderId());
                System.out.println("SenderGroup： "+ commonObjectsPayloadMetaInfo.getSenderGroup());
                System.out.println("SenderCategory： "+ commonObjectsPayloadMetaInfo.getSenderCategory());
                System.out.println("PayloadClassification： "+ commonObjectsPayloadMetaInfo.getPayloadClassification());
                System.out.println("PayloadProcessor： "+ commonObjectsPayloadMetaInfo.getPayloadProcessor());
                System.out.println("PayloadType： "+ commonObjectsPayloadMetaInfo.getPayloadType());
                System.out.println("PayloadTypeDesc： "+ commonObjectsPayloadMetaInfo.getPayloadTypeDesc());
                System.out.println("----------");

                System.out.println("IncludingContent: "+ commonObjectsPayloadContent.getIncludingContent());
                System.out.println("TextContentEncoded: "+ commonObjectsPayloadContent.getTextContentEncoded());
                System.out.println("TextContentEncodeAlgorithm: "+ commonObjectsPayloadContent.getTextContentEncodeAlgorithm());
                if(commonObjectsPayloadContent.getTextContentEncoded()){
                    if(commonObjectsPayloadContent.getTextContentEncodeAlgorithm().equals("BASE64")){
                        byte[] decodedBytes = Base64.decodeBase64(commonObjectsPayloadContent.getTextContent().getBytes());
                        System.out.println("TextContent: "+new String(decodedBytes));
                    }else{
                        System.out.println("TextContent: "+ commonObjectsPayloadContent.getTextContent());
                    }
                }else{
                    System.out.println("TextContent: "+ commonObjectsPayloadContent.getTextContent());
                }
                ByteBuffer ByteBuffer= commonObjectsPayloadContent.getBinaryContent();
                System.out.println("BinaryContent: "+ByteBuffer);
                getFile(ByteBuffer.array(),"testresult/","RECEIVED-"+new Date().getTime()+".jpg");
                System.out.println("============================================");
            }
        };

        final CommonObjectsMessageReceiver commonObjectsMessageReceiver =new CommonObjectsMessageReceiver(commonObjectsMessageHandler);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            public void run() {
                try {
                    commonObjectsMessageReceiver.startMessageReceive(new String[]{"CommonObjectsTopic"});
                } catch (ConfigurationErrorException e) {
                    e.printStackTrace();
                } catch (MessageHandleErrorException e) {
                    e.printStackTrace();
                }
            }
        });

        //Thread.sleep(5000);
        //executor.shutdown();
    }

    public static void getFile(byte[] bfile, String filePath,String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+""+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
