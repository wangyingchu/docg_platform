package com.viewfunction.docg.coreRealm.realmServiceCore.util.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LogbackConfigurationHandler {

    public static void setLogbackConfigFileLocation(String locationPath){
        File logbackFile = new File(locationPath);
        if (logbackFile.exists()) {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            try {
                configurator.doConfigure(logbackFile);
            }
            catch (JoranException e) {
                e.printStackTrace(System.err);
                System.exit(-1);
            }
        }
    }
}
