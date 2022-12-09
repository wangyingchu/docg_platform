#!/bin/sh
#############################################################################
#
# NAME:launchpad.sh
#
# FUNCTION: 启动 Data Compute 服务节点.
#
# USAGE:sh launchpad.sh
#
#############################################################################
WORKINGDIR=`/bin/pwd`
#echo $WORKINGDIR
# Setup runtime lib classpath
# Add service lib jar here
for l in lib/*;
do LIB=$WORKINGDIR/$l:"$LIB";
done
LIB="$WORKINGDIR/docg_dataCompute-0.1.0.jar":"$LIB"
#echo $LIB
#Add extedned jar here
EXTENDCLASSPATH=.
for i in extCLASS/*;
do EXTENDCLASSPATH=$WORKINGDIR/$i:"$EXTENDCLASSPATH";
done
#echo $EXTENDCLASSPATH

#Start main program
# Must add -Xrs here, otherwise when start this shell from nohup, after telnet window closes the java process will closed, add --add-opens parts to support Java 17
"java" -XX:+UseG1GC -XX:+DisableExplicitGC -Xmx10G -Xrs \
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED \
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED \
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED \
--add-opens=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED \
--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED \
--add-opens=java.base/java.io=ALL-UNNAMED \
--add-opens=java.base/java.nio=ALL-UNNAMED \
--add-opens=java.base/java.util=ALL-UNNAMED \
--add-opens=java.base/java.lang=ALL-UNNAMED \
-cp $EXTENDCLASSPATH:$LIB "com.viewfunction.docg.dataCompute.consoleApplication.DataComputeApplicationLauncher"
