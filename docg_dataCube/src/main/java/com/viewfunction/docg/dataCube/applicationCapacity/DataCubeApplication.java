package com.viewfunction.docg.dataCube.applicationCapacity;

import com.viewfunction.docg.dataCube.consoleApplication.feature.BaseApplication;

public class DataCubeApplication implements BaseApplication {

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
