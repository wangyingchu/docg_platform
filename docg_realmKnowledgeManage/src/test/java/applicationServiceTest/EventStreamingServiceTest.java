package applicationServiceTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.EventStreamingService;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.MessageSentEventHandler;

import java.util.*;

public class EventStreamingServiceTest {

    public static void main(String[] args){

        String coreRealmName = "DefaultCoreRealm";
        String conceptionKindName = "ConceptionKindA";
        boolean addPerDefinedRelation = true;
        MessageSentEventHandler messageSentEventHandler = new MessageSentEventHandler() {

            @Override
            public void operateMetaData(long offset, long timestamp, String topic, int partition) {
                System.out.println(offset + timestamp +topic+partition);
            }
        };

        List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();

        for(int i=0;i<10;i++){
            ConceptionEntityValue conceptionEntityValue =new ConceptionEntityValue();
            Map<String,Object> entityPropertyMap = new HashMap<>();
            entityPropertyMap.put("baseprop1","hello world");
            entityPropertyMap.put("baseprop2",100);
            entityPropertyMap.put("baseprop3",new Date());
            entityPropertyMap.put("generalprop1","hello world again");
            entityPropertyMap.put("ProjectId","ProjectId-"+i);
            conceptionEntityValue.setEntityAttributesValue(entityPropertyMap);
            conceptionEntityValueList.add(conceptionEntityValue);
        }

        EventStreamingService.newConceptionEntities(coreRealmName,conceptionKindName,conceptionEntityValueList,addPerDefinedRelation,messageSentEventHandler);

        conceptionEntityValueList.clear();
        for(int i=0;i<10;i++){
            ConceptionEntityValue conceptionEntityValue =new ConceptionEntityValue();
            conceptionEntityValue.setConceptionEntityUID(""+new Date().getTime());
            Map<String,Object> entityPropertyMap = new HashMap<>();
            entityPropertyMap.put("baseprop3",new Date());
            conceptionEntityValue.setEntityAttributesValue(entityPropertyMap);
            conceptionEntityValueList.add(conceptionEntityValue);
        }
        EventStreamingService.updateConceptionEntities(coreRealmName,conceptionKindName,conceptionEntityValueList,messageSentEventHandler);

    }
}
