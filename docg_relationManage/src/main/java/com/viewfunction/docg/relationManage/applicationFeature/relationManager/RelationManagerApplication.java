package com.viewfunction.docg.relationManage.applicationFeature.relationManager;

import com.viewfunction.docg.relationManage.consoleApplication.feature.BaseApplication;

public class RelationManagerApplication implements BaseApplication {
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
