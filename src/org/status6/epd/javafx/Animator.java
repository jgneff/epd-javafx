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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * A JavaFX application for testing the Monocle EPD platform.
 *
 * @author John Neffenger
 */
public class Animator extends Application {

    private static final String STAGE_TITLE = "EPD JavaFX Animator";
    private static final long TOUCH_TIME_MS = 250;
    private static final double TOUCH_RADIUS_PX = 20.0;
    private static final int IMAGE_LOGGING_MINIMUM = 80;

    private static final String WIDTH_KEY = "width";
    private static final String HEIGHT_KEY = "height";
    private static final String PATTERN_KEY = "pattern";
    private static final String LEVELS_KEY = "levels";
    private static final String LOOPS_KEY = "loops";
    private static final String IMAGE_KEY = "image";
    private static final String PATROL_KEY = "patrol";

    private static final int PATTERN_MOVE = 1;
    private static final int PATTERN_SWEEP = 2;
    private static final int PATTERN_IMAGE = 3;
    private static final int[] PATTERNS_SUPPORTED = {PATTERN_MOVE, PATTERN_SWEEP, PATTERN_IMAGE};
    private static final int[] LEVELS_SUPPORTED = {1, 2, 4, 16};

    private static final int PATTERN_DEFAULT = PATTERN_MOVE;
    private static final int LEVELS_DEFAULT = 1;
    private static final int LOOPS_DEFAULT = 2;
    private static final String IMAGE_DEFAULT = "duke-waving.gif";

    private static final int WIDTH_MINIMUM = 400;
    private static final int HEIGHT_MINIMUM = 300;
    private static final int LOOPS_MINIMUM = 0;

    private int width;
    private int height;
    private int pattern;
    private int levels;
    private int loops;
    private String image;
    private boolean patrol;

    private final List<AnimationTimer> timers;
    private boolean isRunning;
    private Parent root;

    private Point2D oldPoint;
    private long oldTime;

