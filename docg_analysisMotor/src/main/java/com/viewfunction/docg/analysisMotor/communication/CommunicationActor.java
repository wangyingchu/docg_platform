package com.viewfunction.docg.analysisMotor.communication;

import akka.actor.ActorSelection;
import akka.actor.UntypedAbstractActor;
import com.viewfunction.docg.analysisMotor.feature.communication.messagePayload.AnalyseRequest;
import com.viewfunction.docg.analysisMotor.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisMotor.util.PropertyHandler;

public class CommunicationActor extends UntypedAbstractActor {

    private ActorSelection remoteActor = null;

    @Override
    public void preStart(){
        String engineCommunicationHostName= PropertyHandler.getConfigPropertyValue("engineCommunicationHostName");
        String engineCommunicationPort= PropertyHandler.getConfigPropertyValue("engineCommunicationPort");
        String path = "akka://CIMAnalysisEngineCommunicationSystem@"+engineCommunicationHostName+":"+engineCommunicationPort+"/user/communicationRouter";
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
