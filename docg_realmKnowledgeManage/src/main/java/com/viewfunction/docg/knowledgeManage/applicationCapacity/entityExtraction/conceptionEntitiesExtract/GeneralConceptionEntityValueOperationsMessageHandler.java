package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.conceptionEntitiesExtract;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.EntityExtractionApplication;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationPayload;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationType;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageHandler;
import com.viewfunction.docg.knowledgeManage.consoleApplication.util.ApplicationLauncherUtil;

import java.util.*;

public class GeneralConceptionEntityValueOperationsMessageHandler extends ConceptionEntityValueOperationsMessageHandler {

    private Map<Object,Object> commandContextDataMap;
    private int degreeOfParallelism = 1;
    private boolean autoCreateConceptionKind = false;
    private List<String> existConceptionKindList;

    public GeneralConceptionEntityValueOperationsMessageHandler(Map<Object,Object> commandContextDataMap){
        this.commandContextDataMap = commandContextDataMap;
        String degreeOfParallelismStr = ApplicationLauncherUtil.getApplicationInfoPropertyValue("EntityExtraction.MessageHandle.degreeOfParallelism");
        this.degreeOfParallelism = Integer.valueOf(degreeOfParallelismStr);
        String autoCreateConceptionKindStr = ApplicationLauncherUtil.getApplicationInfoPropertyValue("EntityExtraction.MessageHandle.autoCreateConceptionKind");
        this.autoCreateConceptionKind = Boolean.valueOf(autoCreateConceptionKindStr);
        this.existConceptionKindList = new ArrayList<>();
    }

    @Override
    public void handleConceptionEntityOperationContents(List<? extends ConceptionEntityValueOperationPayload> conceptionEntityValueOperationPayloads) {

        Date currentDate = new Date();
        Map<String,List<ConceptionEntityValue>> conceptionEntitiesKindGroupMap_INSERT = new HashMap<>();
        Map<String,List<ConceptionEntityValue>> conceptionEntitiesKindGroupMap_UPDATE = new HashMap<>();
        Map<String,List<String>> conceptionEntitiesKindGroupMap_DELETE = new HashMap<>();

        long fromOffset = 0;
        long toOffset = 0;

        for(int i = 0;i < conceptionEntityValueOperationPayloads.size(); i++){
            ConceptionEntityValueOperationPayload currentConceptionEntityValueOperationPayload = conceptionEntityValueOperationPayloads.get(i);
            ConceptionEntityValueOperationContent conceptionEntityValueOperationContent = currentConceptionEntityValueOperationPayload.getConceptionEntityValueOperationContent();

            String targetCoreRealmName = conceptionEntityValueOperationContent.getCoreRealmName();
            String targetConceptionKindName = conceptionEntityValueOperationContent.getConceptionKindName();
            ConceptionEntityValueOperationType currentDataOperationType = conceptionEntityValueOperationContent.getOperationType();
            ConceptionEntityValue conceptionEntityValue = currentConceptionEntityValueOperationPayload.getConceptionEntityValue();

            switch(currentDataOperationType){
                case INSERT:
                    prepareConceptionKindDataForModifyOperation(conceptionEntitiesKindGroupMap_INSERT,targetConceptionKindName,conceptionEntityValue);
                    break;
                case UPDATE:
                    prepareConceptionKindDataForModifyOperation(conceptionEntitiesKindGroupMap_UPDATE,targetConceptionKindName,conceptionEntityValue);
                    break;
                case DELETE:
                    prepareConceptionKindDataForDeleteOperation(conceptionEntitiesKindGroupMap_DELETE,targetConceptionKindName,conceptionEntityValue);
                    break;
            }

            if(i == 0){
                fromOffset = currentConceptionEntityValueOperationPayload.getPayloadOffset();
            }
            if(i == conceptionEntityValueOperationPayloads.size() - 1){
                toOffset = currentConceptionEntityValueOperationPayload.getPayloadOffset();
            }

            /*
            System.out.println(currentConceptionEntityValueOperationPayload.getPayloadOffset());
            System.out.println(currentConceptionEntityValueOperationPayload.getPayloadKey());
            System.out.println(conceptionEntityValue.getEntityAttributesValue());
            System.out.println(conceptionEntityValue.getConceptionEntityUID());
            System.out.println(conceptionEntityValueOperationContent.getConceptionEntityUID());
            System.out.println(conceptionEntityValueOperationContent.getEntityAttributesValue());
            System.out.println(conceptionEntityValueOperationContent.getSenderId());
            System.out.println(conceptionEntityValueOperationContent.getSenderIP());
            System.out.println(conceptionEntityValueOperationContent.getSendTime());
            System.out.println(conceptionEntityValueOperationContent.isAddPerDefinedRelation());
            */
        }

        StringBuffer appInfoMessageStringBuffer=new StringBuffer();
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("--------------------------------------------------------------------------");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("Received batch entity operation request at: "+ currentDate.toString());
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("Conception entity request number:           "+ conceptionEntityValueOperationPayloads.size());
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("--------------------------------------------------------------------------");

        System.out.println(appInfoMessageStringBuffer.toString());
        System.out.print(">_");

        List<String> messageReceiveHistoryList = (List<String>)commandContextDataMap.get(EntityExtractionApplication.MESSAGE_RECEIVE_HISTORY);

        StringBuffer currentReceiveHistoryStringBuffer=new StringBuffer();
        currentReceiveHistoryStringBuffer.append("Received operation request at: "+ currentDate.toString());
        currentReceiveHistoryStringBuffer.append("\n\r");
        currentReceiveHistoryStringBuffer.append("Entity request number:         "+ conceptionEntityValueOperationPayloads.size());
        currentReceiveHistoryStringBuffer.append("\n\r");
        currentReceiveHistoryStringBuffer.append("OffsetRage:                    "+ fromOffset + " to " + toOffset);
        messageReceiveHistoryList.add(currentReceiveHistoryStringBuffer.toString());

        Set<String> conceptionKindNameSet = conceptionEntitiesKindGroupMap_INSERT.keySet();
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        for(String currentConceptionKindName:conceptionKindNameSet){
            List<ConceptionEntityValue> targetConceptionEntityValueList = conceptionEntitiesKindGroupMap_INSERT.get(currentConceptionKindName);
            if(existConceptionKindList.contains(currentConceptionKindName)){
                BatchDataOperationUtil.batchAddNewEntities(currentConceptionKindName,targetConceptionEntityValueList,this.degreeOfParallelism);
            }else{
                if(coreRealm.getConceptionKind(currentConceptionKindName) == null){
                    if(this.autoCreateConceptionKind){
                        ConceptionKind newCreatedConceptionKind = coreRealm.createConceptionKind(currentConceptionKindName,"AutoCreatedByEntityExtractionOperation");
                        if(newCreatedConceptionKind != null){
                            existConceptionKindList.add(currentConceptionKindName);
                            BatchDataOperationUtil.batchAddNewEntities(currentConceptionKindName,targetConceptionEntityValueList,this.degreeOfParallelism);
                        }
                    }else{
                        break;
                    }
                }else{
                    existConceptionKindList.add(currentConceptionKindName);
                    BatchDataOperationUtil.batchAddNewEntities(currentConceptionKindName,targetConceptionEntityValueList,this.degreeOfParallelism);
                }
            }
        }
    }

