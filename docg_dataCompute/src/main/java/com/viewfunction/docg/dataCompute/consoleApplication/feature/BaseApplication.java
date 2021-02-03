package com.viewfunction.docg.dataCompute.consoleApplication.feature;

public interface BaseApplication {

    public boolean initApplication();

    public boolean shutdownApplication();

    public void executeConsoleCommand(String consoleCommand);
}
