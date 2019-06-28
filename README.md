# JavaFX on E-Paper

This project builds a Java application to test the new support in JavaFX for devices with electronic paper, also known as electronic ink. The project website contains [videos of the application](https://jgneff.github.io/epd-javafx/) running on an actual e-paper display.

[![Duke Waving](docs/images/duke-2019-03-22-180.png)](https://jgneff.github.io/epd-javafx/duke.html) [![Doll Dancing](docs/images/doll-2019-03-30-180.png)](https://jgneff.github.io/epd-javafx/doll.html)

The application uses only the *javafx.base* and *javafx.graphics* modules of the [JavaFX SDK](https://gluonhq.com/products/javafx/). Because it's written in Java, it runs without modification on desktop computers running Windows, macOS, or Linux. The Apache NetBeans IDE makes it easy to run, debug, and profile the application both locally on a development workstation and remotely on an e-paper device.

The goal is to be able to run JavaFX applications on e-paper devices simply by creating a *chroot* environment with a small [Ubuntu Base](http://cdimage.ubuntu.com/ubuntu-base/releases/14.04/release/) image and installing JavaFX with:

```ShellSession
$ sudo apt-get install openjfx
```

## Background

The support is added to JavaFX as a new [Monocle](https://wiki.openjdk.java.net/display/OpenJFX/Monocle) platform called *EPD,* for E-Paper Display. The design of the EPD platform is described by the Request for Enhancement in [Issue #339](https://github.com/javafxports/openjdk-jfx/issues/339), "JDK-8217605: Add support for e-paper displays." The implementation for JavaFX 13 is found in [Pull Request #369](https://github.com/javafxports/openjdk-jfx/pull/369).

## Licenses

The content of this project is licensed under the [GNU General Public License v3.0](https://choosealicense.com/licenses/gpl-3.0/) except for the video files in the [docs/videos](docs/videos) directory, which are licensed under the Creative Commons [Attribution-ShareAlike 4.0 International](https://choosealicense.com/licenses/cc-by-sa-4.0/) license.

## Building

The application is a project of the [Apache NetBeans IDE](https://netbeans.apache.org/) with the following settings:

* the Java platform is the default (JDK 12 on my system),
* the source and binary format is JDK 12,
* a global library named "JavaFX 12" contains the JavaFX SDK version 12.0.1,
* the JavaFX JAR files are in *${user.home}/lib/javafx-sdk-12.0.1/lib*, and
* the JavaFX source files in the archive *lib/src.zip* are unzipped into *${user.home}/lib/javafx-sdk-12.0.1/src*.

## Usage

The program accepts the parameters listed below, specified in the format: `--name=value`.

* **width** – the width of the JavaFX scene in pixels. The minimum width is 400. The default width is the width of the primary screen.
* **height** – the height of the JavaFX scene in pixels. The minimum height is 300. The default height is the height of the primary screen.
* **pattern** – the number of the pattern to display (see the examples below). There are three patterns: 1 (move), 2 (sweep), and 3 (image). The default pattern is 1 (move).
* **levels** – the number of gray levels when the pattern is 2 (sweep). There are four levels: 1 (black), 2 (1-bit monochrome), 4 (2-bit grayscale), and 16 (4-bit grayscale). The default is 1 (black).
* **loops** – The number of animation loops. A value of 0 (zero) means to loop until the application is terminated.
* **image** – The name of a [bilevel](https://en.wikipedia.org/wiki/Binary_image) and [coalesced](https://imagemagick.org/script/command-line-options.php#coalesce) animated GIF file when the pattern is 3 (image). The default file name is *duke-waving.gif*, bundled with the application. The default file name for the unit tests is *doll-dancing.gif*, found under the test directory. Any other image file must be located in the same directory as the *epd-javafx.jar* file.
* **patrol** – The type of loop cycle when the pattern is 3 (image): *true* to cycle back and forth between the first and last frames of the animation, called a *patrol cycle*; otherwise *false* to loop back to the first frame after the last. The default is *false*.

The animation patterns are implemented as subclasses of the JavaFX [AnimationTimer](https://openjfx.io/javadoc/12/javafx.graphics/javafx/animation/AnimationTimer.html) class:

* The *move* pattern is displayed by the [MoveAnimation](src/org/status6/epd/javafx/MoveAnimation.java) timer.
* The *sweep* pattern is displayed by the [SweepAnimation](src/org/status6/epd/javafx/SweepAnimation.java) timer.
* The *image* pattern is displayed by the [ImageAnimation](src/org/status6/epd/javafx/ImageAnimation.java) timer.

## Examples

Variations of the application parameters and system properties are shown below. The timing of the image on the left is set to approximate the speed of the animation on an actual e-paper display. See the [videos](https://jgneff.github.io/epd-javafx/) for examples of these patterns on e-paper, including pattern 3 (image).

| Pattern | Description |
|:-------:|:------------|
| [![](docs/images/pattern1-a2-wait-200x150.gif)](docs/images/pattern1-a2-wait-800x600.gif) | Pattern 1, waveform mode 4, with wait, at 7.7 fps (130 ms/frame)<br>`java ... -Dmonocle.epd.waveformMode=4 -jar epd-javafx.jar` |
| [![](docs/images/pattern2-a2-nowait-200x150.gif)](docs/images/pattern2-a2-nowait-800x600.gif) | Pattern 2, waveform mode 4, no wait, at 20 fps (50 ms/frame)<br>`java ... -Dmonocle.epd.waveformMode=4 -Dmonocle.epd.noWait=true -jar epd-javafx.jar --pattern=2` |
| [![](docs/images/pattern2-auto-level01-200x150.gif)](docs/images/pattern2-auto-level01-800x600.gif) | Pattern 2, auto waveform mode, one gray level, at 4.3 fps (230 ms/frame)<br>`java ... -jar epd-javafx.jar --pattern=2 --levels=1` |
| [![](docs/images/pattern2-auto-level02-200x150.gif)](docs/images/pattern2-auto-level02-800x600.gif) | Pattern 2, auto waveform mode, two gray levels, at 4.8 fps (210 ms/frame)<br>`java ... -jar epd-javafx.jar --pattern=2 --levels=2` |
| [![](docs/images/pattern2-auto-level04-200x150.gif)](docs/images/pattern2-auto-level04-800x600.gif) | Pattern 2, auto waveform mode, four gray levels, at 1.9 fps (520 ms/frame)<br>`java ... -jar epd-javafx.jar --pattern=2 --levels=4` |
| [![](docs/images/pattern2-auto-level16-200x150.gif)](docs/images/pattern2-auto-level16-800x600.gif) | Pattern 2, auto waveform mode, 16 gray levels, at 1.9–2.2 fps (470–520 ms/frame), depending on the waveform mode selected<br>`java ... -jar epd-javafx.jar --pattern=2 --levels=16` |
