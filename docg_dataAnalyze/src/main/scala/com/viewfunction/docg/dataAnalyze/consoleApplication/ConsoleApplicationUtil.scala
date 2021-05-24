package com.viewfunction.docg.dataAnalyze.consoleApplication

import java.io.{File, FileInputStream}
import java.util.{Date, Properties}

object ConsoleApplicationUtil {

  def printApplicationConsoleBanner(): Unit = {
    val bannerStringBuffer = new StringBuffer
    val echo0 = "====================================================================="
    bannerStringBuffer.append(echo0)
    bannerStringBuffer.append("\n\r")
    bannerStringBuffer.append(getApplicationInfoPropertyValue("applicationName") + " | " + "ver. " + getApplicationInfoPropertyValue("applicationVersion"))
    bannerStringBuffer.append("\n\r")
    val echo6 = "--------------------------------------------------------------------"
    bannerStringBuffer.append(echo6)
    bannerStringBuffer.append("\n\r")
    val prop = System.getProperties
    val osName = prop.getProperty("os.name")
    val osVersion = prop.getProperty("os.version")
    val osArch = prop.getProperty("os.arch")
    val osInfo = osName + " " + osVersion + " " + osArch
    bannerStringBuffer.append("OS: " + osInfo)
    bannerStringBuffer.append("\n\r")
    bannerStringBuffer.append("User: " + prop.getProperty("user.name"))
    bannerStringBuffer.append("\n\r")
    val jvmVendor = prop.getProperty("java.vm.vendor")
    val jvmName = prop.getProperty("java.vm.name")
    val jvmVersion = prop.getProperty("java.vm.version")
    bannerStringBuffer.append("JVM: ver. " + prop.getProperty("java.version") + " " + jvmVendor)
    bannerStringBuffer.append("\n\r")
    bannerStringBuffer.append("     " + jvmName + " " + jvmVersion)
    bannerStringBuffer.append("\n\r")
    bannerStringBuffer.append("Started at: " + new Date + "")
    bannerStringBuffer.append("\n\r")
    val echo7 = "====================================================================="
    bannerStringBuffer.append(echo7)
    println(bannerStringBuffer.toString)
  }

  def getApplicationInfoPropertyValue(propertyNameName: String): String = {
    val properties = new Properties()
    var propertiesFile = new File("ApplicationLaunchCfg.properties")
    if(!propertiesFile.exists()){
      propertiesFile = new File(this.getClass.getClassLoader.getResource("").getPath+"/"+"ApplicationLaunchCfg.properties")
    }
    properties.load(new FileInputStream(propertiesFile))
    properties.get(propertyNameName).toString
  }
}
