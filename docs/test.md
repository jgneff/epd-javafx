---
title: System Tests
---

## System Tests

This page contains one of [several videos](index.html) showing a [JavaFX application](https://github.com/jgneff/epd-javafx) running on a device with an e-paper display.

The video below shows the system tests for the JavaFX Monocle EPD platform running on a 2013 Kobo Touch N905C e-reader. The associated [log file](logs/test-2019-03-22.log) contains all of the messages printed during the test with *javafx.level* set to *FINER* in the logging properties. All of the animation patterns are displayed by the [MoveAnimation](https://github.com/jgneff/epd-javafx/blob/master/src/org/status6/epd/javafx/MoveAnimation.java) or [SweepAnimation](https://github.com/jgneff/epd-javafx/blob/master/src/org/status6/epd/javafx/SweepAnimation.java) subclasses of the [AnimationTimer](https://openjfx.io/javadoc/12/javafx.graphics/javafx/animation/AnimationTimer.html) class.

The [video file](videos/test-2019-03-22.webm "Download") is a WebM container with the VP9 video coding format.

<video src="videos/test-2019-03-22.webm" poster="images/test-2019-03-22-360.png" width="640" height="360" controls>
<p><em>To watch the video here, your browser must support the WebM format with VP9 encoding.</em></p>
</video>

[![CC BY-SA 4.0](images/by-sa.png)](http://creativecommons.org/licenses/by-sa/4.0/) © 2019 John Neffenger. The *System Tests* video is licensed under the [Creative Commons Attribution-ShareAlike 4.0 International License](http://creativecommons.org/licenses/by-sa/4.0/).
