package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;

import java.net.InetAddress;
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

    public static void  newConceptionEntities(String conceptionKindName, List<ConceptionEntityValue> conceptionEntityValueList, boolean addPerDefinedRelation){}

    public static void  updateConceptionEntities(String conceptionKindName, List<ConceptionEntityValue> conceptionEntityValueList){}

    public static void  deleteConceptionEntities(String conceptionKindName, List<String> conceptionEntityUIDList){}
}
