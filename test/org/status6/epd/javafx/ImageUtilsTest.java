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
import java.util.function.Consumer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the performance of various methods to convert an AWT
 * {@code BufferedImage} to a JavaFX {@code WriteableImage}.
 *
 * @author John Neffenger
 */
public class ImageUtilsTest {

    private static final String FILENAME = "doll-dancing.gif";
    private static final String FORMAT_NAME = "gif";
    private static final int ITERATIONS = 1;

    private static final String JAVA_VERSION = "java.version";
    private static final String JAVA_VERSION_DATE = "java.version.date";
    private static final String JAVA_RUNTIME_NAME = "java.runtime.name";
    private static final String JAVA_RUNTIME_VERSION = "java.runtime.version";
    private static final String JAVA_VM_NAME = "java.vm.name";
    private static final String JAVA_VM_VERSION = "java.vm.version";
    private static final String JAVA_VM_INFO = "java.vm.info";
    private static final String OS_NAME = "os.name";
    private static final String OS_VERSION = "os.version";
    private static final String OS_ARCH = "os.arch";

    private static ArrayList<BufferedImage> frames;
    private static WritableImage jfxImage;

    /**
     * Creates a new object for testing {@link ImageUtils}.
     */
    public ImageUtilsTest() {
    }

    /**
     * Loads all of the animation frames in the GIF image file.
     *
     * @throws IOException if an error occurs reading the file
     * @throws IllegalArgumentException if the file is not a GIF image
     */
    @BeforeClass
    public static void onlyOnce() throws IOException {
        frames = new ArrayList<>();
        try (var input = ImageAnimation.class.getResourceAsStream("/" + FILENAME)) {
            if (input == null) {
                throw new IOException(String.format(
                        "Failed loading image file: %s", FILENAME));
            }
            try (var stream = ImageIO.createImageInputStream(input)) {
                ImageReader reader = ImageIO.getImageReadersByFormatName(FORMAT_NAME).next();
                reader.setInput(stream);
                int count = reader.getNumImages(true);
                if (count == 0) {
                    throw new IllegalArgumentException(String.format(
                            "Failed reading GIF image: %s", FILENAME));
                }
                for (int i = 0; i < count; i++) {
                    frames.add(reader.read(i));
                }
            }
        }
        BufferedImage first = frames.get(0);
        int width = first.getWidth();
        int height = first.getHeight();
        jfxImage = new WritableImage(width, height);

        String javaVersion = System.getProperty(JAVA_VERSION);
        String javaVersionDate = System.getProperty(JAVA_VERSION_DATE);
        String javaRuntimeName = System.getProperty(JAVA_RUNTIME_NAME);
        String javaRuntimeVersion = System.getProperty(JAVA_RUNTIME_VERSION);
        String javaVMName = System.getProperty(JAVA_VM_NAME);
        String javaVMVersion = System.getProperty(JAVA_VM_VERSION);
        String javaVMInfo = System.getProperty(JAVA_VM_INFO);
        String osName = System.getProperty(OS_NAME);
        String osVersion = System.getProperty(OS_VERSION);
        String osArch = System.getProperty(OS_ARCH);
        System.out.println(String.format("openjdk version \"%s\" %s", javaVersion, javaVersionDate));
        System.out.println(String.format("%s (build %s)", javaRuntimeName, javaRuntimeVersion));
        System.out.println(String.format("%s (build %s, %s)", javaVMName, javaVMVersion, javaVMInfo));
        System.out.println(String.format("%s version %s (%s)", osName, osVersion, osArch));
        System.out.println(String.format("%d × %d frames of %d × %d px", ITERATIONS, frames.size(), width, height));
    }

    private void print(String name, long duration) {
        int total = frames.size() * ITERATIONS;
        System.out.println(String.format("%,9d ms (%6.2f ms/frame) %s",
                duration, (double) duration / total, name));
    }

