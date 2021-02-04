package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class CommandProcessorFactory {

    public static BaseCommandProcessor getCommandProcessor(Map<String,BaseCommandProcessor> commandProcessorMap, String commandContent, String[] commandOptions,
                                                           CoreRealm coreRealm, ExecutorService executor, Map<Object,Object> commandContextDataMap, Ignite nodeIgnite){
        if(commandContent.equals("clear")){
            BaseCommandProcessor clearAppConsoleCommandProcessor=commandProcessorMap.get("clear");
            if(clearAppConsoleCommandProcessor==null){
                clearAppConsoleCommandProcessor=new ClearCommandProcessor();
                commandProcessorMap.put("clear",clearAppConsoleCommandProcessor);
            }
            clearAppConsoleCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("cubeinf")){
            BaseCommandProcessor showAppInfoCommandProcessor=commandProcessorMap.get("cubeinf");
            if(showAppInfoCommandProcessor==null){
                showAppInfoCommandProcessor=new CubeInfCommandProcessor(nodeIgnite);
                commandProcessorMap.put("cubeinf",showAppInfoCommandProcessor);
            }
            showAppInfoCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("lscube")){
            BaseCommandProcessor listApplicationsCommandProcessor=commandProcessorMap.get("lscube");
            if(listApplicationsCommandProcessor==null){
                listApplicationsCommandProcessor=new LscubeCommandProcessor(nodeIgnite);
                commandProcessorMap.put("lscube",listApplicationsCommandProcessor);
            }
            listApplicationsCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("lssvc")){
            BaseCommandProcessor listServicesCommandProcessor=commandProcessorMap.get("lssvc");
            if(listServicesCommandProcessor==null){
                listServicesCommandProcessor=new LssvcCommandProcessor(nodeIgnite);
                commandProcessorMap.put("lssvc",listServicesCommandProcessor);
            }
            listServicesCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("cubeds")){
            BaseCommandProcessor showAppDataStoresInfoCommandProcessor=commandProcessorMap.get("cubeds");
            if(showAppDataStoresInfoCommandProcessor==null){
                showAppDataStoresInfoCommandProcessor=new CubedsCommandProcessor(nodeIgnite);
                commandProcessorMap.put("cubeds",showAppDataStoresInfoCommandProcessor);
            }
            showAppDataStoresInfoCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("help")){
            BaseCommandProcessor showHelpInfoCommandProcessor=commandProcessorMap.get("help");
            if(showHelpInfoCommandProcessor==null){
                showHelpInfoCommandProcessor=new HelpCommandProcessor(nodeIgnite);
                commandProcessorMap.put("help",showHelpInfoCommandProcessor);
            }
            showHelpInfoCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("cubemetr")){
            BaseCommandProcessor showAppMetricsCommandProcessor=commandProcessorMap.get("cubemetr");
            if(showAppMetricsCommandProcessor==null){
                showAppMetricsCommandProcessor=new CubemetrCommandProcessor(nodeIgnite);
                commandProcessorMap.put("cubemetr",showAppMetricsCommandProcessor);
            }
            showAppMetricsCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("gridmetr")){
            BaseCommandProcessor showGridMetricsCommandProcessor=commandProcessorMap.get("gridmetr");
            if(showGridMetricsCommandProcessor==null){
                showGridMetricsCommandProcessor=new GridmetrCommandProcessor(nodeIgnite);
                commandProcessorMap.put("gridmetr",showGridMetricsCommandProcessor);
            }
            showGridMetricsCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("actvgrid")){
            BaseCommandProcessor activeGridCommandProcessor=commandProcessorMap.get("actvgrid");
            if(activeGridCommandProcessor==null){
                activeGridCommandProcessor=new ActvgridCommandProcessor(nodeIgnite);
                commandProcessorMap.put("actvgrid",activeGridCommandProcessor);
            }
            activeGridCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("dactgrid")){
            BaseCommandProcessor deactiveGridCommandProcessor=commandProcessorMap.get("dactgrid");
            if(deactiveGridCommandProcessor==null){
                deactiveGridCommandProcessor=new DactgridCommandProcessor(nodeIgnite);
                commandProcessorMap.put("dactgrid",deactiveGridCommandProcessor);
            }
            deactiveGridCommandProcessor.processCommand(commandContent,commandOptions);
        }
        else{
            System.out.println("Command [ "+commandContent+" ] not supported");
        }
        return null;
    }
}
