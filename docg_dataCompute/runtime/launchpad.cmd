@echo off
setlocal EnableDelayedExpansion
set EXTCLASS=extCLASS\*
set LIB=lib\*
"java" -XX:+UseG1GC -XX:+DisableExplicitGC -Xmx10G -Xrs -cp docg_dataCompute-0.1.0.jar;%EXTCLASS%;%LIB% "com.viewfunction.docg.dataCompute.consoleApplication.DataComputeApplicationLauncher"