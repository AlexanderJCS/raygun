package raytracer;

import static org.lwjgl.opengl.GL45.*;
import org.joml.Vector3f;


/**
 * Uses an SSBO to store the spheres in the scene.
 */
public class ObjectsBuffer {
    // as defined by the compute shader #define MAX_COUNT [max_count]
    private static final int MAX_OBJECT_COUNT = 100;

    private final int ssbo;
    private final int numObjects;

    public ObjectsBuffer(Vector3f[] centers, float[] radii, int[] materialIDs) {
        if (centers.length != radii.length || centers.length != materialIDs.length) {
            throw new IllegalArgumentException("centers, radii, and materialIDs must have the same length");
        }

        if (centers.length > MAX_OBJECT_COUNT) {
            throw new IllegalArgumentException("Too many objects, max is " + MAX_OBJECT_COUNT);
        }

        this.numObjects = centers.length;

        float[] centersFloat = new float[MAX_OBJECT_COUNT * 4];
        for (int i = 0; i < centers.length; i++) {
            centersFloat[i * 4] = centers[i].x;
            centersFloat[i * 4 + 1] = centers[i].y;
            centersFloat[i * 4 + 2] = centers[i].z;
            centersFloat[i * 4 + 3] = 0;  // requires a vec4 due to std430 layout
        }

        float[] radiiPadded = new float[MAX_OBJECT_COUNT];
        System.arraycopy(radii, 0, radiiPadded, 0, radii.length);

        int[] materialIDsPadded = new int[MAX_OBJECT_COUNT];
        System.arraycopy(materialIDs, 0, materialIDsPadded, 0, materialIDs.length);

        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, (long) (MAX_OBJECT_COUNT * 5) * Float.BYTES + (long) MAX_OBJECT_COUNT * Integer.BYTES, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, centersFloat);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) MAX_OBJECT_COUNT * 4 * Float.BYTES, radiiPadded);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) MAX_OBJECT_COUNT * 5 * Float.BYTES, materialIDsPadded);
    }

    public int numObjects() {
        return numObjects;
    }

    public void bind() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, ssbo);
    }

    public void unbind() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, 0);
    }

    public void cleanup() {
        glDeleteBuffers(ssbo);
    }
}
