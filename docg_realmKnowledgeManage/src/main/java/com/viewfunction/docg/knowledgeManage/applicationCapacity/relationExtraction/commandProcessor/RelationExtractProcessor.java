package com.viewfunction.docg.knowledgeManage.applicationCapacity.relationExtraction.commandProcessor;

import com.beust.jcommander.JCommander;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class RelationExtractProcessor implements BaseCommandProcessor {

    private CoreRealm coreRealm;
    private ExecutorService executor;
    private Map<Object,Object> commandContextDataMap;

    public RelationExtractProcessor(CoreRealm coreRealm, ExecutorService executor, Map<Object,Object> commandContextDataMap){
        this.coreRealm = coreRealm;
        this.executor = executor;
        this.commandContextDataMap = commandContextDataMap;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        RelationExtractionCommandOptions relationExtractionCommandOptions = new RelationExtractionCommandOptions();
        JCommander.newBuilder().addObject(relationExtractionCommandOptions).build().parse(commandOptions);
        //System.out.println(relationExtractionCommandOptions.getExtractionId());
        //System.out.println(relationExtractionCommandOptions.getLinkerId());
        //System.out.println(relationExtractionCommandOptions.isMultimode());
    }
}
