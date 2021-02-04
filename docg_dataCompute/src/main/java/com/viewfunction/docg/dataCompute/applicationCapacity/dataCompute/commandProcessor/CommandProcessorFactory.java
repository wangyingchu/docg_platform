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
                clearAppConsoleCommandProcessor=new ClearAppConsoleCommandProcessor();
                commandProcessorMap.put("clear",clearAppConsoleCommandProcessor);
            }
            clearAppConsoleCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("appinfo")){
            BaseCommandProcessor showAppInfoCommandProcessor=commandProcessorMap.get("appinfo");
            if(showAppInfoCommandProcessor==null){
                showAppInfoCommandProcessor=new ShowAppInfoCommandProcessor(nodeIgnite);
                commandProcessorMap.put("appinfo",showAppInfoCommandProcessor);
            }
            showAppInfoCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("lsapps")){
            BaseCommandProcessor listApplicationsCommandProcessor=commandProcessorMap.get("lsapps");
            if(listApplicationsCommandProcessor==null){
                listApplicationsCommandProcessor=new ListApplicationsCommandProcessor(nodeIgnite);
                commandProcessorMap.put("lsapps",listApplicationsCommandProcessor);
            }
            listApplicationsCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("lsservices")){
            BaseCommandProcessor listServicesCommandProcessor=commandProcessorMap.get("lsservices");
            if(listServicesCommandProcessor==null){
                listServicesCommandProcessor=new ListServicesCommandProcessor(nodeIgnite);
                commandProcessorMap.put("lsservices",listServicesCommandProcessor);
            }
            listServicesCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("appdss")){
            BaseCommandProcessor showAppDataStoresInfoCommandProcessor=commandProcessorMap.get("appdss");
            if(showAppDataStoresInfoCommandProcessor==null){
                showAppDataStoresInfoCommandProcessor=new ShowAppDataStoresInfoCommandProcessor(nodeIgnite);
                commandProcessorMap.put("appdss",showAppDataStoresInfoCommandProcessor);
            }
            showAppDataStoresInfoCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("help")){
            BaseCommandProcessor showHelpInfoCommandProcessor=commandProcessorMap.get("help");
            if(showHelpInfoCommandProcessor==null){
                showHelpInfoCommandProcessor=new ShowHelpInfoCommandProcessor(nodeIgnite);
                commandProcessorMap.put("help",showHelpInfoCommandProcessor);
            }
            showHelpInfoCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("appmetrics")){
            BaseCommandProcessor showAppMetricsCommandProcessor=commandProcessorMap.get("appmetrics");
            if(showAppMetricsCommandProcessor==null){
                showAppMetricsCommandProcessor=new ShowAppMetricsCommandProcessor(nodeIgnite);
                commandProcessorMap.put("appmetrics",showAppMetricsCommandProcessor);
            }
            showAppMetricsCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("gridmetrics")){
            BaseCommandProcessor showGridMetricsCommandProcessor=commandProcessorMap.get("gridmetrics");
            if(showGridMetricsCommandProcessor==null){
                showGridMetricsCommandProcessor=new ShowGridMetricsCommandProcessor(nodeIgnite);
                commandProcessorMap.put("gridmetrics",showGridMetricsCommandProcessor);
            }
            showGridMetricsCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("activegrid")){
            BaseCommandProcessor activeGridCommandProcessor=commandProcessorMap.get("activegrid");
            if(activeGridCommandProcessor==null){
                activeGridCommandProcessor=new ActiveGridCommandProcessor(nodeIgnite);
                commandProcessorMap.put("activegrid",activeGridCommandProcessor);
            }
            activeGridCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("deactivegrid")){
            BaseCommandProcessor deactiveGridCommandProcessor=commandProcessorMap.get("deactivegrid");
            if(deactiveGridCommandProcessor==null){
                deactiveGridCommandProcessor=new DeactiveGridCommandProcessor(nodeIgnite);
                commandProcessorMap.put("deactivegrid",deactiveGridCommandProcessor);
            }
            deactiveGridCommandProcessor.processCommand(commandContent,commandOptions);
        }
        else{
            System.out.println("Command [ "+commandContent+" ] not supported");
        }
        return null;
    }
}