    private void prepareConceptionKindDataForModifyOperation(Map<String,List<ConceptionEntityValue>> conceptionEntitiesKindGroupMap,
                                                             String targetConceptionKindName,ConceptionEntityValue conceptionEntityValue){
        if(conceptionEntityValue != null) {
            if (!conceptionEntitiesKindGroupMap.containsKey(targetConceptionKindName)) {
                List<ConceptionEntityValue> conceptionKindEntityList = new ArrayList<>();
                conceptionEntitiesKindGroupMap.put(targetConceptionKindName, conceptionKindEntityList);
            }
            List<ConceptionEntityValue> targetConceptionKindEntityList = conceptionEntitiesKindGroupMap.get(targetConceptionKindName);
            targetConceptionKindEntityList.add(conceptionEntityValue);
        }
    }

    private void prepareConceptionKindDataForDeleteOperation( Map<String,List<String>> conceptionEntitiesKindGroupMap,
                                                             String targetConceptionKindName,ConceptionEntityValue conceptionEntityValue){
        if(conceptionEntityValue != null && conceptionEntityValue.getConceptionEntityUID() != null) {
            if (!conceptionEntitiesKindGroupMap.containsKey(targetConceptionKindName)) {
                List<String> conceptionKindEntityUIDList = new ArrayList<>();
                conceptionEntitiesKindGroupMap.put(targetConceptionKindName, conceptionKindEntityUIDList);
            }
            List<String> targetConceptionKindEntityUIDList = conceptionEntitiesKindGroupMap.get(targetConceptionKindName);
            targetConceptionKindEntityUIDList.add(conceptionEntityValue.getConceptionEntityUID());
        }
    }
}
