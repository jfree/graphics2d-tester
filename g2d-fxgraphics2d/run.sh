#!/bin/bash

source ~/test-zulu17.sh

#BACKEND=sw
BACKEND=es2

java -Xms1g -Xmx1g -Dprism.verbose=true -Dprism.allowhidpi=false -Dprism.order=${BACKEND} -Dglass.gtk.uiScale=1.0 -Dsun.java2d.uiScale=1.0 -jar target/g2d-fxgraphics2d-1.0.0-jar-with-dependencies.jar

