#!/bin/bash
# Runs the EPD JavaFX Animator program
trap exit INT TERM
set -o errexit

dir=$(dirname $0)
jdkdir=$HOME/opt/jdk-13+33
jfxlib=$HOME/lib/armv6hf-sdk/lib
epdjar=$dir/../dist/epd-javafx.jar

$jdkdir/bin/java --add-modules=javafx.graphics --module-path=$jfxlib \
    -Djava.util.logging.config.file=$dir/../conf/logging.properties \
    -Dglass.platform=Monocle -Dmonocle.platform=EPD -Dprism.order=sw \
    -Dmonocle.input.18/0/0/0.maxX=800 -Dmonocle.input.18/0/0/0.maxY=600 \
    -Dmonocle.epd.waveformMode=4 -jar $epdjar "$@"
