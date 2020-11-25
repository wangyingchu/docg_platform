package com.viewfunction.docg.knowledgeManage.applicationService.ruleEngine;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.Map;

public class RuleEngineService {

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

    }
}
