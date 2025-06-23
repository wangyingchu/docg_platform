package com.viewfunction.docg.dataFabric.applicationCapacity.dataChangeDetect;

import com.viewfunction.docg.dataFabric.applicationService.util.ApplicationServiceConstant;
import com.viewfunction.docg.dataFabric.consoleApplication.feature.BaseApplication;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.core.FlowExecutorHolder;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.property.LiteflowConfig;
import com.yomahub.liteflow.slot.DefaultContext;

public class DataChangeDetectApplication implements BaseApplication {

    @Override
    public boolean isDaemonApplication() {
        return true;
    }

    @Override
    public boolean initApplication() {
        return true;
    }

    @Override
    public boolean shutdownApplication() {
        return true;
    }

    @Override
    public void executeConsoleCommand(String consoleCommand) {
        System.out.println("DataChangeDetectApplication executeConsoleCommand");
    }

    @Override
    public void executeDaemonLogic() {
        System.out.println("DataChangeDetectApplication executeDaemonLogic");
        LiteflowConfig config = new LiteflowConfig();
        config.setRuleSource(ApplicationServiceConstant.BUSINESS_FLOW_CONFIG_ROOT_PATH+"/dataChangeDetect/flow.xml");
        FlowExecutor flowExecutor = FlowExecutorHolder.loadInstance(config);
        LiteflowResponse response = flowExecutor.execute2Resp("chain1", "arg", DefaultContext.class);
        System.out.println("DataChangeDetectApplication executeDaemonLogic response code:"+response.getMessage());
    }
}
