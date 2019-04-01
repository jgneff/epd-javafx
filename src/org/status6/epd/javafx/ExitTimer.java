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
 * An animation timer that terminates the application after the specified number
 * of frames.
 *
 * @author John Neffenger
 */
public class ExitTimer extends AnimationTimer {

    private final int frames;

    private int count;

    /**
     * Creates a new exit timer.
     *
     * @param frames the number of frames displayed before terminating the
     * application
     */
    ExitTimer(int frames) {
        this.frames = frames;
    }

    @Override
    public void handle(long now) {
        if (frames != 0 && count == frames) {
            Platform.exit();
        }
        count++;
    }
}
