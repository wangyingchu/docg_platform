package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.conceptionEntitiesExtract;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationPayload;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageHandler;

import java.util.List;

public class GeneralConceptionEntityValueOperationsMessageHandler extends ConceptionEntityValueOperationsMessageHandler {

    @Override
    public void handleConceptionEntityOperationContents(List<? extends ConceptionEntityValueOperationPayload> infoObjectValueOperationPayloads) {
        long totalHandledNum = 0;

        for(ConceptionEntityValueOperationPayload currentConceptionEntityValueOperationPayload:infoObjectValueOperationPayloads){

            ConceptionEntityValueOperationContent conceptionEntityValueOperationContent = currentConceptionEntityValueOperationPayload.getConceptionEntityValueOperationContent();
            ConceptionEntityValue conceptionEntityValue = currentConceptionEntityValueOperationPayload.getConceptionEntityValue();

            System.out.println(currentConceptionEntityValueOperationPayload.getPayloadOffset());
            System.out.println(currentConceptionEntityValueOperationPayload.getPayloadKey());
            System.out.println(conceptionEntityValue.getEntityAttributesValue());
            System.out.println(conceptionEntityValue.getConceptionEntityUID());

            System.out.println(conceptionEntityValueOperationContent.getConceptionEntityUID());
            System.out.println(conceptionEntityValueOperationContent.getEntityAttributesValue());
            System.out.println(conceptionEntityValueOperationContent.getOperationType());
            System.out.println(conceptionEntityValueOperationContent.getConceptionKindName());
            System.out.println(conceptionEntityValueOperationContent.getCoreRealmName());
            System.out.println(conceptionEntityValueOperationContent.getSenderId());
            System.out.println(conceptionEntityValueOperationContent.getSenderIP());
            System.out.println(conceptionEntityValueOperationContent.getSendTime());
            System.out.println(conceptionEntityValueOperationContent.isAddPerDefinedRelation());

            System.out.println("=----------------------------------=");
            totalHandledNum++;
        }
        System.out.println("totalHandledNum = "+totalHandledNum);
    }
}
