package applicationServiceTest;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.SchemaFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContentType;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsMessageTargetInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.CommonObjectsMessageSender;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.MessageSentEventHandler;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.nio.ByteBuffer;
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
        commonObjectsPayloadContent.setIncludingContent(CommonObjectsPayloadContentType.ALL);
        commonObjectsPayloadContent.setTextContentEncoded(true);

        String textContent="Message 0123456789023456789中文Ωß∂ç√∂©©ƒƒß≈√ƒ";
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("field001",1);
        node.put("field002",new Date().getTime());
        node.put("field003",textContent);
        byte[] encodedBytes = Base64.encodeBase64(node.toString().getBytes());
        commonObjectsPayloadContent.setTextContentEncodeAlgorithm("BASE64");
        commonObjectsPayloadContent.setTextContent(new String(encodedBytes));

        byte[] fileBteArray=getBytes("testresource/testPic.jpg");
        ByteBuffer buffer= ByteBuffer.wrap(fileBteArray);
        commonObjectsPayloadContent.setBinaryContent(buffer);

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

    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
