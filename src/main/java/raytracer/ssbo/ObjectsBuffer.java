package raytracer.ssbo;

import static org.lwjgl.opengl.GL45.*;
import org.joml.Vector3f;
import org.joml.Vector3i;
import raytracer.ArrayUtil;


/**
 * Uses an SSBO to store the spheres in the scene.
 */
public class ObjectsBuffer {
    // as defined by the compute shader #define MAX_COUNT [max_count]
    private static final int MAX_TRIANGLES = 1000;
    private static final int MAX_OBJECT = 100;

    private final int ssbo;
    private final int numObjects;

    public ObjectsBuffer(Mesh[] meshes) {
        if (meshes.length > MAX_OBJECT) {
            throw new IllegalArgumentException("Too many objects, max is " + MAX_OBJECT);
        }

        this.numObjects = meshes.length;

        float[][] vertices = new float[MAX_OBJECT][MAX_TRIANGLES];
        int[][] indices = new int[MAX_OBJECT][MAX_TRIANGLES];
        int[] materialIDs = new int[MAX_OBJECT];

        for (int i = 0; i < meshes.length; i++) {
            Vector3f[] verticesVec = meshes[i].vertices();
            Vector3i indicesVec = meshes[i].indices();

            for (int j = 0; j < verticesVec.length; j++) {
                vertices[i][j * 3] = verticesVec[j].x;
                vertices[i][j * 3 + 1] = verticesVec[j].y;
                vertices[i][j * 3 + 2] = verticesVec[j].z;
            }

            for (int j = 0; j < indicesVec.length(); j++) {
                indices[i][j] = indicesVec.get(j);
            }

            materialIDs[i] = meshes[i].materialIndex();
        }

        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, (long) (4 * MAX_TRIANGLES * MAX_OBJECT) * Float.BYTES + (long) ((4 * MAX_TRIANGLES + 3) * MAX_OBJECT) * Integer.BYTES, GL_DYNAMIC_DRAW);

        int objectOffset = 4 * MAX_TRIANGLES * Float.BYTES + (4 * MAX_TRIANGLES + 3) * Integer.BYTES;

        for (int i = 0; i < MAX_OBJECT; i++) {
            // upload in the order of centers, radii, materialIDs as a densely packed array

            // all this commented out code is garbage
//            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * 4 * MAX_TRIANGLES * Float.BYTES, vertices[i]);
//            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 4 * MAX_TRIANGLES + 3) * Float.BYTES, new int[]{materialIDs[i]});
//            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (4 * MAX_TRIANGLES * MAX_OBJECT + i * 4 * MAX_TRIANGLES) * Integer.BYTES, indices[i]);
        }
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
