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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

/**
 * Utility method for converting an AWT {@link BufferedImage} to a JavaFX
 * {@link WritableImage}. This class contains a modified version of the
 * {@code toFXImage} method in {@link javafx.embed.swing.SwingFXUtils}.
 *
 * @author John Neffenger
 */
class ImageUtils {

    /**
     * Prevents instances of this class from being created.
     */
    private ImageUtils() {
    }

    private static BufferedImage converted;
    private static Graphics2D graphics;
    private static int width;
    private static int height;

    /**
     * Snapshots the specified AWT {@link BufferedImage} and stores a copy of
     * its pixels into a JavaFX {@link WritableImage} object, creating a new
     * object if needed. The returned {@code WritableImage} is a static snapshot
     * of the state of the pixels in the {@code BufferedImage} at the time the
     * method completes. Further changes to the {@code BufferedImage} will not
     * be reflected in the {@code WritableImage}.
     * <p>
     * The optional JavaFX {@code WritableImage} parameter may be reused to
     * store the copy of the pixels. A new {@code WritableImage} object will be
     * created if the supplied object is {@code null} or is too small for the
     * specified {@code BufferedImage}.</p>
     *
     * @implNote
     * <p>
     * This method is the same as the original {@code toFXImage} method in
     * {@code javafx.embed.swing.SwingFXUtils} except for the following changes
     * made to increase its performance.</p>
     * <p>
     * This method reuses an internal {@code BufferedImage} object instead of
     * always creating a new one when the source AWT image type is not one of
     * {@link BufferedImage#TYPE_INT_ARGB TYPE_INT_ARGB} or
     * {@link BufferedImage#TYPE_INT_ARGB_PRE TYPE_INT_ARGB_PRE}.</p>
     * <p>
     * This method always uses a pre-multiplied pixel format when copying into
     * the JavaFX {@code WriteableImage}, even when the source AWT
     * {@code BufferedImage} is not pre-multiplied with alpha.</p>
     * <p>
     * Therefore, this method is <strong>not thread safe</strong> and works best
     * for images <strong>with no alpha</strong>.</p>
     *
     * @param awtImage the AWT {@code BufferedImage} to be converted
     * @param jfxImage an optional JavaFX {@code WritableImage} that can be used
     * to store the returned pixel data
     * @return a {@code WritableImage} object representing a snapshot of the
     * current pixels in the {@code BufferedImage}
     */
    static WritableImage toFXImage(BufferedImage awtImage, WritableImage jfxImage) {
        int awtWidth = awtImage.getWidth();
        int awtHeight = awtImage.getHeight();
        switch (awtImage.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                break;
            default:
                if (width != awtWidth || height != awtHeight) {
                    if (converted != null) {
                        System.err.println(String.format(
                                "Warning: frame size changed (%d × %d to %d × %d)",
                                width, height, awtWidth, awtHeight));
                        graphics.dispose();
                    }
                    converted = new BufferedImage(awtWidth, awtHeight, BufferedImage.TYPE_INT_ARGB);
                    graphics = converted.createGraphics();
                    width = awtWidth;
                    height = awtHeight;
                }
                graphics.drawImage(awtImage, 0, 0, null);
                awtImage = converted;
                break;

        }
        if (jfxImage != null) {
            int jfxWidth = (int) jfxImage.getWidth();
            int jfxHeight = (int) jfxImage.getHeight();
            if (jfxWidth < awtWidth || jfxHeight < awtHeight) {
                jfxImage = null;
            } else if (awtWidth < jfxWidth || awtHeight < jfxHeight) {
                int[] empty = new int[jfxWidth];
                var writer = jfxImage.getPixelWriter();
                var format = PixelFormat.getIntArgbPreInstance();
                if (awtWidth < jfxWidth) {
                    writer.setPixels(awtWidth, 0, jfxWidth - awtWidth, awtHeight, format, empty, 0, 0);
                }
                if (awtHeight < jfxHeight) {
                    writer.setPixels(0, awtHeight, jfxWidth, jfxHeight - awtHeight, format, empty, 0, 0);
                }
            }
        }
        if (jfxImage == null) {
            jfxImage = new WritableImage(awtWidth, awtHeight);
        }
        var writer = jfxImage.getPixelWriter();
        var format = PixelFormat.getIntArgbPreInstance();
        var raster = awtImage.getRaster();
        var buffer = (DataBufferInt) raster.getDataBuffer();
        int[] data = buffer.getData();
        int offset = buffer.getOffset();
        int scan = 0;
        var model = raster.getSampleModel();
        if (model instanceof SinglePixelPackedSampleModel) {
            scan = ((SinglePixelPackedSampleModel) model).getScanlineStride();
        }
        writer.setPixels(0, 0, awtWidth, awtHeight, format, data, offset, scan);
        return jfxImage;
    }
}
