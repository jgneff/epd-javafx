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

cmd="$JAVA_HOME/bin/java @$argfile --module-path=$JAVAFX_LIB -Djava.util.logging.config.file=$logfile"

printf "\n===> Testing frame buffer color depths in 4-bit grayscale: 8, 16, 32 bpp ...\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bpp ...\n"
    $cmd -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.waveformMode=2 -jar $jarfile --pattern=2 --levels=16
done

printf "\n===> Testing frame buffer color depths in 1-bit grayscale: 8, 16, 32 bpp ...\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bpp ...\n"
    $cmd -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.waveformMode=4 -jar $jarfile
done

printf "\n===> Testing frame buffer rotations: 0 (UR), 1 (CW), 2 (UD), 3 (CCW) ...\n"
for n in 0 1 2 3; do
    printf "===> Rotation: $n ...\n"
    $cmd @$rootdir/bin/rotate${n}.conf -Dmonocle.epd.rotate=$n -Dmonocle.epd.waveformMode=4 -jar $jarfile
done

printf "\n===> Testing Y8-inverted frame buffer ...\n"
$cmd -Dmonocle.epd.bitsPerPixel=8 -Dmonocle.epd.y8inverted=true -Dmonocle.epd.waveformMode=4 -jar $jarfile

printf "\n===> Testing no wait with frame buffer color depths: 8, 16, 32 bpp ...\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bpp ...\n"
    $cmd -Dmonocle.epd.noWait=true -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.waveformMode=4 -jar $jarfile --pattern=2
done

printf "\n===> Testing waveform modes in 1-bit grayscale: 4 (A2), 1 (DU), 3 (GC4), 2 (GC16) ...\n"
for n in 4 1 3 2; do
    printf "===> Waveform mode: $n ...\n"
    $cmd -Dmonocle.epd.waveformMode=$n -jar $jarfile
done

printf "\n===> Testing waveform modes in 4-bit grayscale: 4 (A2), 1 (DU), 3 (GC4), 2 (GC16) ...\n"
for n in 4 1 3 2; do
    printf "===> Waveform mode: $n ...\n"
    $cmd -Dmonocle.epd.waveformMode=$n -jar $jarfile --pattern=2 --levels=16
done

printf "\n===> Testing automatic waveform mode selection with gray levels: 1, 2, 4, 16 ...\n"
for n in 1 2 4 16; do
    printf "===> Gray levels: $n ...\n"
    $cmd -jar $jarfile --pattern=2 --levels=$n
done

printf "\n===> Testing update flag: enable inversion ...\n"
$cmd -Dmonocle.epd.enableInversion=true -Dmonocle.epd.waveformMode=4 -jar $jarfile

printf "\n===> Testing update flag: force monochrome (gray levels 4, 16) ...\n"
for n in 4 16; do
    printf "===> Gray levels: $n ...\n"
    $cmd -Dmonocle.epd.forceMonochrome=true -Dmonocle.epd.waveformMode=4 -jar $jarfile --pattern=2 --levels=$n
done

printf "\n===> Testing frame rate (move pattern, with wait): 16 times ...\n"
$cmd -Dmonocle.epd.waveformMode=4 -jar $jarfile --loops=16

printf "\n===> Testing frame rate (sweep pattern, no wait): 16 times ...\n"
$cmd -Dmonocle.epd.waveformMode=4 -Dmonocle.epd.noWait=true -jar $jarfile --pattern=2 --loops=16
