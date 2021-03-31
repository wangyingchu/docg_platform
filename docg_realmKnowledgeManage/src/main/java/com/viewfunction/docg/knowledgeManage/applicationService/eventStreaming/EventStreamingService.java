package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.SchemaFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationType;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.ConceptionEntityValueOperationsMessageSender;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.MessageSentEventHandler;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

public class EventStreamingService {

    private static String senderId =null;
    private static String senderIP =null;

    static{
        InetAddress ia=null;
        try {
            ia=ia.getLocalHost();
            senderId=ia.getHostName();
            senderIP=ia.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void newConceptionEntities(String coreRealmName,String conceptionKindName, List<ConceptionEntityValue> conceptionEntityValueList, boolean addPerDefinedRelation, MessageSentEventHandler messageSentEventHandler){
        ConceptionEntityValueOperationsMessageSender conceptionEntityValueOperationsMessageSender;
        try {
            conceptionEntityValueOperationsMessageSender = new ConceptionEntityValueOperationsMessageSender(messageSentEventHandler);
            conceptionEntityValueOperationsMessageSender.beginMessageSendBatch();
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                ConceptionEntityValueOperationContent conceptionEntityValueOperationContent = generateOperationContent(coreRealmName,conceptionKindName);
                conceptionEntityValueOperationContent.setOperationType(ConceptionEntityValueOperationType.INSERT);
                conceptionEntityValueOperationContent.setAddPerDefinedRelation(addPerDefinedRelation);
                conceptionEntityValueOperationsMessageSender.sendConceptionEntityValueOperationMessage(conceptionEntityValueOperationContent,currentConceptionEntityValue);
            }
            conceptionEntityValueOperationsMessageSender.finishMessageSendBatch();
        } catch (ConfigurationErrorException e) {
            e.printStackTrace();
        } catch (MessageFormatErrorException e) {
            e.printStackTrace();
        } catch (MessageHandleErrorException e) {
            e.printStackTrace();
        } catch (SchemaFormatErrorException e) {
            e.printStackTrace();
        }
    }

    public static void  updateConceptionEntities(String coreRealmName,String conceptionKindName, List<ConceptionEntityValue> conceptionEntityValueList,MessageSentEventHandler messageSentEventHandler){
        ConceptionEntityValueOperationsMessageSender conceptionEntityValueOperationsMessageSender;
        try {
            conceptionEntityValueOperationsMessageSender = new ConceptionEntityValueOperationsMessageSender(messageSentEventHandler);
            conceptionEntityValueOperationsMessageSender.beginMessageSendBatch();
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                ConceptionEntityValueOperationContent conceptionEntityValueOperationContent = generateOperationContent(coreRealmName,conceptionKindName);
                conceptionEntityValueOperationContent.setOperationType(ConceptionEntityValueOperationType.UPDATE);
                conceptionEntityValueOperationContent.setConceptionEntityUID(currentConceptionEntityValue.getConceptionEntityUID());
                conceptionEntityValueOperationsMessageSender.sendConceptionEntityValueOperationMessage(conceptionEntityValueOperationContent,currentConceptionEntityValue);
            }
            conceptionEntityValueOperationsMessageSender.finishMessageSendBatch();
        } catch (ConfigurationErrorException e) {
            e.printStackTrace();
        } catch (MessageFormatErrorException e) {
            e.printStackTrace();
        } catch (MessageHandleErrorException e) {
            e.printStackTrace();
        } catch (SchemaFormatErrorException e) {
            e.printStackTrace();
        }
    }

    public static void  deleteConceptionEntities(String coreRealmName,String conceptionKindName, List<String> conceptionEntityUIDList){}

    private static ConceptionEntityValueOperationContent generateOperationContent(String coreRealmName,String conceptionKindName){
        ConceptionEntityValueOperationContent conceptionEntityValueOperationContent =new ConceptionEntityValueOperationContent();
        conceptionEntityValueOperationContent.setCoreRealmName(coreRealmName);
        conceptionEntityValueOperationContent.setConceptionKindName(conceptionKindName);
        conceptionEntityValueOperationContent.setSenderId(senderId);
        conceptionEntityValueOperationContent.setSenderIP(senderIP);
        conceptionEntityValueOperationContent.setSendTime(new Date().getTime());
        return conceptionEntityValueOperationContent;
    }
}
