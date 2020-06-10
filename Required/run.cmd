@echo off
:# Open RSC: Striving for a replica RSC game and more

:# Path variables:
SET mariadbpath="mariadb-10.5.3-winx64\bin\"
SET antpath="apache-ant-1.10.5\bin\"

call START /min "" %mariadbpath%mysqld.exe --console
call START /min "" %antpath%ant -f ../server\build.xml runserver

echo Launching the game in 10 seconds (gives time to start on slow PCs)
PING localhost -n 11 >NUL
call %mariadbpath%mysqlcheck -uroot -proot -o --all-databases > db_check_log.txt
call %mariadbpath%mysql.exe -uroot -proot -D openrsc -e "USE openrsc; UPDATE `players` SET online = '0';"
cd ../client
call java -jar Open_RSC_Client.jar
taskkill /F /IM Java*
taskkill /F /IM mysqld*
exit
