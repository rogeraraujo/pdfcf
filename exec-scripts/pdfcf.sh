#!/bin/bash

JAVA_CMD=java
JAVA_OPTIONS=-Dswing.aatext=true -Dsun.java2d.d3d=false
JAR_FILE=pdfcf.jar
PDFCF_OPTIONS=-listLafs

${JAVA_CMD} ${JAVA_OPTIONS} -jar ${JAR_FILE} ${PDFCF_OPTIONS}
