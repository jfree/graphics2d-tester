#!/bin/bash

source ../test-jdk17.sh

java -Xms1g -Xmx1g -verbose:gc -cp target/g2d-skijagraphics2d-1.0.0-jar-with-dependencies.jar org.jfree.graphics2d.tester.skija.jwm.SkijaGraphics2DWithJWMRunner

