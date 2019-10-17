@echo off
:# Open RSC: Striving for a replica RSC game and more

:# Path variables:
SET mariadbpath="mariadb10.3.8\bin\"

call START /min "" %mariadbpath%mysqld.exe --console