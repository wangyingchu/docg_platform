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
            BaseCommandProcessor clearCommandProcessor=commandProcessorMap.get("clear");
            if(clearCommandProcessor==null){
                clearCommandProcessor=new ClearCommandProcessor();
                commandProcessorMap.put("clear",clearCommandProcessor);
            }
            clearCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("unitinf")){
            BaseCommandProcessor unitinfCommandProcessor=commandProcessorMap.get("unitinf");
            if(unitinfCommandProcessor==null){
                unitinfCommandProcessor=new UnitInfCommandProcessor(nodeIgnite);
                commandProcessorMap.put("unitinf",unitinfCommandProcessor);
            }
            unitinfCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("lsunit")){
            BaseCommandProcessor lsunitCommandProcessor=commandProcessorMap.get("lsunit");
            if(lsunitCommandProcessor==null){
                lsunitCommandProcessor=new LsunitCommandProcessor(nodeIgnite);
                commandProcessorMap.put("lsunit",lsunitCommandProcessor);
            }
            lsunitCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("lssvc")){
            BaseCommandProcessor lssvcCommandProcessor=commandProcessorMap.get("lssvc");
            if(lssvcCommandProcessor==null){
                lssvcCommandProcessor=new LssvcCommandProcessor(nodeIgnite);
                commandProcessorMap.put("lssvc",lssvcCommandProcessor);
            }
            lssvcCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("unitds")){
            BaseCommandProcessor unitdsCommandProcessor=commandProcessorMap.get("unitds");
            if(unitdsCommandProcessor==null){
                unitdsCommandProcessor=new UnitdsCommandProcessor(nodeIgnite);
                commandProcessorMap.put("unitds",unitdsCommandProcessor);
            }
            unitdsCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("help")){
            BaseCommandProcessor helpCommandProcessor=commandProcessorMap.get("help");
            if(helpCommandProcessor==null){
                helpCommandProcessor=new HelpCommandProcessor(nodeIgnite);
                commandProcessorMap.put("help",helpCommandProcessor);
            }
            helpCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("unitmetr")){
            BaseCommandProcessor unitmetrCommandProcessor=commandProcessorMap.get("unitmetr");
            if(unitmetrCommandProcessor==null){
                unitmetrCommandProcessor=new UnitmetrCommandProcessor(nodeIgnite);
                commandProcessorMap.put("unitmetr",unitmetrCommandProcessor);
            }
            unitmetrCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("gridmetr")){
            BaseCommandProcessor gridmetrCommandProcessor=commandProcessorMap.get("gridmetr");
            if(gridmetrCommandProcessor==null){
                gridmetrCommandProcessor=new GridmetrCommandProcessor(nodeIgnite);
                commandProcessorMap.put("gridmetr",gridmetrCommandProcessor);
            }
            gridmetrCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("actvgrid")){
            BaseCommandProcessor actvgridCommandProcessor=commandProcessorMap.get("actvgrid");
            if(actvgridCommandProcessor==null){
                actvgridCommandProcessor=new ActvgridCommandProcessor(nodeIgnite);
                commandProcessorMap.put("actvgrid",actvgridCommandProcessor);
            }
            actvgridCommandProcessor.processCommand(commandContent,commandOptions);
        }else if(commandContent.equals("dactgrid")){
            BaseCommandProcessor dactgridCommandProcessor=commandProcessorMap.get("dactgrid");
            if(dactgridCommandProcessor==null){
                dactgridCommandProcessor=new DactgridCommandProcessor(nodeIgnite);
                commandProcessorMap.put("dactgrid",dactgridCommandProcessor);
            }
            dactgridCommandProcessor.processCommand(commandContent,commandOptions);
        }
        else{
            System.out.println("Command [ "+commandContent+" ] not supported");
        }
        return null;
    }
}
