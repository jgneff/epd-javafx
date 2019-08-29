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

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An animation timer that displays a box moving across the screen from left to
 * right, top to bottom.
 *
 * @author John Neffenger
 */
class MoveAnimation extends FiniteAnimation {

    private static final int NUM_BOXES_LONG = 8;
    private static final int NUM_BOXES_SHORT = 6;

    private final GraphicsContext graphics;
    private final int boxWidth;
    private final int boxHeight;
    private final int boxesPerWidth;
    private final int boxesPerHeight;
    private final int boxesPerScreen;

    private long count;
    private double oldX;
    private double oldY;

    /**
     * Creates a new moving box animation.
     *
     * @param canvas the canvas on which to draw the animation
     */
    MoveAnimation(Canvas canvas) {
        graphics = canvas.getGraphicsContext2D();
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        boolean landscape = width > height;
        boxWidth = landscape ? width / NUM_BOXES_LONG : width / NUM_BOXES_SHORT;
        boxHeight = landscape ? height / NUM_BOXES_SHORT : height / NUM_BOXES_LONG;
        boxesPerWidth = width / boxWidth;
        boxesPerHeight = height / boxHeight;
        boxesPerScreen = boxesPerWidth * boxesPerHeight;
    }

    @Override
    int getNumFrames() {
        return boxesPerScreen;
    }

    @Override
    public void handle(long now) {
        double x = (count % boxesPerWidth) * boxWidth;
        double y = ((count / boxesPerWidth) % boxesPerHeight) * boxHeight;
        graphics.setFill(Color.BLACK);
        graphics.fillRect(x, y, boxWidth, boxHeight);
        if (count > 0) {
            graphics.setFill(Color.WHITE);
            graphics.fillRect(oldX, oldY, boxWidth, boxHeight);
        }
        oldX = x;
        oldY = y;
        count++;
    }
}
