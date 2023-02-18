#!/bin/bash

source ../test-zulu17.sh

#BACKEND=sw
BACKEND=es2,sw

OPTS=""
#OPTS="-Dprism.verbose=true"

# -Dprism.printAllocs=true 
java -Xms1g -Xmx1g $OPTS -Djavafx.animation.fullspeed=true -Dprism.allowhidpi=false -Dprism.order=${BACKEND} -Dglass.gtk.uiScale=1.0 -Dsun.java2d.uiScale=1.0 -jar target/g2d-fxgraphics2d-1.0.0-jar-with-dependencies.jar

