package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;

public class DactgridCommandProcessor implements BaseCommandProcessor {
    private Ignite nodeIgnite;

    public DactgridCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        StringBuffer appInfoStringBuffer=new StringBuffer();
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Start deactive global grid......");
        appInfoStringBuffer.append("\n\r");
        this.nodeIgnite.active(false);
        appInfoStringBuffer.append("Deactive global grid finish.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        System.out.println(appInfoStringBuffer.toString());
    }
}
