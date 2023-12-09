package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor.CommandProcessorFactory;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.config.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseApplication;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class DataComputeApplication implements BaseApplication {

    private Ignite nodeIgnite=null;

    private CoreRealm coreRealm = null;
    private ExecutorService executor = null;
    private Map<Object,Object> commandContextDataMap;
    private Map<String,BaseCommandProcessor> commandProcessorMap;

    @Override
    public boolean initApplication() {
        String isClientNodeCfg= DataComputeConfigurationHandler.getConfigPropertyValue("isClientUnit");
        boolean isClientNode=Boolean.parseBoolean(isClientNodeCfg);
        if(isClientNode){
            Ignition.setClientMode(true);
        }
        nodeIgnite= Ignition.start(DataComputeConfigurationHandler.getDataComputeIgniteConfigurationFilePath());
        commandProcessorMap = new HashMap<>();
        commandContextDataMap = new HashMap<>();
        return true;
    }

    @Override
    public boolean shutdownApplication() {
        nodeIgnite.close();
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
                    BaseCommandProcessor commandProcessor = CommandProcessorFactory.getCommandProcessor(commandProcessorMap,command,options,
                            this.coreRealm,this.executor,this.commandContextDataMap,this.nodeIgnite);
                    if(commandProcessor!=null){
                        commandProcessor.processCommand(command,options);
                    }
                }
            }
        }
    }
}
