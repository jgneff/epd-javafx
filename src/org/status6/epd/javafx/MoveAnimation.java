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

    private static final int BOX_WIDTH = 100;
    private static final int BOX_HEIGHT = 100;

    private final GraphicsContext graphics;
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
        boxesPerWidth = (int) canvas.getWidth() / BOX_WIDTH;
        boxesPerHeight = (int) canvas.getHeight() / BOX_HEIGHT;
        boxesPerScreen = boxesPerWidth * boxesPerHeight;
    }

    @Override
    int getNumFrames() {
        return boxesPerScreen;
    }

    @Override
    public void handle(long now) {
        double x = (count % boxesPerWidth) * BOX_WIDTH;
        double y = ((count / boxesPerWidth) % boxesPerHeight) * BOX_HEIGHT;
        graphics.setFill(Color.BLACK);
        graphics.fillRect(x, y, BOX_WIDTH, BOX_HEIGHT);
        if (count > 0) {
            graphics.setFill(Color.WHITE);
            graphics.fillRect(oldX, oldY, BOX_WIDTH, BOX_HEIGHT);
        }
        oldX = x;
        oldY = y;
        count++;
    }
}
