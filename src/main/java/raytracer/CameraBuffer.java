package raytracer;

import raytracer.config.Camera;

import java.util.Arrays;

import static org.lwjgl.opengl.GL45.*;

public class CameraBuffer {
    private static final int BINDING_POINT = 5;
    private final int id;

    public CameraBuffer(Camera config) {
        id = glGenBuffers();

        this.bind();
        // 15 = 4 (origin + 1 pad) + 4 (lookAt + 1 pad) + 4 (up + 1 pad) + 1 (fov) + 1 (focusDist) + 1 (defocusAngle) - 1 (for god knows what reason)
        glBufferData(GL_UNIFORM_BUFFER, 15 * Float.BYTES, GL_STATIC_DRAW);

        System.out.println(Arrays.toString(config.origin()));

        glBufferSubData(GL_UNIFORM_BUFFER, 0, config.origin());
        glBufferSubData(GL_UNIFORM_BUFFER, 4 * Float.BYTES, config.lookAt());
        glBufferSubData(GL_UNIFORM_BUFFER, 8 * Float.BYTES, config.up());
        glBufferSubData(GL_UNIFORM_BUFFER, 11 * Float.BYTES, new float[]{config.fov()});  // no idea why this is 11 * and not 12 * but it works
        glBufferSubData(GL_UNIFORM_BUFFER, 12 * Float.BYTES, new float[]{config.focusDist()});
        glBufferSubData(GL_UNIFORM_BUFFER, 13 * Float.BYTES, new float[]{config.defocusAngle()});

        this.unbind();
    }

    public void bind() {
        glBindBufferBase(GL_UNIFORM_BUFFER, BINDING_POINT, id);
    }

    public void unbind() {
        glBindBufferBase(GL_UNIFORM_BUFFER, BINDING_POINT, 0);
    }

    public void cleanup() {
        glDeleteBuffers(id);
    }
}
