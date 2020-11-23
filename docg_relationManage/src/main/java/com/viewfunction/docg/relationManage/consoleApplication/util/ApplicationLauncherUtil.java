package com.viewfunction.docg.relationManage.consoleApplication.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ApplicationLauncherUtil {

    private static Properties _applicationInfoProperties;

    public static String getApplicationInfoPropertyValue(String resourceFileName){
        if(_applicationInfoProperties==null){
            _applicationInfoProperties=new Properties();
            try {
                _applicationInfoProperties.load(new FileInputStream("ApplicationLaunchCfg.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _applicationInfoProperties.getProperty(resourceFileName);
    }

    public static String getApplicationExitCommand(){
        return getApplicationInfoPropertyValue("applicationExitCommand");
    }

    public static String getDefaultApplicationFeatureName(){
        return getApplicationInfoPropertyValue("defaultApplicationFeatureName");
    }

    public static void printApplicationConsoleBanner(){
        StringBuffer bannerStringBuffer=new StringBuffer();
        String echo0="==================================================================";
        bannerStringBuffer.append(echo0);
        bannerStringBuffer.append("\n\r");
        bannerStringBuffer.append(getApplicationInfoPropertyValue("applicationName")+" | "
                +"ver. " + getApplicationInfoPropertyValue("applicationVersion"));

        bannerStringBuffer.append("\n\r");
        String echo6="-----------------------------------------------------------------";
        bannerStringBuffer.append(echo6);
        bannerStringBuffer.append("\n\r");

        Properties prop = System.getProperties();
        String osName = prop.getProperty("os.name");
        String osVersion = prop.getProperty("os.version");
        String osArch = prop.getProperty("os.arch");
        String osInfo=osName+" "+osVersion+" "+osArch;
        bannerStringBuffer.append("OS: "+osInfo);
        bannerStringBuffer.append("\n\r");

        bannerStringBuffer.append("User: "+prop.getProperty("user.name"));
        bannerStringBuffer.append("\n\r");

        String jvmVendor=prop.getProperty("java.vm.vendor");
        String jvmName=prop.getProperty("java.vm.name");
        String jvmVersion=prop.getProperty("java.vm.version");
        bannerStringBuffer.append("JVM: ver. "+prop.getProperty("java.version")+ " "+jvmVendor);
        bannerStringBuffer.append("\n\r");
        bannerStringBuffer.append("     "+jvmName +" "+jvmVersion);
        bannerStringBuffer.append("\n\r");

        bannerStringBuffer.append("Started at: "+new Date()+"");
        bannerStringBuffer.append("\n\r");
        String echo7="==================================================================";
        bannerStringBuffer.append(echo7);
        System.out.println(bannerStringBuffer.toString());
    }

    public static void printApplicationFeatureMessage(String featureProcessorName,String message){
        System.out.println("["+featureProcessorName+"] > "+message);
    }

    public static List<String> getCMD(){
        if(_applicationInfoProperties==null){
            _applicationInfoProperties=new Properties();
            try {
                _applicationInfoProperties.load(new FileInputStream("ApplicationLaunchCfg.properties"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<String> cmds = new ArrayList<>();
        Set<String> propertyNames =  _applicationInfoProperties.stringPropertyNames();

        for(String propertyName:propertyNames){
            if(propertyName.contains("CMD[") && propertyName.contains("]")){
                cmds.add(_applicationInfoProperties.getProperty(propertyName));
            }
        }
        return cmds;
    }
}
