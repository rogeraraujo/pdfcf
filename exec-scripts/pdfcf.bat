@echo off

set JAVA_CMD=java
set JAVA_OPTIONS=-Dswing.aatext=true -Dsun.java2d.d3d=false
set JAR_FILE=pdfcf.jar
set PDFCF_OPTIONS=-listLafs

%JAVA_CMD% %JAVA_OPTIONS% -jar %JAR_FILE% %PDFCF_OPTIONS%
