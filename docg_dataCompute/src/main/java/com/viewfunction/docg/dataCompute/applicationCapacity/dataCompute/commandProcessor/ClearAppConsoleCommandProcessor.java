package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import com.viewfunction.docg.dataCompute.consoleApplication.util.ApplicationLauncherUtil;

public class ClearAppConsoleCommandProcessor implements BaseCommandProcessor {
    @Override
    public void processCommand(String command, String[] commandOptions) {
        ApplicationLauncherUtil.printApplicationConsoleBanner();
    }
}
