package com.viewfunction.docg.dataFabric.applicationService.ruleEngine;

import com.viewfunction.docg.dataFabric.consoleApplication.exception.RuleEngineRuntimeException;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Collection;

public class RulesEngine {

    private KieServices ks;
    private KieContainer kc;
    private KieSession kSession;

    public RulesEngine(String rulesId){
        ks = KieServices.Factory.get();
        kc = ks.getKieClasspathContainer();
        kSession = kc.getKieBase(rulesId).newKieSession();
    }

    public void stopRulesEngine(){
        if(kSession!=null){
            kSession.close();
            kSession.dispose();
            kSession = null;
        }
        if(kc != null){
            kc.dispose();
            kc = null;
        }
    }

    public void executeRules()throws RuleEngineRuntimeException{
        if(kSession != null){
            kSession.fireAllRules();
        }else{
            throw new RuleEngineRuntimeException();
        }
    }

    public void insertRuleFact(Object fact) throws RuleEngineRuntimeException {
        if(kSession != null) {
            kSession.insert(fact);
        }else{
            throw new RuleEngineRuntimeException();
        }
    }

    public void setGlobalAttribute(String attributeName,Object attributeValue) throws RuleEngineRuntimeException {
        if(kSession != null) {
            kSession.setGlobal(attributeName,attributeValue);
        }else{
            throw new RuleEngineRuntimeException();
        }
    }

    public Collection<? extends Object> getRuleFacts() throws RuleEngineRuntimeException {
        if(kSession != null) {
            return kSession.getObjects();
        }else{
            throw new RuleEngineRuntimeException();
        }
    }
}
