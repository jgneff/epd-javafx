/*
 * Copyright (C) 2019 John Glenn Neffenger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.status6.epd.javafx;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;

/**
 * An animation timer that tracks the frame rate and logs its measurement each
 * time it completes the specified number of frames.
 *
 * @author John Neffenger
 */
class RateTimer extends AnimationTimer {

    private static final long NANOS_PER_SECOND = 1_000_000_000;
    private static final long NANOS_PER_MILLIS = 1_000_000;

    private final int frames;

    private long begin;
    private long start;
    private int count;

    /**
     * Creates a new frame rate timer.
     *
     * @param frames the number of frames in each logging interval
     */
    RateTimer(int frames) {
        this.frames = frames;
    }

    /**
     * Schedules the logging on the JavaFX Application Thread, capturing the
     * duration of the time interval and the number of frames displayed.
     *
     * @implNote Alternative methods caused a noticeable delay when first
     * invoked, such as formatting the message with
     * {@link java.text.MessageFormat MessageFormat}, logging the message with a
     * {@link java.util.logging.Logger Logger}, or printing the message directly
     * on the current thread. Switching threads with a separate
     * {@link java.util.concurrent.Executors#newSingleThreadExecutor Executor}
     * just adds more overhead on a single-core device.
     *
     * @param duration the duration of the time interval in nanoseconds
     * @param number the number of frames displayed during the interval
     * @param prefix the prefix for the frame rate message
     */
    private void capture(final long duration, final long number, final String prefix) {
        Platform.runLater(() -> {
            double seconds = (double) duration / NANOS_PER_SECOND;
            double milliseconds = (double) duration / NANOS_PER_MILLIS;
            System.out.println(String.format(
                    "%s: %d frames in %5.2f s = %5.2f fps (%.0f ms/frame)",
                    prefix, number, seconds, number / seconds, milliseconds / number));
        });
    }

    @Override
    public void stop() {
        super.stop();
        capture(System.nanoTime() - begin, count, "Total rate");
    }

    @Override
    public void handle(long now) {
        if (count % frames == 0) {
            if (count == 0) {
                begin = now;
                start = now;
            } else {
                capture(now - start, frames, "Frame rate");
                start = now;
            }
        }
        count++;
    }
}
