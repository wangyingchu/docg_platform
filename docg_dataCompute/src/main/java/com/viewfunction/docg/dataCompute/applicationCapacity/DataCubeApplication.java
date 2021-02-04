package com.viewfunction.docg.dataCompute.applicationCapacity;

import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseApplication;
import com.viewfunction.docg.dataCompute.consoleApplication.util.ApplicationLauncherUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class DataCubeApplication implements BaseApplication {

    private Ignite nodeIgnite=null;

    @Override
    public boolean initApplication() {
        String isClientNodeCfg= ApplicationLauncherUtil.getApplicationInfoPropertyValue("isClientNode");
        boolean isClientNode=Boolean.parseBoolean(isClientNodeCfg);
        if(isClientNode){
            Ignition.setClientMode(true);
        }
        nodeIgnite= Ignition.start();
        return true;
    }

    @Override
    public boolean shutdownApplication() {
        nodeIgnite.close();
        return true;
    }

    @Override
    public void executeConsoleCommand(String consoleCommand) {

    }
}
