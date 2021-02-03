package com.viewfunction.docg.dataCube.consoleApplication.feature;

public interface BaseApplication {

    public boolean initApplication();

    public boolean shutdownApplication();

    public void executeConsoleCommand(String consoleCommand);
}
