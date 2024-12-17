package raytracer.buffers;

import static org.lwjgl.opengl.GL45.*;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import raytracer.util.ArrayUtil;
import raytracer.Mesh;

import java.util.Arrays;


/**
 * Uses an SSBO to store the spheres in the scene.
 */
public class ObjectsBuffer extends ShaderStorageBuffer {
    // as defined by the compute shader #define MAX_COUNT [max_count]
    private static final int MAX_TRIANGLES = 2000;
    private static final int MAX_OBJECT = 50;

    private final int numObjects;

    public ObjectsBuffer(Mesh[] meshes) {
        super(glGenBuffers(), 1);

        if (meshes.length > MAX_OBJECT) {
            cleanup();
            throw new IllegalArgumentException("Too many objects, max is " + MAX_OBJECT);
        }

        this.numObjects = meshes.length;

        float[][] vertices = new float[MAX_OBJECT][MAX_TRIANGLES * 4];
        int[][] indices = new int[MAX_OBJECT][MAX_TRIANGLES * 4];
        float[][] texCoords = new float[MAX_OBJECT][MAX_TRIANGLES * 4];
        int[][] texIndices = new int[MAX_OBJECT][MAX_TRIANGLES * 4];
        int[] materialIDs = new int[MAX_OBJECT];

        for (int i = 0; i < meshes.length; i++) {
            Vector3f[] verticesVec = meshes[i].vertices();
            Vector3i[] indicesVec = meshes[i].indices();
            Vector2f[] texCoordsVec = meshes[i].texCoords();
            Vector3i[] texIndicesVec = meshes[i].texIndices();

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

            for (int j = 0; j < texCoordsVec.length; j++) {
                texCoords[i][j * 4] = texCoordsVec[j].x;
                texCoords[i][j * 4 + 1] = texCoordsVec[j].y;
            }

            for (int j = 0; j < texIndicesVec.length; j++) {
                texIndices[i][j * 4] = texIndicesVec[j].x;
                texIndices[i][j * 4 + 1] = texIndicesVec[j].y;
                texIndices[i][j * 4 + 2] = texIndicesVec[j].z;
            }

            materialIDs[i] = meshes[i].materialIndex();
        }

        int stride = (6 * MAX_TRIANGLES + 4 + 4) * Float.BYTES + (8 * MAX_TRIANGLES + 3) * Integer.BYTES + Integer.BYTES;

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id());
        glBufferData(GL_SHADER_STORAGE_BUFFER, (long) stride * MAX_OBJECT * Integer.BYTES, GL_DYNAMIC_DRAW);


        for (int i = 0; i < meshes.length; i++) {
            // upload in the order of centers, radii, materialIDs as a densely packed array
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride, vertices[i]);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 4 * MAX_TRIANGLES * Float.BYTES, indices[i]);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 4 * MAX_TRIANGLES * Float.BYTES + (4 * MAX_TRIANGLES) * Integer.BYTES, texCoords[i]);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 6 * MAX_TRIANGLES * Float.BYTES + (4 * MAX_TRIANGLES) * Integer.BYTES, texIndices[i]);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 6 * MAX_TRIANGLES * Float.BYTES + (8 * MAX_TRIANGLES + 1) * Integer.BYTES, new int[]{meshes[i].indices().length});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 6 * MAX_TRIANGLES * Float.BYTES + (8 * MAX_TRIANGLES + 2) * Integer.BYTES, new int[]{materialIDs[i]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + 6 * MAX_TRIANGLES * Float.BYTES + (8 * MAX_TRIANGLES + 4) * Integer.BYTES, ArrayUtil.toVec4FloatArray(meshes[i].minBounds()));
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * stride + (6 * MAX_TRIANGLES + 4) * Float.BYTES + (8 * MAX_TRIANGLES + 4) * Integer.BYTES, ArrayUtil.toVec4FloatArray(meshes[i].maxBounds()));
        }
    }

    public int numObjects() {
        return numObjects;
    }
}
