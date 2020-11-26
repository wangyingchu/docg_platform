package com.viewfunction.docg.knowledgeManage.applicationService.ruleEngine;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import org.kie.api.runtime.KieSession;

import java.util.Map;

public interface RuleFactsGenerator {
    public void generateRuleFacts(KieSession kSession, CoreRealm coreRealm, Map<Object,Object> commandContextDataMap, String extractionId, String linkerId);
}
