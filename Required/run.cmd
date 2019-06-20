@echo off
:# Open RSC: A replica RSC private server framework

:# Path variables:
SET antpath="apache-ant-1.10.5\bin\"

call START /min "" %antpath%ant -f server\build.xml runserver

echo Launching the game in 10 seconds (gives time to start on slow PCs)
PING localhost -n 11 >NUL
call java -jar Open_RSC_Client.jar
taskkill /F /IM Java*
exit
