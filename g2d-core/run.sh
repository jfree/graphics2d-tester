#!/bin/bash

source ~/test-jdk17.sh

java -Xms1g -Xmx1g -Dsun.java2d.uiScale=1.0 -jar target/g2d-core-1.0.0-jar-with-dependencies.jar

