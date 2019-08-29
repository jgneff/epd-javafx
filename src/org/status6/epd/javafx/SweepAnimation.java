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
 * An animation timer that displays a box sweeping across the screen from left
 * to right, top to bottom, alternating between grayscale boxes and all white
 * boxes on each cycle.
 *
 * @author John Neffenger
 */
class SweepAnimation extends FiniteAnimation {

    private static final int NUM_BOXES_LONG = 8;
    private static final int NUM_BOXES_SHORT = 6;

    private final GraphicsContext graphics;
    private final Color[] grays;
    private final int boxWidth;
    private final int boxHeight;
    private final int boxesPerWidth;
    private final int boxesPerHeight;
    private final int boxesPerScreen;

    private long count;
    private int index;

    /**
     * Gets an array of grayscale values with the given number of gray levels.
     *
     * @param levels the number of gray levels
     * @return an array of grayscale values
     */
    private static Color[] getGrays(int levels) {
        Color[] grays;
        switch (levels) {
            case 2:
                grays = new Color[]{Color.WHITE, Color.BLACK};
                break;
            case 4:
                grays = new Color[]{
                    Color.WHITE,
                    Color.grayRgb(0xAA),
                    Color.grayRgb(0x55),
                    Color.BLACK
                };
                break;
            case 16:
                grays = new Color[]{
                    Color.WHITE,
                    Color.grayRgb(0xEE),
                    Color.grayRgb(0xDD),
                    Color.grayRgb(0xCC),
                    Color.grayRgb(0xBB),
                    Color.grayRgb(0xAA),
                    Color.grayRgb(0x99),
                    Color.grayRgb(0x88),
                    Color.grayRgb(0x77),
                    Color.grayRgb(0x66),
                    Color.grayRgb(0x55),
                    Color.grayRgb(0x44),
                    Color.grayRgb(0x33),
                    Color.grayRgb(0x22),
                    Color.grayRgb(0x11),
                    Color.BLACK
                };
                break;
            default:
                grays = new Color[]{Color.BLACK};
                break;
        }
        return grays;
    }

    /**
     * Creates a new sweeping box animation.
     *
     * @param canvas the canvas on which to draw the animation
     * @param levels the number of gray levels to draw on alternating cycles
     */
    SweepAnimation(Canvas canvas, int levels) {
        graphics = canvas.getGraphicsContext2D();
        grays = getGrays(levels);
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
        boolean even = (count / boxesPerScreen) % 2 == 0;
        graphics.setFill(even ? grays[index] : Color.WHITE);
        graphics.fillRect(x, y, boxWidth, boxHeight);
        index = index < grays.length - 1 ? index + 1 : 0;
        count++;
    }
}
