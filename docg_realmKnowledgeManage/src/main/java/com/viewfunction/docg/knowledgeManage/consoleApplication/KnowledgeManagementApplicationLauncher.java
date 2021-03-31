package com.viewfunction.docg.knowledgeManage.consoleApplication;

import com.viewfunction.docg.knowledgeManage.consoleApplication.exception.ApplicationInitException;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseApplication;

import com.viewfunction.docg.knowledgeManage.consoleApplication.util.ApplicationLauncherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class KnowledgeManagementApplicationLauncher {

    private static BaseApplication consoleApplication;
    private static boolean applicationRunningFlag = true;
    private static boolean applicationAbnormalCloseFlag = true;
    private static Logger logger = LoggerFactory.getLogger(KnowledgeManagementApplicationLauncher.class);

    private static void stopApplication() {
        applicationAbnormalCloseFlag = false;
        boolean shutdownApplicationResult = consoleApplication.shutdownApplication();
        if (!shutdownApplicationResult) {
            logger.error("Clean Application Resource Error");
        }
        applicationRunningFlag = false;
    }

    private static class CleanApplicationResourceThread extends Thread {
        @Override
        public void run() {
            if (applicationAbnormalCloseFlag) {
                boolean showdownApplicationResult = consoleApplication.shutdownApplication();
                if (!showdownApplicationResult) {
                    logger.error("Clean Application Resource Error");
                }
            }
        }
    }

    private static void handleConsoleInputCommands() {
        Scanner sc = new Scanner(System.in);
        System.out.print(">_");
        String command = sc.nextLine();
        System.out.println("Processing [ " + command + " ]......");
        if (command.equals(ApplicationLauncherUtil.getApplicationExitCommand())) {
            stopApplication();
        } else {
            consoleApplication.executeConsoleCommand(command);
        }
    }

    public static void main(String[] args) throws ApplicationInitException {
        try {
            String applicationFeatureName = args.length > 0 ? args[0] : ApplicationLauncherUtil.getDefaultApplicationFeatureName();
            if (args.length == 0) {
                if (applicationFeatureName == null) {
                    ApplicationInitException applicationInitException = new ApplicationInitException();
                    applicationInitException.initCause(new RuntimeException("Application Feature Name Is Not Set!"));
                    throw applicationInitException;
                }
            }
            consoleApplication = ApplicationCapacityRegistry.createConsoleApplication(applicationFeatureName);
            if (consoleApplication == null) {
                ApplicationInitException applicationInitException = new ApplicationInitException();
                applicationInitException.initCause(new RuntimeException("Application Feature " + applicationFeatureName + " Not Found!"));
                throw applicationInitException;
            }
            Runtime.getRuntime().addShutdownHook(new CleanApplicationResourceThread());
            boolean initApplicationResult = consoleApplication.initApplication();
            if (initApplicationResult) {
                ApplicationLauncherUtil.printApplicationConsoleBanner();
                if(consoleApplication.isDaemonApplication()){
                    consoleApplication.executeDaemonLogic();
                }
                while (applicationRunningFlag) {
                    handleConsoleInputCommands();
                }
            } else {
                throw new ApplicationInitException();
            }
        } catch (Exception e) {
            ApplicationInitException applicationInitException = new ApplicationInitException();
            applicationInitException.initCause(e);
            throw applicationInitException;
        }
    }
}
