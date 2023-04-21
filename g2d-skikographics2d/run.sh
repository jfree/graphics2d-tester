#!/bin/bash

source ../test-jdk17.sh

java -Xms1g -Xmx1g -verbose:gc -jar target/g2d-skikographics2d-1.0.0-jar-with-dependencies.jar

