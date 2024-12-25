package com.viewfunction.docg.analysisProvider.client;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viewfunction.docg.analysisProvider.client.exception.AnalyseResponseFormatException;
import com.viewfunction.docg.analysisProvider.client.exception.AnalysisEngineRuntimeException;
import com.viewfunction.docg.analysisProvider.client.exception.ProviderClientInitException;
import com.viewfunction.docg.analysisProvider.communication.CommunicationActor;
import com.viewfunction.docg.analysisProvider.feature.communication.AnalyseResponseCallback;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.admin.AnalysisProviderPingRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.admin.AnalysisProviderRunningStatusRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.admin.FunctionalFeatureRunningStatusRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.admin.FunctionalFeaturesInfoRequest;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.FeatureRunningInfo;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.FunctionalFeatureInfo;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.ProviderRunningInfo;
import com.viewfunction.docg.analysisProvider.util.PropertyHandler;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AnalysisProviderAdminClient {

    private String hostName;
    private int hostPort;
    private ActorSystem actorSystem;
    private ActorRef localCommunicationActor;
    private ActorSelection remoteCommunicationActor;

    public AnalysisProviderAdminClient(String hostName, int hostPort){
        this.hostName = hostName;
        this.hostPort = hostPort;
    }

    private void openSession(){
        String configStr = "akka{" +
                "actor {"+
                "provider = cluster," +
                "serializers {  " +
                "kryo = \"com.twitter.chill.akka.AkkaSerializer\"," +
                "java = \"akka.serialization.JavaSerializer\""+
                "}," +
                "serialization-bindings {" +
                "\"com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest\" = kryo,"+
                "\"com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse\" = kryo"+
                "}" +
                "},"+
                "remote {" +
                "artery {" +
                "transport = tcp," +
                "canonical.hostname = \""+this.hostName+"\"," +
                "canonical.port = "+this.hostPort+
                "}" +
                "}," +
                "loglevel=ERROR" +
                "}";
        Config config = ConfigFactory.parseString(configStr);
        actorSystem = ActorSystem.create("AnalysisClientCommunicationSystem", config);
        localCommunicationActor = actorSystem.actorOf(Props.create(CommunicationActor.class), "localCommunicationActor");

        String providerCommunicationHostName= PropertyHandler.getConfigPropertyValue("providerCommunicationHostName");
        String providerCommunicationPort= PropertyHandler.getConfigPropertyValue("providerCommunicationPort");
        String path = "akka://DOCGAnalysisProviderCommunicationSystem@"+providerCommunicationHostName+":"+providerCommunicationPort+"/user/communicationRouter";
        remoteCommunicationActor = actorSystem.actorSelection(path);
    }

    private void closeSession() throws ProviderClientInitException {
        if(actorSystem != null){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            actorSystem.terminate();
        }else{
            throw new ProviderClientInitException();
        }
    }

    private void sendAnalyseRequest(AnalyseRequest analyseRequest, AnalyseResponseCallback analyseResponseCallback, int timeoutSecond) throws ProviderClientInitException, AnalysisEngineRuntimeException {
        if(remoteCommunicationActor != null){
            if(analyseRequest != null){
                analyseRequest.generateMetaInfo();
                Timeout timeout = new Timeout(Duration.create(timeoutSecond, TimeUnit.SECONDS));
                Future<Object> future = Patterns.ask(remoteCommunicationActor, analyseRequest, timeout);
                future.onComplete(new OnComplete<Object>() {
                    @Override
                    public void onComplete(Throwable throwable, Object o) throws Throwable {
                        if (throwable != null) {
                            //System.out.println("返回结果异常：" + throwable.getMessage());
                            throwable.printStackTrace();
                        } else {
                            //System.out.println("返回消息：" + o);
                            analyseResponseCallback.onResponseReceived(o);
                        }
                    }
                }, actorSystem.dispatcher());
                // 成功，执行过程
                future.onSuccess(new OnSuccess<Object>() {
                    @Override
                    public void onSuccess(Object msg) throws Throwable {
                        //System.out.println("回复的消息：" + msg);
                        if(msg instanceof AnalyseResponse){
                            analyseResponseCallback.onSuccessResponseReceived((AnalyseResponse)msg);
                        }else{
                            analyseResponseCallback.onFailureResponseReceived(new AnalyseResponseFormatException());
                        }
                    }
                }, actorSystem.dispatcher());
                //失败，执行过程
                future.onFailure(new OnFailure() {
                    @Override
                    public void onFailure(Throwable throwable) throws Throwable {
                        if (throwable instanceof TimeoutException) {
                            //System.out.println("服务超时");
                            throwable.printStackTrace();
                        } else {
                            //System.out.println("未知错误");
                        }
                        analyseResponseCallback.onFailureResponseReceived(throwable);
                    }
                }, actorSystem.dispatcher());
            }else{
                throw new AnalysisEngineRuntimeException();
            }
        }else{
            throw new ProviderClientInitException();
        }
    }

    private void sendAnalyseRequest(AnalyseRequest analyseRequest) throws ProviderClientInitException,AnalysisEngineRuntimeException{
        if(localCommunicationActor != null){
            if(analyseRequest != null){
                analyseRequest.generateMetaInfo();
                localCommunicationActor.tell(analyseRequest,localCommunicationActor);
            }else{
                throw new AnalysisEngineRuntimeException();
            }
        }else{
            throw new ProviderClientInitException();
        }
    }

    public interface PingAnalysisProviderCallback {
        public void onPingSuccess();
        public void onPingFail();
    }

    public void pingAnalysisProvider(PingAnalysisProviderCallback pingAnalysisProviderCallback,int pingTimeOutInSecond) {
        try {
            openSession();
            AnalysisProviderPingRequest analysisProviderPingRequest = new AnalysisProviderPingRequest();
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {}

                @Override
                public void onSuccessResponseReceived(AnalyseResponse analyseResponse) {
                    try {
                        pingAnalysisProviderCallback.onPingSuccess();
                        closeSession();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailureResponseReceived(Throwable throwable) {
                    try {
                        pingAnalysisProviderCallback.onPingFail();
                        closeSession();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }
            };

            sendAnalyseRequest(analysisProviderPingRequest,analyseResponseCallback,pingTimeOutInSecond);
        } catch (ProviderClientInitException e) {
            throw new RuntimeException(e);
        } catch (AnalysisEngineRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public interface ListFunctionalFeaturesCallback {
        public void onExecutionSuccess(List<FunctionalFeatureInfo> functionalFeatureInfoList);
        public void onExecutionFail();
    }

    public void listFunctionalFeatures(ListFunctionalFeaturesCallback listFunctionalFeaturesCallback,int listTimeOutInSecond) {
        try {
            openSession();
            FunctionalFeaturesInfoRequest functionalFeaturesInfoRequest = new FunctionalFeaturesInfoRequest();
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {}

                @Override
                public void onSuccessResponseReceived(AnalyseResponse analyseResponse) {
                    try {
                        List<FunctionalFeatureInfo> functionalFeatureInfoList = null;
                        if(analyseResponse.getResponseData() != null){
                            functionalFeatureInfoList = (List<FunctionalFeatureInfo>)analyseResponse.getResponseData();
                        }
                        closeSession();
                        listFunctionalFeaturesCallback.onExecutionSuccess(functionalFeatureInfoList);
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailureResponseReceived(Throwable throwable) {
                    try {
                        closeSession();
                        listFunctionalFeaturesCallback.onExecutionFail();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }
            };

            sendAnalyseRequest(functionalFeaturesInfoRequest,analyseResponseCallback,listTimeOutInSecond);
        } catch (ProviderClientInitException e) {
            throw new RuntimeException(e);
        } catch (AnalysisEngineRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public interface ListProviderRunningStatusCallback {
        public void onExecutionSuccess(List<ProviderRunningInfo> providerRunningInfoList);
        public void onExecutionFail();
    }

    public void listProviderRunningStatus(ListProviderRunningStatusCallback listProviderRunningStatusCallback,int listTimeOutInSecond) {
        try {
            openSession();
            AnalysisProviderRunningStatusRequest analysisProviderRunningStatusRequest = new AnalysisProviderRunningStatusRequest();
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {}

                @Override
                public void onSuccessResponseReceived(AnalyseResponse analyseResponse) {
                    try {
                        List<ProviderRunningInfo> providerRunningInfoList = null;
                        if(analyseResponse.getResponseData() != null){
                            providerRunningInfoList = (List<ProviderRunningInfo>)analyseResponse.getResponseData();
                        }
                        closeSession();
                        listProviderRunningStatusCallback.onExecutionSuccess(providerRunningInfoList);
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailureResponseReceived(Throwable throwable) {
                    try {
                        closeSession();
                        listProviderRunningStatusCallback.onExecutionFail();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }
            };

            sendAnalyseRequest(analysisProviderRunningStatusRequest,analyseResponseCallback,listTimeOutInSecond);
        } catch (ProviderClientInitException e) {
            throw new RuntimeException(e);
        } catch (AnalysisEngineRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public interface ListFeatureRunningStatusCallback {
        public void onExecutionSuccess(List<FeatureRunningInfo> featureRunningInfo);
        public void onExecutionFail();
    }

    public void listFeatureRunningStatus(ListFeatureRunningStatusCallback listFeatureRunningStatusCallback,int listTimeOutInSecond) {
        try {
            openSession();
            FunctionalFeatureRunningStatusRequest functionalFeatureRunningStatusRequest = new FunctionalFeatureRunningStatusRequest();
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {

                    System.out.println("listFeatureRunningStatus");
                    System.out.println("listFeatureRunningStatus");
                }

                @Override
                public void onSuccessResponseReceived(AnalyseResponse analyseResponse) {
                    try {
                        List<FeatureRunningInfo> featureRunningInfoList = null;
                        if(analyseResponse.getResponseData() != null){
                            featureRunningInfoList = (List<FeatureRunningInfo>)analyseResponse.getResponseData();
                        }
                        closeSession();
                        listFeatureRunningStatusCallback.onExecutionSuccess(featureRunningInfoList);
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailureResponseReceived(Throwable throwable) {
                    try {
                        closeSession();
                        listFeatureRunningStatusCallback.onExecutionFail();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }
            };

            sendAnalyseRequest(functionalFeatureRunningStatusRequest,analyseResponseCallback,listTimeOutInSecond);
        } catch (ProviderClientInitException e) {
            throw new RuntimeException(e);
        } catch (AnalysisEngineRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
