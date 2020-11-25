package com.viewfunction.docg.knowledgeManage.applicationCapacity.relationExtraction.commandProcessor;

import com.beust.jcommander.JCommander;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.knowledgeManage.applicationService.ruleEngine.RuleEngineService;
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
        String extractionId = relationExtractionCommandOptions.getExtractionId();
        if(extractionId == null){
            System.out.println("Extraction ID is required, please use attribute -extra to set the value");
            return;
        }else if(!validateExtractionId(extractionId)){
            System.out.println("Extraction ID "+extractionId + " is invalid");
            return;
        }else{
            String linkerId = relationExtractionCommandOptions.getLinkerId();
            if(linkerId == null){
                linkerId = System.getProperties().getProperty("user.name");
            }
            processRelationExtractionLogic(extractionId,linkerId,relationExtractionCommandOptions.isMultimode());
        }
    }

    private boolean validateExtractionId(String extractionId){
        return true;
    }

    private void processRelationExtractionLogic(String extractionId,String linkerId,boolean useMultiMode){
        //execute relation extraction logic chain
        //chain step 1, execute rule engine logic
        RuleEngineService.executeRuleLogic(this.coreRealm,this.commandContextDataMap,extractionId,linkerId);
    }
}
