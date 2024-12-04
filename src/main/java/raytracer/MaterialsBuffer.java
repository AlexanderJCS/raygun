package raytracer;

import static org.lwjgl.opengl.GL43.*;
import org.joml.Vector3f;


public class MaterialsBuffer {
    private final int ssbo;

    public MaterialsBuffer(Vector3f[] albedos) {
        float[] albedosFloat = new float[albedos.length * 3];
        for (int i = 0; i < albedos.length; i++) {
            albedosFloat[i * 3] = albedos[i].x;
            albedosFloat[i * 3 + 1] = albedos[i].y;
            albedosFloat[i * 3 + 2] = albedos[i].z;
        }

        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, albedosFloat, GL_DYNAMIC_DRAW);
    }

    public void bind() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
    }

    public void unbind() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
    }

    public void cleanup() {
        glDeleteBuffers(ssbo);
    }
}
