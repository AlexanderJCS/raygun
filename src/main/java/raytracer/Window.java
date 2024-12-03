package raytracer;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL43.*;

public class Window {
    private Window() {}

    private static long window = -1;

    public static void init(int width, int height) {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        if (window != -1) {
            throw new IllegalStateException("Window already initialized");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(width, height, "Ray Tracer", 0, 0);
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSetWindowAttrib(window, GLFW_RESIZABLE, GLFW_FALSE);
    }

    public static boolean shouldRun() {
        return !glfwWindowShouldClose(window);
    }

    public static void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void update() {
        glfwPollEvents();
        glfwSwapBuffers(window);
    }

    public static void close() {
        glfwTerminate();
    }
}
