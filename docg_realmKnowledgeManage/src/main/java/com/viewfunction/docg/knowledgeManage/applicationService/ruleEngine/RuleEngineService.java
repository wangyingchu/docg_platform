package com.viewfunction.docg.knowledgeManage.applicationService.ruleEngine;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;

import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
/*
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.drools.core.impl.KnowledgeBaseFactory;
*/

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




    public static void executeRuleLogic(CoreRealm coreRealm, Map<Object,Object> commandContextDataMap,String extractionId, String linkerId){
        System.out.println(extractionId);
        System.out.println(linkerId);

        //KieHelper helper = new KieHelper();
        //helper.addContent(ruleModel.getRule(), ResourceType.DRL);
        //KieContainer kc = helper.getKieContainer();//
        //KieContainer kc = KieServices.Factory.get().newKieClasspathContainer();
        //KieSession ksession = kc.newKieSession("docgRelationExtractionKS");
        //ksession.fireAllRules();
        //ksession.dispose();

        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("docgRelationExtractionKS");

       // ksession.addEventListener( new DebugAgendaEventListener() );
       // ksession.addEventListener( new DebugRuleRuntimeEventListener() );

        // Insert facts into the KIE session.
        final Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        ksession.insert( message );

        // Fire the rules.
        ksession.fireAllRules();


        // Fire the rules.
        ksession.fireAllRules();
        ksession.dispose();
    }
}
