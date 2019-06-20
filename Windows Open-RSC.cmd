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
echo   %RED%1%NC% - Run Game
echo   %RED%2%NC% - Rank a player
echo   %RED%3%NC% - Backup database
echo   %RED%4%NC% - Restore database
echo   %RED%5%NC% - Reset entire database
echo   %RED%6%NC% - Exit
echo:
SET /P action=Please enter a number choice from above:
echo:

if /i "%action%"=="1" goto run
if /i "%action%"=="2" goto rank
if /i "%action%"=="3" goto backup
if /i "%action%"=="4" goto import
if /i "%action%"=="5" goto reset
if /i "%action%"=="6" goto exit

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


:<------------Begin Rank------------>
:rank
cls
echo:
echo What would you like rank a player as?
echo:
echo Choices:
echo   %RED%1%NC% - Admin
echo   %RED%2%NC% - Mod
echo   %RED%3%NC% - Regular Player
echo   %RED%4%NC% - Return
echo:
SET /P rank=Please enter a number choice from above:
echo:

if /i "%rank%"=="1" goto admin
if /i "%rank%"=="2" goto mod
if /i "%rank%"=="3" goto regular
if /i "%rank%"=="4" goto start

echo Error! %rank% is not a valid option. Press enter to try again.
echo:
SET /P rank=""
goto start

:admin
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set as an admin and press enter.
echo:
SET /P username=""
call START "" %mariadbpath%mysqld.exe --console
echo Player update will occur in 5 seconds (gives time to start the database server on slow PCs)
PING localhost -n 6 >NUL
call %mariadbpath%mysql.exe -uroot -proot -D openrsc -e "USE openrsc; UPDATE `openrsc_players` SET `group_id` = '1' WHERE `openrsc_players`.`username` = '%username%';"
echo:
echo %username% has been made an admin!
echo:
pause
goto start

:mod
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set as a mod and press enter.
echo:
SET /P username=""
call START "" %mariadbpath%mysqld.exe --console
echo Player update will occur in 5 seconds (gives time to start the database server on slow PCs)
PING localhost -n 6 >NUL
call %mariadbpath%mysql.exe -uroot -proot -D openrsc -e "USE openrsc; UPDATE `openrsc_players` SET `group_id` = '2' WHERE `openrsc_players`.`username` = '%username%';"
echo:
echo %username% has been made a mod!
echo:
pause
goto start

:regular
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set as a regular player and press enter.
echo:
SET /P username=""
call START "" %mariadbpath%mysqld.exe --console
echo Player update will occur in 5 seconds (gives time to start the database server on slow PCs)
PING localhost -n 6 >NUL
call %mariadbpath%mysql.exe -uroot -proot -D openrsc -e "USE openrsc; UPDATE `openrsc_players` SET `group_id` = '10' WHERE `openrsc_players`.`username` = '%username%';"
echo:
echo %username% has been made a regular player!
echo:
pause
goto start
:<------------End Rank------------>


:<------------Begin Backup------------>
:backup
REM Shuts down existing processes
taskkill /F /IM Java*

REM Performs a full database export
cls
echo:
echo What do you want to name your player database backup? (Avoid spaces and symbols for the filename)
echo:
SET /P filename=""
call START "" %mariadbpath%mysqld.exe --console
call START "" %mariadbpath%mysqldump.exe -uroot -proot --database openrsc --result-file="%filename%.sql"
echo:
echo Player database backup complete.
echo:
pause
goto start
:<------------End Backup------------>


:<------------Begin Import------------>
:import
REM Shuts down existing processes
taskkill /F /IM Java*

cls
REM Performs a full database import
echo:
dir /B *.sql
echo:
echo Which player database listed above do you wish to restore?
echo:
SET /P filename=""
call START "" %mariadbpath%mysqld.exe --console
call %mariadbpath%mysql.exe -uroot -proot openrsc < %filename%
echo:
echo Player database restore complete.
echo:
pause
goto start
:<------------End Import------------>


:<------------Begin Reset------------>
:reset
REM Shuts down existing processes
taskkill /F /IM Java*

REM Verifies the user wishes to clear existing player data
cls
echo:
echo Are you ABSOLUTELY SURE that you want to reset the player database?
echo:
echo To confirm the database reset, type yes and press enter.
echo:
SET /P confirmwipe=""
echo:
if /i "%confirmwipe%"=="yes" goto wipe

echo Error! %confirmwipe% is not a valid option.
pause
goto start

:wipe
REM Starts up the database server and imports both server and player database files to replace anything previousy existing
cls
echo:
call START "" %mariadbpath%mysqld.exe --console
echo Database wipe will occur in 5 seconds (gives time to start the database server on slow PCs)
PING localhost -n 6 >NUL
call %mariadbpath%mysql.exe -uroot -proot openrsc < Required\openrsc_game_server.sql
call %mariadbpath%mysql.exe -uroot -proot openrsc < Required\openrsc_game_players.sql
echo:
echo The player database has been reset!
echo:
pause
goto start
:<------------End Reset------------>


:<------------Begin Upgrade------------>
:upgrade
REM Shuts down existing processes
taskkill /F /IM Java*
taskkill /F /IM mysqld*

REM Fetches the most recent release version
echo:
call Required\curl -sL https://gitlab.openrsc.com/api/v4/projects/open-rsc%2Fsingle-player/releases | Required\grep "tag_name" | Required\egrep -o (ORSC-).[0-9].[0-9].[0-9] | Required\head -1 > version.txt
set /P version=<version.txt
echo %version%

REM Downloads the most recent release archive and copies the contents into "Single-Player"
cd ..
call Single-Player\Required\wget https://gitlab.openrsc.com/open-rsc/Single-Player/-/archive/%version%/Single-Player-%version%.zip
call Single-Player\Required\7za.exe x Single-Player-%version%.zip -aoa
cd Single-Player-%version%
call xcopy "*.*" "../Single-Player\" /K /D /H /Y

REM Cleans up files that are no longer needed
cd ..
call del Single-Player-%version%.zip
call rd /s /q Single-Player
call del Single-Player\version.txt

REM Launches the database server and imports only the server data, leaving player data safely alone
cd Single-Player
call START "" %mariadbpath%mysqld.exe --console
echo Database wipe will occur in 5 seconds (gives time to start the database server on slow PCs)
PING localhost -n 6 >NUL
call %mariadbpath%mysql.exe -uroot -proot < Required\openrsc_game_server.sql
echo:
echo Upgrade complete.
echo:
pause
goto start
:<------------End Upgrade------------>