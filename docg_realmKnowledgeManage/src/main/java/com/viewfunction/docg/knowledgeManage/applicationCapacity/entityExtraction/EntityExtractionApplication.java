package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction;

import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseApplication;

public class EntityExtractionApplication implements BaseApplication {
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

    }
}
