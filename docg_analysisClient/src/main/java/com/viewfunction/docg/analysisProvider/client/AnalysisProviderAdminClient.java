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
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.admin.*;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.FeatureRunningInfo;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.FunctionalFeatureInfo;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.ProviderRunningInfo;
import com.viewfunction.docg.analysisProvider.util.PropertyHandler;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class AnalysisProviderAdminClient {

    private String hostName;
    private int hostPort;
    private ActorSystem actorSystem;
    private ActorRef localCommunicationActor;
    private ActorSelection remoteCommunicationActor;
    private int timeoutInSecond = 3;

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

    public int getTimeoutInSecond() {
        return timeoutInSecond;
    }

    public void setTimeoutInSecond(int timeoutInSecond) {
        this.timeoutInSecond = timeoutInSecond;
    }

    public interface PingAnalysisProviderCallback {
        public void onPingSuccess();
        public void onPingFail();
    }

    public void pingAnalysisProvider(PingAnalysisProviderCallback pingAnalysisProviderCallback,int pingTimeOutInSecond) {
        try {
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

            AnalysisProviderPingRequest analysisProviderPingRequest = new AnalysisProviderPingRequest();
            openSession();
            sendAnalyseRequest(analysisProviderPingRequest,analyseResponseCallback,pingTimeOutInSecond);
        } catch (ProviderClientInitException e) {
            throw new RuntimeException(e);
        } catch (AnalysisEngineRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean pingAnalysisProvider(){
        List<Boolean> resultList = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        java.util.concurrent.Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                AnalysisProviderAdminClient.PingAnalysisProviderCallback pingAnalysisProviderCallback = new AnalysisProviderAdminClient.PingAnalysisProviderCallback() {
                    @Override
                    public void onPingSuccess() {
                        resultList.add(Boolean.TRUE);
                    }

                    @Override
                    public void onPingFail() {}
                };
                pingAnalysisProvider(pingAnalysisProviderCallback,timeoutInSecond);
                //need this sleep to make sure the callback is executed
                Thread.sleep(timeoutInSecond*1000);
                return "";
            }
        });

        try {
            // 同步等待异步操作结果
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭ExecutorService
            executor.shutdown();
        }

        if(resultList.size()>0){
            return resultList.get(0);
        }else{
            return false;
        }
    }

    public interface ListFunctionalFeaturesCallback {
        public void onExecutionSuccess(List<FunctionalFeatureInfo> functionalFeatureInfoList);
        public void onExecutionFail();
    }

    public void listFunctionalFeatures(ListFunctionalFeaturesCallback listFunctionalFeaturesCallback,int listTimeOutInSecond) {
        try {
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
            FunctionalFeaturesInfoRequest functionalFeaturesInfoRequest = new FunctionalFeaturesInfoRequest();
            openSession();
            sendAnalyseRequest(functionalFeaturesInfoRequest,analyseResponseCallback,listTimeOutInSecond);
        } catch (ProviderClientInitException e) {
            throw new RuntimeException(e);
        } catch (AnalysisEngineRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FunctionalFeatureInfo> listFunctionalFeatures(){
        Map<String,List<FunctionalFeatureInfo>> resultDataMap = new HashMap<>();
        final String listFunctionalFeaturesKey = "FunctionalFeatureInfoList";

        ExecutorService executor = Executors.newFixedThreadPool(1);
        java.util.concurrent.Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                AnalysisProviderAdminClient.ListFunctionalFeaturesCallback listFunctionalFeaturesCallback = new AnalysisProviderAdminClient.ListFunctionalFeaturesCallback() {
                    @Override
                    public void onExecutionSuccess(List<FunctionalFeatureInfo> functionalFeatureInfoList) {
                        if(functionalFeatureInfoList != null){
                            resultDataMap.put(listFunctionalFeaturesKey,functionalFeatureInfoList);

                        }
                    }

                    @Override
                    public void onExecutionFail() {}
                };
                listFunctionalFeatures(listFunctionalFeaturesCallback,timeoutInSecond);
                //need this sleep to make sure the callback is executed
                Thread.sleep(timeoutInSecond*1000);
                return "";
            }
        });

        try {
            // 同步等待异步操作结果
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭ExecutorService
            executor.shutdown();
        }

        if(resultDataMap.containsKey(listFunctionalFeaturesKey)){
            return resultDataMap.get(listFunctionalFeaturesKey);
        }else{
            return null;
        }
    }

    public interface ListProviderRunningStatusCallback {
        public void onExecutionSuccess(List<ProviderRunningInfo> providerRunningInfoList);
        public void onExecutionFail();
    }

    public void listProviderRunningStatus(ListProviderRunningStatusCallback listProviderRunningStatusCallback,int listTimeOutInSecond) {
        try {

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

            AnalysisProviderRunningStatusRequest analysisProviderRunningStatusRequest = new AnalysisProviderRunningStatusRequest();
            openSession();
            sendAnalyseRequest(analysisProviderRunningStatusRequest,analyseResponseCallback,listTimeOutInSecond);
        } catch (ProviderClientInitException e) {
            throw new RuntimeException(e);
        } catch (AnalysisEngineRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProviderRunningInfo> listProviderRunningStatus(){
        Map<String,List<ProviderRunningInfo>> resultDataMap = new HashMap<>();
        final String listProviderRunningInfoKey = "ProviderRunningStatusList";

        ExecutorService executor = Executors.newFixedThreadPool(1);
        java.util.concurrent.Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                AnalysisProviderAdminClient.ListProviderRunningStatusCallback listProviderRunningStatusCallback = new AnalysisProviderAdminClient.ListProviderRunningStatusCallback() {
                    @Override
                    public void onExecutionSuccess(List<ProviderRunningInfo> providerRunningInfoList) {
                        if(providerRunningInfoList != null){
                            resultDataMap.put(listProviderRunningInfoKey,providerRunningInfoList);

                        }
                    }

                    @Override
                    public void onExecutionFail() {}
                };
                listProviderRunningStatus(listProviderRunningStatusCallback,timeoutInSecond);
                //need this sleep to make sure the callback is executed
                Thread.sleep(timeoutInSecond*1000);
                return "";
            }
        });

        try {
            // 同步等待异步操作结果
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭ExecutorService
            executor.shutdown();
        }

        if(resultDataMap.containsKey(listProviderRunningInfoKey)){
            return resultDataMap.get(listProviderRunningInfoKey);
        }else{
            return null;
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
                public void onResponseReceived(Object analyseResponseObject) {}

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

    public List<FeatureRunningInfo> listFeatureRunningStatus(){
        Map<String,List<FeatureRunningInfo>> resultDataMap = new HashMap<>();
        final String listFeatureRunningStatusKey = "FeatureRunningStatusList";

        ExecutorService executor = Executors.newFixedThreadPool(1);
        java.util.concurrent.Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                AnalysisProviderAdminClient.ListFeatureRunningStatusCallback listFeatureRunningStatusCallback = new AnalysisProviderAdminClient.ListFeatureRunningStatusCallback() {
                    @Override
                    public void onExecutionSuccess(List<FeatureRunningInfo> featureRunningInfo) {
                        if(featureRunningInfo != null){
                            resultDataMap.put(listFeatureRunningStatusKey,featureRunningInfo);
                        }
                    }

                    @Override
                    public void onExecutionFail() {}
                };
                listFeatureRunningStatus(listFeatureRunningStatusCallback,timeoutInSecond);
                //need this sleep to make sure the callback is executed
                Thread.sleep(timeoutInSecond*1000);
                return "";
            }
        });

        try {
            // 同步等待异步操作结果
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭ExecutorService
            executor.shutdown();
        }

        if(resultDataMap.containsKey(listFeatureRunningStatusKey)){
            return resultDataMap.get(listFeatureRunningStatusKey);
        }else{
            return null;
        }
    }

    public boolean registerFunctionalFeature(String functionalFeatureName,String functionalFeatureDesc,int registerTimeOutInSecond){
        Map<String,Boolean> resultDataMap = new HashMap<>();
        final String registerFunctionalFeatureKey = "RegisterFunctionalFeature";

        ExecutorService executor = Executors.newFixedThreadPool(1);
        java.util.concurrent.Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {

                try {
                    openSession();
                    AnalysisProviderRegisterFunctionalFeatureRequest analysisProviderRegisterFunctionalFeatureRequest = new AnalysisProviderRegisterFunctionalFeatureRequest();
                    analysisProviderRegisterFunctionalFeatureRequest.setFunctionalFeatureName(functionalFeatureName);
                    analysisProviderRegisterFunctionalFeatureRequest.setFunctionalFeatureDescription(functionalFeatureDesc);
                    AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                        @Override
                        public void onResponseReceived(Object analyseResponseObject) {}

                        @Override
                        public void onSuccessResponseReceived(AnalyseResponse analyseResponse) {
                            try {
                                if(analyseResponse.getResponseData() != null){
                                    boolean registerResult = (boolean)analyseResponse.getResponseData();
                                    resultDataMap.put(registerFunctionalFeatureKey,registerResult);
                                }
                                closeSession();
                            } catch (ProviderClientInitException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailureResponseReceived(Throwable throwable) {
                            try {
                                closeSession();
                            } catch (ProviderClientInitException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    sendAnalyseRequest(analysisProviderRegisterFunctionalFeatureRequest,analyseResponseCallback,registerTimeOutInSecond);
                } catch (ProviderClientInitException e) {
                    throw new RuntimeException(e);
                } catch (AnalysisEngineRuntimeException e) {
                    throw new RuntimeException(e);
                }

                //need this sleep to make sure the callback is executed
                Thread.sleep(timeoutInSecond*1000);
                return "";
            }
        });

        try {
            // 同步等待异步操作结果
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭ExecutorService
            executor.shutdown();
        }

        if(resultDataMap.containsKey(registerFunctionalFeatureKey)){
            return resultDataMap.get(registerFunctionalFeatureKey);
        }else{
            return false;
        }
    }
}
