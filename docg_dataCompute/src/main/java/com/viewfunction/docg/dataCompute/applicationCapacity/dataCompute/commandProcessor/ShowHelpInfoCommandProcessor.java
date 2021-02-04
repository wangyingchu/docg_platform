package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;

public class ShowHelpInfoCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public ShowHelpInfoCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        StringBuffer appInfoStringBuffer=new StringBuffer();
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Available Commands : ");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("-------------------------------------------------------------");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("appinfo:      " +"Show current connectome application basic information.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("appmetrics:   " +"Show current connectome application real time metrics.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("appdss:       " +"Show all data stores contains in current connectome application.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("lsapps:       " +"List all connectome applications serving in global grid.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("lsservices:   " +"List all services running in global grid.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("gridmetrics:  " +"Show whole neuron grid real time metrics.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("activegrid:   " +"Active global grid after all connectome applications startup.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("deactivegrid: " +"Deactive global grid after all connectome applications startup.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("clear:        " +"Clear current connectome application console message.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("help:         " +"Show available commands list.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        System.out.println(appInfoStringBuffer.toString());
    }
}

