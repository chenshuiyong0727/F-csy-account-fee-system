@echo off
chcp 65001 > nul
D:
cd D:\account-fee-system\winsw
account-fee-service.exe stop
pause
