package com.viewfunction.docg.dataFabric.applicationCapacity.dataChangeDetect.businessLogicBlock;

import com.viewfunction.docg.dataFabric.applicationCapacity.dataChangeDetect.businessRuleItem.KieRulesRunner;
import com.yomahub.liteflow.core.NodeComponent;

public class BCmp extends NodeComponent {

    @Override
    public void process() {
        //do your business
        System.out.println("BCmp");



        //RuleTest ruleTest = new RuleTest();
      // ruleTest.test();

        KieRulesRunner kieRulesRunner = new KieRulesRunner();
        kieRulesRunner.test();


    }
}
