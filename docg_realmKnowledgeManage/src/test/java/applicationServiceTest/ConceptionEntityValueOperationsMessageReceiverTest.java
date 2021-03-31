package applicationServiceTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationPayload;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageReceiver;

import java.util.List;

public class ConceptionEntityValueOperationsMessageReceiverTest {

    public static void main(String[] args) throws MessageHandleErrorException, ConfigurationErrorException{
        doReceive();
    }

    private static void doReceive() throws ConfigurationErrorException, MessageHandleErrorException {

        ConceptionEntityValueOperationsMessageHandler conceptionEntityValueOperationsMessageHandler = new ConceptionEntityValueOperationsMessageHandler() {
            long totalHandledNum = 0;
            @Override
            public void handleConceptionEntityOperationContents(List<? extends ConceptionEntityValueOperationPayload> infoObjectValueOperationPayloads) {
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
        };

        ConceptionEntityValueOperationsMessageReceiver conceptionEntityValueOperationsMessageReceiver = new ConceptionEntityValueOperationsMessageReceiver(conceptionEntityValueOperationsMessageHandler);
        conceptionEntityValueOperationsMessageReceiver.startMessageReceive(new String[]{"DefaultCoreRealm"});
    }
}
