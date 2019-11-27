#!/bin/bash
# Runs the EPD animation tests
trap exit INT TERM
set -o errexit

# JDK and JavaFX SDK
JAVA_HOME=$HOME/opt/jdk-13.0.1+9
JAVAFX_LIB=$HOME/lib/armv6hf-sdk/lib

rootdir=$HOME/src/epd-javafx
argfile=$rootdir/bin/epdargs.conf
jarfile=$rootdir/dist/epd-javafx.jar
logfile=$rootdir/conf/logging.properties

cmd="$JAVA_HOME/bin/java @$argfile --module-path=$JAVAFX_LIB \
    -Djava.util.logging.config.file=$logfile"

printf "\n===> 16 gray levels with color depths: 8, 16, 32 bits/px\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bits/px\n"
    $cmd -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.waveformMode=2 \
        -jar $jarfile --pattern=2 --levels=16
done

printf "\n===> Black with color depths: 8, 16, 32 bits/px\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bits/px\n"
    $cmd -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.waveformMode=4 \
        -jar $jarfile --pattern=1
done

printf "\n===> Rotations: 0 (UR), 1 (CW), 2 (UD), 3 (CCW)\n"
for n in 0 1 2 3; do
    printf "===> Rotation: $n\n"
    $cmd @$rootdir/bin/rotate${n}.conf -Dmonocle.epd.rotate=$n \
        -Dmonocle.epd.waveformMode=4 -jar $jarfile --pattern=1
done

printf "\n===> Inverted 8-bit grayscale\n"
$cmd -Dmonocle.epd.bitsPerPixel=8 -Dmonocle.epd.y8inverted=true \
    -Dmonocle.epd.waveformMode=4 -jar $jarfile --pattern=1

printf "\n===> No wait with color depths: 8, 16, 32 bits/px\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bits/px\n"
    $cmd -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.noWait=true \
        -Dmonocle.epd.waveformMode=4 -jar $jarfile --pattern=2 --levels=1
done

printf "\n===> Black with modes: 4 (A2), 1 (DU), 3 (GC4), 2 (GC16)\n"
for n in 4 1 3 2; do
    printf "===> Waveform mode: $n\n"
    $cmd -Dmonocle.epd.waveformMode=$n -jar $jarfile --pattern=1
done

printf "\n===> 16 gray levels with modes: 4 (A2), 1 (DU), 3 (GC4), 2 (GC16)\n"
for n in 4 1 3 2; do
    printf "===> Waveform mode: $n\n"
    $cmd -Dmonocle.epd.waveformMode=$n -jar $jarfile --pattern=2 --levels=16
done

printf "\n===> Automatic waveform selection with gray levels: 1, 2, 4, 16\n"
for n in 1 2 4 16; do
    printf "===> Gray levels: $n\n"
    $cmd -jar $jarfile --pattern=2 --levels=$n
done

printf "\n===> Enable inversion update flag\n"
$cmd -Dmonocle.epd.waveformMode=4 -Dmonocle.epd.enableInversion=true \
    -jar $jarfile --pattern=1

printf "\n===> Force monochrome update flag with gray levels: 4, 16\n"
for n in 4 16; do
    printf "===> Gray level: $n\n"
    $cmd -Dmonocle.epd.waveformMode=4 -Dmonocle.epd.forceMonochrome=true \
        -jar $jarfile --pattern=2 --levels=$n
done

printf "\n===> Frame rate using move pattern with wait (16 times)\n"
$cmd -Dmonocle.epd.waveformMode=4 -jar $jarfile --pattern=1 --loops=16

printf "\n===> Frame rate using sweep pattern and no wait (16 times)\n"
$cmd -Dmonocle.epd.noWait=true -Dmonocle.epd.waveformMode=4 \
    -jar $jarfile --pattern=2 --levels=1 --loops=16
