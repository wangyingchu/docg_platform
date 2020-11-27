package com.viewfunction.docg.knowledgeManage.applicationService.ruleEngine;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Map;

public class RuleEngineService {

    public static class Message {
        public static final int HELLO   = 0;
        public static final int GOODBYE = 1;
        public String          message;
        public int             status;
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public int getStatus() {
            return status;
        }
        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static boolean validateKieBaseExistence(String kieBaseName){
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        return kc.getKieBaseNames().contains(kieBaseName);
    }


    public static void executeRuleLogic(CoreRealm coreRealm, Map<Object,Object> commandContextDataMap,String extractionId, String linkerId,RuleFactsGenerator ruleFactsGenerator){
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        //KieSession kSession = kc.newKieSession("docgRelationExtractionKS");
        KieSession kSession = kc.getKieBase(extractionId).newKieSession();
        /*
        // Insert facts into the KIE session.
        final Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        ksession.insert( message );
        */
        ruleFactsGenerator.generateRuleFacts(kSession,coreRealm,commandContextDataMap,extractionId,linkerId);
        // Fire the rules.
        kSession.fireAllRules();
        // Fire the rules.
        kSession.fireAllRules();
        kSession.dispose();
    }
}
