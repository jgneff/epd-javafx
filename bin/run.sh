#!/bin/bash
# Runs the EPD JavaFX Animator program

# Checks that this script was run with sudo
if [ -z "$SUDO_USER" ]; then
    printf "Error: This script must run as root with sudo.\n"
    exit 1
fi

# Sets HOME to the home directory of the user who invoked sudo
HOME=/home/$SUDO_USER

# JDK and JavaFX SDK
JAVA_HOME=$HOME/opt/jdk-15+36-jre
JAVAFX_LIB=$HOME/lib/armv6hf-sdk/lib

apphome=$HOME/src/epd-javafx
argfile=$apphome/bin/epdargs.conf
jarfile=$apphome/dist/epd-javafx.jar
logfile=$apphome/conf/logging.properties

$JAVA_HOME/bin/java --show-version \
    @$argfile --module-path=$JAVAFX_LIB \
    -Djava.util.logging.config.file=$logfile \
    -Dmonocle.epd.waveformMode=4 -jar $jarfile $@
