package raytracer.util;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * A utility class to handle different time-related tasks.
 */
public class Clock {
    /**
     * Second to last tick is used for getTimeDelta()
     */
    private double secondToLastTick;
    private double lastTick;
    private double[] fpsSamples;
    /**
     * The next index to modify in fpsSamples.
     */
    private int fpsSampleIndex;
    private int frameCounter;

    public Clock() {
        secondToLastTick = 0;
        lastTick = 0;
        fpsSamples = new double[100];
        fpsSampleIndex = 0;
        frameCounter = 0;
    }

    /**
     * Sets deltaTime to the correct value
     */
    private void updateDeltaTime() {
        secondToLastTick = lastTick;
        lastTick = getTime();
    }

    /**
     * Add a new value to smoothedFps. Should only be called after updateDeltaTime so the information is for this frame
     * and not the previous frame.
     */
    private void updateSmoothedFPS() {
        fpsSamples[fpsSampleIndex] = 1 / getTimeDelta();
        fpsSampleIndex++;

        if (fpsSampleIndex >= fpsSamples.length) {
            fpsSampleIndex = 0;
        }
    }

    /**
     * Updates the delta time and the smoothed FPS.
     */
    public void update() {
        frameCounter++;

        updateDeltaTime();
        updateSmoothedFPS();
    }

    /**
     * @return The time between the last frame and the second to last frame.
     */
    public double getTimeDelta() {
        return lastTick - secondToLastTick;
    }

    /**
     * @return Clock.getTimeDelta() cast to a float. Useful as to provide less-verbose code.
     */
    public float getTimeDeltaf() {
        return (float) getTimeDelta();
    }

    /**
     * @return The seconds since the window initialized
     */
    public double getTime() {
        return glfwGetTime();
    }

    /**
     * @return Clock.getTime() cast to a float. Useful as to provide less-verbose code.
     */
    public float getTimef() {
        return (float) getTime();
    }

    /**
     * @return The number of smoothed FPS samples
     */
    public int getNumFpsSamples() {
        return fpsSamples.length;
    }

    /**
     * @param n The number of samples to set the smoothed FPS to. Must be >= 1.
     */
    public void setNumFpsSamples(int n) throws IllegalArgumentException {
        if (n <= 0) {
            throw new IllegalArgumentException("Smoothed FPS sample size must be 1 or greater, not " + n + ".");
        }

        fpsSamples = new double[n];
    }

    /**
     * @return The smoothed fps over the last n samples
     */
    public double getSmoothedFps() {
        double sum = 0;
        int validSamples = 0;

        for (double sample : fpsSamples) {
            if (sample > 0) {  // Ignore invalid samples (including 0)
                sum += sample;
                validSamples++;
            }
        }

        return validSamples > 0 ? sum / validSamples : 0;
    }


    public double getUnsmoothedFps() {
        return 1 / getTimeDelta();
    }

    public int getFrameCount() {
        return frameCounter;
    }
}