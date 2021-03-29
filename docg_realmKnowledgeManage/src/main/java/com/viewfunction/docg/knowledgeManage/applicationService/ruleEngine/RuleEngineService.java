package com.viewfunction.docg.knowledgeManage.applicationService.ruleEngine;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

public class RuleEngineService {

    private static Properties _RuleFactsGeneratorsProperties;

    public static String getRuleFactsGeneratorClassName(String extractionId){
        if(_RuleFactsGeneratorsProperties == null){
            _RuleFactsGeneratorsProperties = new Properties();
            try {
                _RuleFactsGeneratorsProperties.load(new FileInputStream("RuleEngineServiceCfg.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _RuleFactsGeneratorsProperties.getProperty(extractionId);
    }

    public static boolean validateKieBaseExistence(String kieBaseName){
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        return kc.getKieBaseNames().contains(kieBaseName);
    }

    public static boolean validateRuleFactsGeneratorExistence(String extractionId){
        String targetRuleFactsGeneratorClassName = getRuleFactsGeneratorClassName(extractionId);
        return targetRuleFactsGeneratorClassName != null ? true : false;
    }

    public static RuleFactsGenerator getRuleFactsGenerator(String extractionId){
        String targetRuleFactsGeneratorClassName = getRuleFactsGeneratorClassName(extractionId);
        if(targetRuleFactsGeneratorClassName != null){
            Class<?> ruleFactsGeneratorClass = null;
            try {
                ruleFactsGeneratorClass = Class.forName(targetRuleFactsGeneratorClassName);
                Object ruleFactsGeneratorObject = ruleFactsGeneratorClass.getDeclaredConstructor().newInstance();
                return (RuleFactsGenerator)ruleFactsGeneratorObject;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void executeRuleLogic(CoreRealm coreRealm, Map<Object,Object> commandContextDataMap,String extractionId, String linkerId,RuleFactsGenerator ruleFactsGenerator){
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession kSession = kc.getKieBase(extractionId).newKieSession();
        try{
            ruleFactsGenerator.generateRuleFacts(kSession,coreRealm,commandContextDataMap,extractionId,linkerId);
            // Fire the rules.
            kSession.fireAllRules();
            // Fire the rules.
            kSession.fireAllRules();
        }finally {
            if(kSession != null){
                kSession.dispose();
            }
        }
    }
}
