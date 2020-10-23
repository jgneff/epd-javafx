#!/bin/bash
# Runs the EPD animation tests
trap exit INT TERM
set -o errexit

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

$JAVA_HOME/bin/java -version
cmd="$JAVA_HOME/bin/java @$argfile --module-path=$JAVAFX_LIB \
    -Djava.util.logging.config.file=$logfile"

printf "\n===> 16 gray levels with color depths: 8, 16, 32 bpp\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bpp\n"
    $cmd -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.waveformMode=2 \
        -jar $jarfile --pattern=2 --levels=16
done

printf "\n===> Black with color depths: 8, 16, 32 bpp\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bpp\n"
    $cmd -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.waveformMode=4 \
        -jar $jarfile --pattern=1
done

printf "\n===> Color depths and rotations: 0 (UR), 1 (CW), 2 (UD), 3 (CCW)\n"
for n in 8 16 32; do
    for rot in 0 1 2 3; do
        printf "===> Color depth: $n bpp; Rotation: $rot\n"
        $cmd @$apphome/bin/rotate${rot}.conf -Dmonocle.epd.rotate=$rot \
            -Dmonocle.epd.bitsPerPixel=$n -Dmonocle.epd.waveformMode=4 \
            -jar $jarfile --pattern=1
    done
done

printf "\n===> Inverted 8-bit grayscale\n"
$cmd -Dmonocle.epd.bitsPerPixel=8 -Dmonocle.epd.Y8Inverted=true \
    -Dmonocle.epd.waveformMode=4 -jar $jarfile --pattern=1

printf "\n===> No wait with color depths: 8, 16, 32 bpp\n"
for n in 8 16 32; do
    printf "===> Color depth: $n bpp\n"
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
