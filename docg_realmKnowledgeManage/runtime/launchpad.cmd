@echo off
setlocal EnableDelayedExpansion
set EXTCLASS=extCLASS\*
set LIB=lib\*
"java" -XX:+UseG1GC -XX:+DisableExplicitGC -Xmx10G -Xrs -cp docg_realmKnowledgeManage-0.5.jar;%EXTCLASS%;%LIB% "com.viewfunction.docg.knowledgeManage.consoleApplication.KnowledgeManagementApplicationLauncher"