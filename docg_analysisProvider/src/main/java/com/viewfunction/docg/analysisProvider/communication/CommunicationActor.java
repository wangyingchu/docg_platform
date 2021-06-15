package com.viewfunction.docg.analysisProvider.communication;

import akka.actor.ActorSelection;
import akka.actor.UntypedAbstractActor;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisProvider.util.PropertyHandler;

public class CommunicationActor extends UntypedAbstractActor {

    private ActorSelection remoteActor = null;

    @Override
    public void preStart(){
        String providerCommunicationHostName= PropertyHandler.getConfigPropertyValue("providerCommunicationHostName");
        String providerCommunicationPort= PropertyHandler.getConfigPropertyValue("providerCommunicationPort");
        String path = "akka://DOCGAnalysisProviderCommunicationSystem@"+providerCommunicationHostName+":"+providerCommunicationPort+"/user/communicationRouter";
        remoteActor = getContext().actorSelection(path);
    }

    @Override
    public void onReceive(Object msg){
        if(msg instanceof AnalyseRequest){
            remoteActor.tell(msg,getSelf());
        }else if(msg instanceof AnalyseResponse){
            //handle async analyse response
            AsyncAnalyseResponseProcessor.processAsyncAnalyseResponse((AnalyseResponse)msg);
        }else{
            unhandled(msg);
        }
    }
}
