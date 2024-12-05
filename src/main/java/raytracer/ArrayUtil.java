package raytracer;

import org.joml.Vector3f;

public class ArrayUtil {
    private ArrayUtil() {}

    public static float[] toVec4FloatArray(Vector3f[] vectors) {
        float[] floats = new float[vectors.length * 4];
        for (int i = 0; i < vectors.length; i++) {
            floats[i * 4] = vectors[i].x;
            floats[i * 4 + 1] = vectors[i].y;
            floats[i * 4 + 2] = vectors[i].z;
            floats[i * 4 + 3] = 0;
        }

        return floats;
    }

    public static float[] toVec3FloatArray(Vector3f[] vectors) {
        float[] floats = new float[vectors.length * 3];
        for (int i = 0; i < vectors.length; i++) {
            floats[i * 3] = vectors[i].x;
            floats[i * 3 + 1] = vectors[i].y;
            floats[i * 3 + 2] = vectors[i].z;
        }

        return floats;
    }
}