    private void test(Consumer<? super BufferedImage> action, String method) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) {
            frames.forEach(action);
        }
        long end = System.currentTimeMillis();
        print(method, end - start);
    }

    private void initAltToFXImage(int imageType) {
        if (ImageUtilsAlt.converted != null) {
            ImageUtilsAlt.converted = null;
            ImageUtilsAlt.graphics.dispose();
            ImageUtilsAlt.width = 0;
            ImageUtilsAlt.height = 0;
        }
        ImageUtilsAlt.imageType = imageType;
    }

    /**
     * Tests the {@link SwingFXUtils#toFXImage} method.
     */
    @Test
    public void testToFXImageOriginal() {
        test((awtImage) -> SwingFXUtils.toFXImage(awtImage, jfxImage), "SwingFXUtils.toFXImage");
    }

    /**
     * Tests the {@link ImageUtils#toFXImage} method.
     */
    @Test
    public void testToFXImageModified() {
        test((awtImage) -> ImageUtils.toFXImage(awtImage, jfxImage), "ImageUtils.toFXImage");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImageLoops} method.
     */
    @Test
    public void testToFXImageLoops() {
        test((awtImage) -> ImageUtilsAlt.toFXImageLoops(awtImage, jfxImage), "ImageUtilsAlt.toFXImageLoops");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImageArray} method.
     */
    @Test
    public void testToFXImageArray() {
        test((awtImage) -> ImageUtilsAlt.toFXImageArray(awtImage, jfxImage), "ImageUtilsAlt.toFXImageArray");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImageOrdered} method.
     */
    @Test
    public void testToFXImageOrdered() {
        test((awtImage) -> ImageUtilsAlt.toFXImageOrdered(awtImage, jfxImage), "ImageUtilsAlt.toFXImageOrdered");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImageParallel} method.
     */
    @Test
    public void testToFXImageParallel() {
        test((awtImage) -> ImageUtilsAlt.toFXImageParallel(awtImage, jfxImage), "ImageUtilsAlt.toFXImageParallel");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImage} method with image type
     * {@link BufferedImage#TYPE_INT_RGB}.
     */
    @Test
    public void testToFXImageIntRgb() {
        initAltToFXImage(BufferedImage.TYPE_INT_RGB);
        test((awtImage) -> ImageUtilsAlt.toFXImage(awtImage, jfxImage), "ImageUtilsAlt.toFXImage (TYPE_INT_RGB)");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImage} method with image type
     * {@link BufferedImage#TYPE_INT_ARGB}.
     */
    @Test
    public void testToFXImageIntArgb() {
        initAltToFXImage(BufferedImage.TYPE_INT_ARGB);
        test((awtImage) -> ImageUtilsAlt.toFXImage(awtImage, jfxImage), "ImageUtilsAlt.toFXImage (TYPE_INT_ARGB)");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImage} method with image type
     * {@link BufferedImage#TYPE_INT_ARGB_PRE}.
     */
    @Test
    public void testToFXImageIntArgbPre() {
        initAltToFXImage(BufferedImage.TYPE_INT_ARGB_PRE);
        test((awtImage) -> ImageUtilsAlt.toFXImage(awtImage, jfxImage), "ImageUtilsAlt.toFXImage (TYPE_INT_ARGB_PRE)");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImage} method with image type
     * {@link BufferedImage#TYPE_INT_BGR}.
     */
    @Test
    public void testToFXImageIntBgr() {
        initAltToFXImage(BufferedImage.TYPE_INT_BGR);
        test((awtImage) -> ImageUtilsAlt.toFXImage(awtImage, jfxImage), "ImageUtilsAlt.toFXImage (TYPE_INT_BGR)");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImageByte} method with image type
     * {@link BufferedImage#TYPE_4BYTE_ABGR}.
     */
    @Test
    public void testToFXImageByteAbgr() {
        initAltToFXImage(BufferedImage.TYPE_4BYTE_ABGR);
        test((awtImage) -> ImageUtilsAlt.toFXImageByte(awtImage, jfxImage), "ImageUtilsAlt.toFXImageByte (TYPE_4BYTE_ABGR)");
    }

    /**
     * Tests the {@link ImageUtilsAlt#toFXImageByte} method with image type
     * {@link BufferedImage#TYPE_4BYTE_ABGR_PRE}.
     */
    @Test
    public void testToFXImageByteAbgrPre() {
        initAltToFXImage(BufferedImage.TYPE_4BYTE_ABGR_PRE);
        test((awtImage) -> ImageUtilsAlt.toFXImageByte(awtImage, jfxImage), "ImageUtilsAlt.toFXImageByte (TYPE_4BYTE_ABGR_PRE)");
    }
}
