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

/**
 * An abstract class for animations consisting of a finite sequence of frames.
 *
 * @author John Neffenger
 */
abstract class FiniteAnimation extends AnimationTimer {

    /**
     * Gets the number of frames in this animation.
     *
     * @return the number of frames
     */
    abstract int getNumFrames();
}