    /**
     * Gets the integer value to which the specified key is mapped, or the
     * provided default value if there is no mapping for the key.
     *
     * @param map the mapping of keys to values
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if {@code map} contains no mapping for the key
     * @throws IllegalArgumentException if the value for the key is not a number
     */
    private static int getInteger(Map<String, String> map, String key, int defaultValue) {
        String string = map.getOrDefault(key, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                    "Value of %s=%s is not a number", key, string), e);
        }
    }

    /**
     * Gets the integer value to which the specified key is mapped, or the
     * provided default value if there is no mapping for the key, while checking
     * that the value is in the list of permitted values.
     *
     * @param map the mapping of keys to values
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @param list the list of permitted values
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if {@code map} contains no mapping for the key
     * @throws IllegalArgumentException if the value for the key is not in the
     * list of permitted values
     */
    private static int getInteger(Map<String, String> map, String key, int defaultValue, int... list) {
        int value = getInteger(map, key, defaultValue);
        boolean found = false;
        for (int i = 0; i < list.length && !found; i++) {
            found = value == list[i];
        }
        if (!found) {
            throw new IllegalArgumentException(String.format(
                    "Value of %s=%d not in %s", key, value, Arrays.toString(list)));
        }
        return value;
    }

    /**
     * Gets the integer value to which the specified key is mapped, or the
     * provided default value if there is no mapping for the key, while checking
     * that the value is at least the specified minimum.
     *
     * @param map the mapping of keys to values
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @param minimum the minimum permitted value
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if {@code map} contains no mapping for the key
     * @throws IllegalArgumentException if the value for the key is less than
     * the minimum
     */
    private static int getInteger(Map<String, String> map, String key, int defaultValue, int minimum) {
        int value = getInteger(map, key, defaultValue);
        if (value < minimum) {
            throw new IllegalArgumentException(String.format(
                    "Value of %s=%d must be %d or greater", key, value, minimum));
        }
        return value;
    }

    /**
     * Creates a new EPD JavaFX animator.
     */
    public Animator() {
        timers = new ArrayList<>();
        oldPoint = new Point2D(0, 0);
    }

    /**
     * Toggles the animation timers between their started and stopped states.
     */
    private void toggleTimers() {
        if (isRunning == true) {
            timers.forEach(AnimationTimer::stop);
            isRunning = false;
        } else {
            timers.forEach(AnimationTimer::start);
            isRunning = true;
        }
    }

    /**
     * Processes the {@link TouchEvent#TOUCH_PRESSED TOUCH_PRESSED} event. This
     * method prints a message and toggles the animation timers if the touch
     * falls outside either the time or distance tolerance.
     *
     * @param event the input touch event
     */
    private void onTouchPressed(TouchEvent event) {
        long now = System.currentTimeMillis();
        TouchPoint touch = event.getTouchPoint();
        Point2D point = new Point2D(touch.getX(), touch.getY());
        double distance = point.distance(oldPoint);
        long duration = now - oldTime;
        if (distance > TOUCH_RADIUS_PX || duration > TOUCH_TIME_MS) {
            event.consume();
            System.out.println(String.format(
                    "Touch pressed: position = (%.0f, %.0f)", touch.getX(), touch.getY()));
            toggleTimers();
        }
        oldPoint = point;
        oldTime = now;
    }

    /**
     * Processes the {@link MouseEvent#MOUSE_PRESSED MOUSE_PRESSED} event. This
     * method prints a message and toggles the animation timers if the mouse
     * pressed event is not synthesized from a touch event.
     *
     * @param event the input mouse event
     */
    private void onMousePressed(MouseEvent event) {
        if (!event.isSynthesized()) {
            event.consume();
            System.out.println(String.format(
                    "Mouse pressed: position = (%.0f, %.0f)", event.getX(), event.getY()));
            toggleTimers();
        }
    }

    /**
     * Processes the {@link KeyEvent#KEY_PRESSED KEY_PRESSED} event. This method
     * prints a message for each key pressed and terminates the application if
     * the key is Q or ESCAPE.
     *
     * @param event the input key event
     */
    private void onKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        System.out.println(String.format(
                "Key pressed: code = 0x%x (%s)", code.getCode(), code));
        if (code == KeyCode.Q || code == KeyCode.ESCAPE) {
            event.consume();
            Platform.exit();
        }
    }

    @Override
    public void init() {
        try {
            Map<String, String> map = getParameters().getNamed();
            int defaultWidth = (int) Screen.getPrimary().getVisualBounds().getWidth();
            int defaultHeight = (int) Screen.getPrimary().getVisualBounds().getHeight();
            width = getInteger(map, WIDTH_KEY, defaultWidth, WIDTH_MINIMUM);
            height = getInteger(map, HEIGHT_KEY, defaultHeight, HEIGHT_MINIMUM);
            pattern = getInteger(map, PATTERN_KEY, PATTERN_DEFAULT, PATTERNS_SUPPORTED);
            levels = getInteger(map, LEVELS_KEY, LEVELS_DEFAULT, LEVELS_SUPPORTED);
            loops = getInteger(map, LOOPS_KEY, LOOPS_DEFAULT, LOOPS_MINIMUM);
            image = map.getOrDefault(IMAGE_KEY, IMAGE_DEFAULT);
            patrol = Boolean.valueOf(map.get(PATROL_KEY));

            FiniteAnimation animation = null;
            int numFrames = 0;
            int logFrames = 0;
            switch (pattern) {
                case PATTERN_MOVE:
                    Canvas canvas = new Canvas(width, height);
                    root = new Group(canvas);
                    animation = new MoveAnimation(canvas);
                    numFrames = animation.getNumFrames();
                    logFrames = numFrames;
                    break;
                case PATTERN_SWEEP:
                    canvas = new Canvas(width, height);
                    root = new Group(canvas);
                    animation = new SweepAnimation(canvas, levels);
                    numFrames = animation.getNumFrames();
                    logFrames = numFrames;
                    break;
                case PATTERN_IMAGE:
                    ImageView view = new ImageView();
                    root = new StackPane(view);
                    animation = new ImageAnimation(view, image, patrol);
                    numFrames = animation.getNumFrames();
                    logFrames = Math.max(numFrames, IMAGE_LOGGING_MINIMUM);
                    break;
                default:
                    throw new IllegalArgumentException(String.format(
                            "Unsupported pattern: %d", pattern));
            }
            timers.add(animation);
            timers.add(new RateTimer(logFrames));
            timers.add(new ExitTimer(numFrames * loops));
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e);
            Platform.exit();
        }
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(root, width, height);
        scene.addEventHandler(TouchEvent.TOUCH_PRESSED, this::onTouchPressed);
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        stage.setTitle(STAGE_TITLE);
        stage.setScene(scene);
        stage.show();
        timers.forEach(AnimationTimer::start);
        isRunning = true;
    }

    @Override
    public void stop() {
        timers.forEach(AnimationTimer::stop);
        isRunning = false;
    }

    /**
     * Launches this JavaFX application.
     *
     * @param args the command line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
