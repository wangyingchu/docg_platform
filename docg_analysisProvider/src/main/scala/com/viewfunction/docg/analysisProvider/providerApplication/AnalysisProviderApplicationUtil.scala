package com.viewfunction.docg.analysisProvider.providerApplication

import java.io.{File, FileInputStream}
import java.util.{Date, Properties}

object AnalysisProviderApplicationUtil {

  val properties = new Properties()

  def printApplicationBanner(): Unit = {
    val bannerStringBuffer = new StringBuffer
    val echo0 = "====================================================================="
    bannerStringBuffer.append(echo0)
    bannerStringBuffer.append("\n\r")
    bannerStringBuffer.append(getApplicationProperty("applicationName") + " | " + "ver. " + getApplicationProperty("applicationVersion"))
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

  def getApplicationProperty(propertyNameName: String): String = {
    if(properties.isEmpty){
      var propertiesFile = new File("analysisProviderConfiguration.properties")
      if(!propertiesFile.exists()){
        propertiesFile = new File(this.getClass.getClassLoader.getResource("").getPath+"/"+"analysisProviderConfiguration.properties")
      }
      properties.load(new FileInputStream(propertiesFile))
    }
    properties.get(propertyNameName).toString
  }
}
