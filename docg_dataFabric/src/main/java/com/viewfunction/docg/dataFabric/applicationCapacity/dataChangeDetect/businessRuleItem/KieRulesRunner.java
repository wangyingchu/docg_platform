package com.viewfunction.docg.dataFabric.applicationCapacity.dataChangeDetect.businessRuleItem;

import com.viewfunction.docg.dataFabric.applicationService.ruleEngine.RulesEngine;
import com.viewfunction.docg.dataFabric.applicationService.ruleEngine.RulesEngineService;
import com.viewfunction.docg.dataFabric.consoleApplication.exception.RuleEngineInitException;
import com.viewfunction.docg.dataFabric.consoleApplication.exception.RuleEngineRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class KieRulesRunner {

    private static final Logger LOG = LoggerFactory.getLogger(KieRulesRunner.class);

    public void test() {

        try {
            RulesEngine rulesEngine = RulesEngineService.getRulesEngine("dataChangeDetect");

            Set<String> check = new HashSet<String>();
            rulesEngine.setGlobalAttribute("controlSet", check);

            Measurement mRed = new Measurement("color", "red");
            rulesEngine.insertRuleFact(mRed);
            rulesEngine.executeRules();

            Measurement mGreen = new Measurement("color", "green");
            rulesEngine.insertRuleFact(mGreen);
            rulesEngine.executeRules();

            Measurement mBlue = new Measurement("color", "blue");
            rulesEngine.insertRuleFact(mBlue);
            rulesEngine.executeRules();

            Measurement mNull = new Measurement("color", null);
            rulesEngine.insertRuleFact(mNull);
            rulesEngine.executeRules();

            System.out.println("======================");
            System.out.println("======================");
            System.out.println(rulesEngine.getRuleFacts().size());
            System.out.println(check.contains("red"));
            System.out.println(check.contains("green"));
            System.out.println(check.contains("blue"));
            System.out.println(check.size());
            System.out.println(check);
            System.out.println("======================");
            System.out.println("======================");

            rulesEngine.stopRulesEngine();

        } catch (RuleEngineInitException | RuleEngineRuntimeException e) {
            throw new RuntimeException(e);
        }

/*
        if(RuleEngineService.validateKieBaseExistence("dataChangeDetect")){
            KieServices ks = KieServices.Factory.get();
            KieContainer kc = ks.getKieClasspathContainer();
            KieSession session = kc.getKieBase("dataChangeDetect").newKieSession();
            try{

                LOG.info("Populating globals");
                Set<String> check = new HashSet<String>();
                session.setGlobal("controlSet", check);

                LOG.info("Now running data");

                Measurement mRed = new Measurement("color", "red");
                session.insert(mRed);
                session.fireAllRules();

                Measurement mGreen = new Measurement("color", "green");
                session.insert(mGreen);
                session.fireAllRules();

                Measurement mBlue = new Measurement("color", "blue");
                session.insert(mBlue);
                session.fireAllRules();

                Measurement mNull = new Measurement("color", null);
                session.insert(mNull);
                session.fireAllRules();

                LOG.info("Final checks");

                System.out.println("======================");
                System.out.println("======================");
                System.out.println(session.getObjects().size());
                System.out.println(check.contains("red"));
                System.out.println(check.contains("green"));
                System.out.println(check.contains("blue"));
                System.out.println(check.size());
                System.out.println(check);
                System.out.println("======================");
                System.out.println("======================");
            }finally {
                if(session != null){
                    session.dispose();
                }
            }
        }
*/



    }
}
