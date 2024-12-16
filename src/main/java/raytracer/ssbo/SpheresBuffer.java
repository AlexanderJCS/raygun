package raytracer.ssbo;

import static org.lwjgl.opengl.GL45.*;
import org.joml.Vector3f;
import raytracer.ArrayUtil;


/**
 * Uses an SSBO to store the spheres in the scene.
 */
public class SpheresBuffer extends ShaderStorageBuffer {
    // as defined by the compute shader #define MAX_COUNT [max_count]
    private static final int MAX_OBJECT = 100;
    private final int numObjects;

    public SpheresBuffer(Vector3f[] centers, float[] radii, int[] materialIDs) {
        super(glGenBuffers(), 2);

        if (centers.length != radii.length || centers.length != materialIDs.length) {
            cleanup();
            throw new IllegalArgumentException("centers, radii, and materialIDs must have the same length");
        }

        if (centers.length > MAX_OBJECT) {
            cleanup();
            throw new IllegalArgumentException("Too many objects, max is " + MAX_OBJECT);
        }

        this.numObjects = centers.length;

        float[] centersFloat = ArrayUtil.toVec3FloatArray(centers);
        float[] centersPadded = new float[MAX_OBJECT * 3];
        System.arraycopy(centersFloat, 0, centersPadded, 0, centersFloat.length);

        float[] radiiPadded = new float[MAX_OBJECT];
        System.arraycopy(radii, 0, radiiPadded, 0, radii.length);

        int[] materialIDsPadded = new int[MAX_OBJECT];
        System.arraycopy(materialIDs, 0, materialIDsPadded, 0, materialIDs.length);

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id());
        glBufferData(GL_SHADER_STORAGE_BUFFER, (long) (MAX_OBJECT * 5) * Float.BYTES + (long) MAX_OBJECT * Integer.BYTES, GL_DYNAMIC_DRAW);

        for (int i = 0; i < MAX_OBJECT; i++) {
            // upload in the order of centers, radii, materialIDs as a densely packed array
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * 8 * Float.BYTES, new float[]{centersPadded[i * 3], centersPadded[i * 3 + 1], centersPadded[i * 3 + 2]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 8 + 3) * Float.BYTES, new float[]{radiiPadded[i]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 8 + 4) * Float.BYTES, new int[]{materialIDsPadded[i]});
        }
    }

    public int numSpheres() {
        return numObjects;
    }
}