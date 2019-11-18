#!/bin/bash
# Runs the EPD JavaFX Animator program

# JDK and JavaFX SDK
JAVA_HOME=$HOME/opt/jdk-13.0.1+9
JAVAFX_LIB=$HOME/lib/armv6hf-sdk/lib

rootdir=$HOME/src/epd-javafx
argfile=$rootdir/bin/epdargs.conf
jarfile=$rootdir/dist/epd-javafx.jar
logfile=$rootdir/conf/logging.properties

$JAVA_HOME/bin/java @$argfile --module-path=$JAVAFX_LIB \
    -Djava.util.logging.config.file=$logfile \
    -Dmonocle.epd.waveformMode=4 -jar $jarfile $@
