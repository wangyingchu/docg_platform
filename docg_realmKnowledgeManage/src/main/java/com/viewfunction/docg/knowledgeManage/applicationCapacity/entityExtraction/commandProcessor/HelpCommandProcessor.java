package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.commandProcessor;

import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;

public class HelpCommandProcessor implements BaseCommandProcessor {
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
        appInfoStringBuffer.append("appinf:       " +"Show basic information of current running entity extraction application.");
        /*
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("unitmetr:     " +"Show real time metrics of current running data compute unit.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("unitds:       " +"Show all data slices contains in current data compute unit.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("lsunit:       " +"List all data compute units serving in global data compute grid.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("lssvc:        " +"List all compute services running in global data compute grid.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("gridmetr:     " +"Show whole data compute grid real time metrics.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("actvgrid:     " +"Active global data compute grid after all data compute cubes startup.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("dactgrid:     " +"Deactivate global data compute grid.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("clear:        " +"Clear console message.");
        */
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("help:         " +"Show available commands.");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        System.out.println(appInfoStringBuffer.toString());
    }
}
