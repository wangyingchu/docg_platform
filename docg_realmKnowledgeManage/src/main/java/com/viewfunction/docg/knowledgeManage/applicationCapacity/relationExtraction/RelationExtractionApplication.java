package com.viewfunction.docg.knowledgeManage.applicationCapacity.relationExtraction;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.relationExtraction.commandProcessor.RelationExtractionCommandProcessorFactory;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseApplication;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;
import com.viewfunction.docg.knowledgeManage.consoleApplication.util.ApplicationLauncherUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RelationExtractionApplication implements BaseApplication {

    private CoreRealm coreRealm = null;
    private ExecutorService executor = null;
    private Map<Object,Object> commandContextDataMap;

    @Override
    public boolean isDaemonApplication() {
        return false;
    }

    @Override
    public void executeDaemonLogic() {

    }

    @Override
    public boolean initApplication() {
        this.coreRealm = RealmTermFactory.getDefaultCoreRealm();
        this.coreRealm.openGlobalSession();
        String maxThreadNum = ApplicationLauncherUtil.getApplicationInfoPropertyValue("RelationExtraction.Executors.MaxThreadNum");
        int maxThreadNumber = 5;
        if(maxThreadNum != null){
            maxThreadNumber = Integer.parseInt(maxThreadNum.trim());
        }
        this.executor = Executors.newFixedThreadPool(maxThreadNumber);
        this.commandContextDataMap = new ConcurrentHashMap<>();
        return true;
    }

    @Override
    public boolean shutdownApplication() {
        if(this.coreRealm != null){
            this.coreRealm.closeGlobalSession();
        }
        if(this.executor != null){
            this.executor.shutdown();
        }
        if(this.commandContextDataMap != null){
            this.commandContextDataMap.clear();
        }
        return true;
    }

    @Override
    public void executeConsoleCommand(String consoleCommand) {
        if(consoleCommand != null){
            String[] commandOptions = consoleCommand.split(" ");
            if(commandOptions.length>0){
                String command = commandOptions[0];
                if(command.startsWith("-")||command.startsWith("--")){
                    System.out.println("Please input valid command and options");
                }else{
                    String[] options = Arrays.copyOfRange(commandOptions,1,commandOptions.length);
                    BaseCommandProcessor commandProcessor = RelationExtractionCommandProcessorFactory.getCommandProcessor(command,this.coreRealm,this.executor,this.commandContextDataMap);
                    if(commandProcessor!=null){
                        commandProcessor.processCommand(command,options);
                    }
                }
            }
        }
    }
}
