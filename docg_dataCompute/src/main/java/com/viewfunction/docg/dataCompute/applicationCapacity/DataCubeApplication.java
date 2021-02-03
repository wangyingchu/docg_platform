package com.viewfunction.docg.dataCompute.applicationCapacity;

import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseApplication;
import org.apache.ignite.Ignite;

public class DataCubeApplication implements BaseApplication {

    private Ignite nodeIgnite=null;

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
