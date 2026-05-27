@echo off
chcp 65001 > nul
title 会计服务费收款登记系统

if exist "D:\Java\21\bin\java.exe" (
    set "JAVA_HOME=D:\Java\21"
    set "PATH=D:\Java\21\bin;%PATH%"
)

java -server -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -jar account-fee-system.jar --spring.config.location=backend/src/main/resources/application-prod.yml
pause
