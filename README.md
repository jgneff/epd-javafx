# Monocle EPD Tests for JavaFX

This project builds a JavaFX application to test the Monocle EPD platform. The associated website contains [videos of the application](https://jgneff.github.io/epd-javafx/) running on an actual e-paper display.

## Background

Support for a Monocle EPD platform was requested with [Issue #339](https://github.com/javafxports/openjdk-jfx/issues/339), "JDK-8217605: Add support for e-paper displays." The implementation is found under [Pull Request #369](https://github.com/javafxports/openjdk-jfx/pull/369).

## Licenses

The content of this project is licensed under the [GNU General Public License v3.0](https://choosealicense.com/licenses/gpl-3.0/) except for the following:

* The [SwingFXUtils](test/javafx/embed/swing/SwingFXUtils.java) class used by the unit tests is published by Oracle under the [GNU General Public License v2.0](https://choosealicense.com/licenses/gpl-2.0/) with the GNU Classpath Exception. See the [LICENSE](test/javafx/embed/swing/LICENSE) and [ADDITIONAL_LICENSE_INFO](test/javafx/embed/swing/ADDITIONAL_LICENSE_INFO) files for details.

* The video files in the [docs/videos](docs/videos) directory are licensed under the [Creative Commons Attribution-ShareAlike 4.0 International License](https://choosealicense.com/licenses/cc-by-sa-4.0/).

## Usage

The program accepts the parameters listed below, specified in the format: `--name=value`.

* **width** – the width of the JavaFX scene in pixels. The minimum width is 400. The default width is the width of the primary screen.
* **height** – the height of the JavaFX scene in pixels. The minimum height is 300. The default height is the height of the primary screen.
* **pattern** – the number of the pattern to display (see below). There are three patterns: 1 (move), 2 (sweep), and 3 (image). The default pattern is 1 (move).
* **levels** – the number of gray levels when the pattern is 2 (sweep). There are four gray levels: 1 (black), 2 (1-bit monochrome), 4 (2-bit grayscale), and 16 (4-bit grayscale). The default is 1 (black).
* **loops** – The number of animation loops. A value of zero means to loop until the application is terminated.
* **image** – The name of a [bilevel](https://en.wikipedia.org/wiki/Binary_image) and [coalesced](https://imagemagick.org/script/command-line-options.php#coalesce) animated GIF file when the pattern is 3 (image). The default file name is *duke-waving.gif*, bundled with the application. Any other image file must be located in the same directory as the *epd-javafx.jar* file.
* **patrol** – *true* to cycle back and forth between the first and last frames of the animation, called a *patrol cycle*; otherwise *false* to loop back to the first frame after the last. The default is *false*.

The *move* pattern is displayed by the [MoveAnimation](src/org/status6/epd/javafx/MoveAnimation.java) timer. The *sweep* pattern is displayed by the [SweepAnimation](src/org/status6/epd/javafx/SweepAnimation.java) timer. The *image* pattern is displayed by the [ImageAnimation](src/org/status6/epd/javafx/ImageAnimation.java) timer.

## Examples

Variations of the program parameters are shown below. The timing of the image on the left is set to approximate the speed of the animation on an actual e-paper display.

| Pattern | Description |
|:-------:|:------------|
| [![](docs/images/pattern1-a2-wait-200x150.gif)](docs/images/pattern1-a2-wait-800x600.gif) | Pattern 1, waveform mode 4, with wait, at 7.7 fps (130 ms/frame)<br>`java ... -Dmonocle.epd.waveformMode=4 -jar epd-javafx.jar` |
| [![](docs/images/pattern2-a2-nowait-200x150.gif)](docs/images/pattern2-a2-nowait-800x600.gif) | Pattern 2, waveform mode 4, no wait, at 20 fps (50 ms/frame)<br>`java ... -Dmonocle.epd.waveformMode=4 -Dmonocle.epd.noWait=true -jar epd-javafx.jar --pattern=2` |
| [![](docs/images/pattern2-auto-level01-200x150.gif)](docs/images/pattern2-auto-level01-800x600.gif) | Pattern 2, auto waveform mode, one gray level, at 4.3 fps (230 ms/frame)<br>`java ... -jar epd-javafx.jar --pattern=2 --levels=1` |
| [![](docs/images/pattern2-auto-level02-200x150.gif)](docs/images/pattern2-auto-level02-800x600.gif) | Pattern 2, auto waveform mode, two gray levels, at 4.8 fps (210 ms/frame)<br>`java ... -jar epd-javafx.jar --pattern=2 --levels=2` |
| [![](docs/images/pattern2-auto-level04-200x150.gif)](docs/images/pattern2-auto-level04-800x600.gif) | Pattern 2, auto waveform mode, four gray levels, at 1.9 fps (520 ms/frame)<br>`java ... -jar epd-javafx.jar --pattern=2 --levels=4` |
| [![](docs/images/pattern2-auto-level16-200x150.gif)](docs/images/pattern2-auto-level16-800x600.gif) | Pattern 2, auto waveform mode, 16 gray levels, at 1.9–2.2 fps (470–520 ms/frame), depending on the waveform mode selected<br>`java ... -jar epd-javafx.jar --pattern=2 --levels=16` |

## Building

The application is a project of the [Apache NetBeans IDE](https://netbeans.apache.org/) with the following settings:

* the Java platform is JDK 12 (the default),
* the source and binary format is JDK 12,
* a global library named "JavaFX 11" contains the JavaFX 11.0.2 SDK,
* the JavaFX JAR files are in *$HOME/lib/javafx-sdk-11.0.2/lib*, and
* the JavaFX source files are unzipped in *$HOME/lib/javafx-sdk-11.0.2/src*.
