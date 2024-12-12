package raytracer.ssbo;

import static org.lwjgl.opengl.GL45.*;
import org.joml.Vector3f;
import org.joml.Vector3i;


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
            Vector3i[] indicesVec = meshes[i].indices();

            for (int j = 0; j < verticesVec.length; j++) {
                vertices[i][j * 4] = verticesVec[j].x;
                vertices[i][j * 4 + 1] = verticesVec[j].y;
                vertices[i][j * 4 + 2] = verticesVec[j].z;
            }

            for (int j = 0; j < indicesVec.length; j++) {
                indices[i][j * 4] = indicesVec[j].x;
                indices[i][j * 4 + 1] = indicesVec[j].y;
                indices[i][j * 4 + 2] = indicesVec[j].z;
            }

            materialIDs[i] = meshes[i].materialIndex();
        }

        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, (long) (4 * MAX_TRIANGLES * MAX_OBJECT) * Float.BYTES + (long) ((4 * MAX_TRIANGLES + 3) * MAX_OBJECT) * Integer.BYTES, GL_DYNAMIC_DRAW);

        int stride = 4 * MAX_TRIANGLES * Float.BYTES + (4 * MAX_TRIANGLES + 3) * Integer.BYTES + Integer.BYTES;

        for (int i = 0; i < meshes.length; i++) {
            // upload in the order of centers, radii, materialIDs as a densely packed array
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride, vertices[i]);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 4 * MAX_TRIANGLES * Float.BYTES, indices[i]);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 4 * MAX_TRIANGLES * Float.BYTES + (4 * MAX_TRIANGLES) * Integer.BYTES, new int[]{meshes[i].vertices().length});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 4 * MAX_TRIANGLES * Float.BYTES + (4 * MAX_TRIANGLES + 1) * Integer.BYTES, new int[]{meshes[i].indices().length});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 4 * MAX_TRIANGLES * Float.BYTES + (4 * MAX_TRIANGLES + 2) * Integer.BYTES, new int[]{materialIDs[i]});
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
