#!/bin/bash

source ../test-jdk17.sh

java -Xms1g -Xmx1g -jar target/g2d-jfreepdf-1.0.0-jar-with-dependencies.jar

