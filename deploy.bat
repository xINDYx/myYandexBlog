@echo off
chcp 1251 >nul
setlocal enabledelayedexpansion

set "TOMCAT_WEBAPPS=C:\Program Files\Apache Software Foundation\Tomcat 11.0\webapps"
set "DEPLOY_WAR=build\libs\myYandexBlog-1.0.0.war"

if exist "%TOMCAT_WEBAPPS%\blog" (
    echo Удаляем папку blog...
    rmdir /s /q "%TOMCAT_WEBAPPS%\blog"
)

if exist "%TOMCAT_WEBAPPS%\blog.war" (
    echo Удаляем старый blog.war...
    del "%TOMCAT_WEBAPPS%\blog.war"
)

if exist "%DEPLOY_WAR%" (
    echo Копируем новый blog.war...
    copy "%DEPLOY_WAR%" "%TOMCAT_WEBAPPS%\"
) else (
    echo Ошибка: файл %DEPLOY_WAR% не найден!
)

echo Операция завершена.
pause