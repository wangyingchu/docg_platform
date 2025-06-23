package com.viewfunction.docg.dataFabric.applicationService.ruleEngine;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.dataFabric.consoleApplication.exception.RuleEngineInitException;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Map;

public class RulesEngineService {

    public static RulesEngine getRulesEngine(String rulesId) throws RuleEngineInitException {
        if(validateKieBaseExistence(rulesId)){
            return new RulesEngine(rulesId);
        }else{
            throw new RuleEngineInitException();
        }
    }


    private static boolean validateKieBaseExistence(String kieBaseName){
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        boolean validateResult = kc.getKieBaseNames().contains(kieBaseName);
        kc.dispose();
        return validateResult;
    }

    public static void executeRuleLogic(CoreRealm coreRealm, Map<Object,Object> commandContextDataMap,String ruleId, String linkerId,RuleFactsGenerator ruleFactsGenerator){
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession kSession = kc.getKieBase().newKieSession();
        try{
            ruleFactsGenerator.generateRuleFacts(kSession,coreRealm,commandContextDataMap,ruleId,linkerId);
            // Fire the rules.
            kSession.fireAllRules();
        }finally {
            if(kSession != null){
                kSession.dispose();
            }
        }
    }
}
