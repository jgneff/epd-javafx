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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

/**
 * An animation timer that cycles through the sequence of frames in an animated
 * GIF image file.
 *
 * @author John Neffenger
 */
class ImageAnimation extends FiniteAnimation {

    private static final String FORMAT_NAME = "gif";

    private final ArrayList<BufferedImage> awtList;
    private final ImageView view;
    private final boolean patrol;
    private final int frames;
    private final WritableImage jfxImage;

    private boolean reverse;
    private int index;

    /**
     * Gets a list of all animation frames in the GIF image.
     *
     * @param filename the name of the image file
     * @return the list of animation frames in the image
     * @throws IOException if an error occurs reading the image file
     * @throws IllegalArgumentException if the image format is not GIF
     */
    private static ArrayList<BufferedImage> getFrames(String filename) throws IOException {
        ArrayList<BufferedImage> list = new ArrayList<>();
        try (var input = ImageAnimation.class.getResourceAsStream("/" + filename)) {
            if (input == null) {
                throw new IOException(String.format(
                        "Failed loading image file: %s", filename));
            }
            try (var stream = ImageIO.createImageInputStream(input)) {
                ImageReader reader = ImageIO.getImageReadersByFormatName(FORMAT_NAME).next();
                reader.setInput(stream);
                int count = reader.getNumImages(true);
                if (count == 0) {
                    throw new IllegalArgumentException(String.format(
                            "Failed reading GIF image: %s", filename));
                }
                for (int i = 0; i < count; i++) {
                    list.add(reader.read(i));
                }
            }
        }
        return list;
    }

    /**
     * Creates a new image animation.
     *
     * @param view the node for painting each animation frame
     * @param filename the name of the image file
     * @param patrol {@code true} to cycle back and forth between the first and
     * last frames of the animation, called a <i>patrol cycle</i>; otherwise
     * {@code false} to loop back to the first frame after the last
     * @throws IOException if an error occurs reading the image file
     */
    ImageAnimation(ImageView view, String filename, boolean patrol) throws IOException {
        awtList = getFrames(filename);
        this.view = view;
        this.patrol = patrol;
        frames = awtList.size();
        BufferedImage first = awtList.get(0);
        jfxImage = new WritableImage(first.getWidth(), first.getHeight());
    }

    /**
     * Gets the index for the next frame when in a patrol cycle.
     *
     * @return the index for the next frame
     */
    private int nextPatrolCycle() {
        int next = reverse ? index - 1 : index + 1;
        if (next < 0) {
            next = 1;
            reverse = false;
        } else if (next == frames) {
            next = frames - 2;
            reverse = true;
        }
        return next;
    }

    /**
     * Gets the index for the next frame when in a loop cycle.
     *
     * @return the index for the next frame
     */
    private int nextLoopCycle() {
        int next = index + 1;
        if (next == frames) {
            next = 0;
        }
        return next;
    }

    @Override
    int getNumFrames() {
        return awtList.size();
    }

    @Override
    public void handle(long now) {
        view.setImage(ImageUtils.toFXImage(awtList.get(index), jfxImage));
        index = patrol ? nextPatrolCycle() : nextLoopCycle();
    }
}
