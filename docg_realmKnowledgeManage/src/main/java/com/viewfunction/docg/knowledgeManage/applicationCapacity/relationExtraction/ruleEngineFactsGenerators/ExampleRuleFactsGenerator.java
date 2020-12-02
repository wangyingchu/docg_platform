package com.viewfunction.docg.knowledgeManage.applicationCapacity.relationExtraction.ruleEngineFactsGenerators;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.knowledgeManage.applicationService.common.BaseRealmEntity;
import com.viewfunction.docg.knowledgeManage.applicationService.ruleEngine.RuleFactsGenerator;
import org.kie.api.runtime.KieSession;

import java.util.Map;

public class ExampleRuleFactsGenerator implements RuleFactsGenerator {

    @Override
    public void generateRuleFacts(KieSession kSession, CoreRealm coreRealm, Map<Object, Object> commandContextDataMap, String extractionId, String linkerId) {
        BaseRealmEntity _BaseRealmEntity = new BaseRealmEntity("uid001",coreRealm);
        _BaseRealmEntity.set("exampleProp1","MATCHED");
        _BaseRealmEntity.set("exampleProp2","NOTMATCHED");
        kSession.insert(_BaseRealmEntity);

        BaseRealmEntity _BaseRealmEntity2 = new BaseRealmEntity("uid002",coreRealm);
        _BaseRealmEntity2.set("exampleProp1","MATCHED2");
        _BaseRealmEntity2.set("exampleProp2","NOTMATCHED");
    }
}
