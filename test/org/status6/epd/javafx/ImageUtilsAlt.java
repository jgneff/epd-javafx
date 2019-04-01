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
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.util.stream.IntStream;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * A class for testing alternatives to the method {@link ImageUtils#toFXImage}.
 *
 * @author John Neffenger
 */
class ImageUtilsAlt {

    /**
     * Prevents instances of this class from being created.
     */
    private ImageUtilsAlt() {
    }

    // Used only by the toFXImageArray method:
    private static int[] pixels;

    // Used only by the toFXImage and toFXImageByte methods:
    static BufferedImage converted;
    static Graphics2D graphics;
    static int width;
    static int height;
    static int imageType;

    /**
     * Converts the image one pixel at a time in nested <i>for loops</i>.
     *
     * @param awtImage the AWT {@code BufferedImage} to be converted
     * @param jfxImage an optional JavaFX {@code WritableImage} that can be used
     * to store the returned pixel data
     * @return a {@code WritableImage} object representing a snapshot of the
     * current pixels in the {@code BufferedImage}
     */
    static WritableImage toFXImageLoops(BufferedImage awtImage, WritableImage jfxImage) {
        int awtWidth = awtImage.getWidth();
        int awtHeight = awtImage.getHeight();
        if (jfxImage == null) {
            jfxImage = new WritableImage(awtWidth, awtHeight);
        }
        PixelWriter writer = jfxImage.getPixelWriter();
        for (int y = 0; y < awtHeight; y++) {
            for (int x = 0; x < awtWidth; x++) {
                writer.setArgb(x, y, awtImage.getRGB(x, y));
            }
        }
        return jfxImage;
    }

    /**
     * Converts the image using an intermediate array of 32-bit pixels instead
     * of converting directly from a {@code BufferedImage}.
     *
     * @param awtImage the AWT {@code BufferedImage} to be converted
     * @param jfxImage an optional JavaFX {@code WritableImage} that can be used
     * to store the returned pixel data
     * @return a {@code WritableImage} object representing a snapshot of the
     * current pixels in the {@code BufferedImage}
     */
    static WritableImage toFXImageArray(BufferedImage awtImage, WritableImage jfxImage) {
        int awtWidth = awtImage.getWidth();
        int awtHeight = awtImage.getHeight();
        if (pixels == null) {
            pixels = new int[awtWidth * awtHeight];
        }
        if (jfxImage == null) {
            jfxImage = new WritableImage(awtWidth, awtHeight);
        }
        PixelWriter writer = jfxImage.getPixelWriter();
        awtImage.getRGB(0, 0, awtWidth, awtHeight, pixels, 0, awtWidth);
        writer.setPixels(0, 0, awtWidth, awtHeight, PixelFormat.getIntArgbPreInstance(), pixels, 0, awtWidth);
        return jfxImage;
    }

    /**
     * Converts the image using an ordered (non-parallel) stream operation.
     *
     * @param awtImage the AWT {@code BufferedImage} to be converted
     * @param jfxImage an optional JavaFX {@code WritableImage} that can be used
     * to store the returned pixel data
     * @return a {@code WritableImage} object representing a snapshot of the
     * current pixels in the {@code BufferedImage}
     */
    static WritableImage toFXImageOrdered(BufferedImage awtImage, WritableImage jfxImage) {
        int awtWidth = awtImage.getWidth();
        int awtHeight = awtImage.getHeight();
        if (jfxImage == null) {
            jfxImage = new WritableImage(awtWidth, awtHeight);
        }
        PixelWriter writer = jfxImage.getPixelWriter();
        IntStream.range(0, awtWidth * awtHeight).forEachOrdered((i) -> {
            int x = i % awtWidth;
            int y = i / awtWidth;
            writer.setArgb(x, y, awtImage.getRGB(x, y));
        });
        return jfxImage;
    }

    /**
     * Converts the image using a parallel stream operation.
     *
     * @param awtImage the AWT {@code BufferedImage} to be converted
     * @param jfxImage an optional JavaFX {@code WritableImage} that can be used
     * to store the returned pixel data
     * @return a {@code WritableImage} object representing a snapshot of the
     * current pixels in the {@code BufferedImage}
     */
    static WritableImage toFXImageParallel(BufferedImage awtImage, WritableImage jfxImage) {
        int awtWidth = awtImage.getWidth();
        int awtHeight = awtImage.getHeight();
        if (jfxImage == null) {
            jfxImage = new WritableImage(awtWidth, awtHeight);
        }
        PixelWriter writer = jfxImage.getPixelWriter();
        IntStream.range(0, awtWidth * awtHeight).parallel().forEach((i) -> {
            int x = i % awtWidth;
            int y = i / awtWidth;
            writer.setArgb(x, y, awtImage.getRGB(x, y));
        });
        return jfxImage;
    }

    /**
     * Converts the image as in {@link ImageUtils#toFXImage} but allows for
     * testing different integer image types for the internal
     * {@code BufferedImage}.
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
                    converted = new BufferedImage(awtWidth, awtHeight, imageType);
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

    /**
     * Converts the image as in {@link ImageUtils#toFXImage} but allows for
     * testing different four-byte image types for the internal
     * {@code BufferedImage}.
     *
     * @param awtImage the AWT {@code BufferedImage} to be converted
     * @param jfxImage an optional JavaFX {@code WritableImage} that can be used
     * to store the returned pixel data
     * @return a {@code WritableImage} object representing a snapshot of the
     * current pixels in the {@code BufferedImage}
     */
    static WritableImage toFXImageByte(BufferedImage awtImage, WritableImage jfxImage) {
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
                    converted = new BufferedImage(awtWidth, awtHeight, imageType);
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
        var format = PixelFormat.getByteBgraPreInstance();
        var raster = awtImage.getRaster();
        var buffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = buffer.getData();
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
