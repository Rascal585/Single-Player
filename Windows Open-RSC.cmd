@echo off
:# Open RSC: A replica RSC private server framework

:# Variable paths:
SET required="Required\"
SET mariadbpath="Required\mariadb10.3.8\bin\"

:<------------Begin Start------------>
REM Initial menu displayed to the user
:start
cls
echo:
echo What would you like to do?
echo:
echo Choices:
echo   %RED%1%NC% - Run Single Player
echo   %RED%2%NC% - Exit
echo:
SET /P action=Please enter a number choice from above:
echo:

if /i "%action%"=="1" goto run
if /i "%action%"=="2" goto exit

echo Error! %action% is not a valid option. Press enter to try again.
echo:
SET /P action=""
goto start
:<------------End Start------------>


:<------------Begin Exit------------>
:exit
REM Shuts down existing processes
taskkill /F /IM Java*
taskkill /F /IM mysqld*
exit
:<------------End Exit------------>


:<------------Begin Run------------>
:run
cls
echo:
echo Starting Open RSC.
echo:
cd Required && call START "" run.cmd && cd ..
echo:
goto start
:<------------End Run------------>