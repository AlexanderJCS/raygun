package raytracer;

import static org.lwjgl.opengl.GL43.*;
import org.joml.Vector3f;


public class MaterialsBuffer {
    private final int ssbo;
    private static final int MAX_MATERIALS = 100;

    public MaterialsBuffer(Vector3f[] albedos, Vector3f[] emissionColor, float[] emissionStrength) {
        float[] albedosFloat = ArrayUtil.toVec3FloatArray(albedos);
        float[] emissionColorFloat = ArrayUtil.toVec3FloatArray(emissionColor);

        float[] albedosPadded = new float[MAX_MATERIALS * 3];
        System.arraycopy(albedosFloat, 0, albedosPadded, 0, albedosFloat.length);

        float[] emissionColorPadded = new float[MAX_MATERIALS * 3];
        System.arraycopy(emissionColorFloat, 0, emissionColorPadded, 0, emissionColorFloat.length);

        float[] emissionStrengthPadded = new float[MAX_MATERIALS];
        System.arraycopy(emissionStrength, 0, emissionStrengthPadded, 0, emissionStrength.length);

        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, (long) (MAX_MATERIALS * 7) * Float.BYTES, GL_DYNAMIC_DRAW);

        for (int i = 0; i < MAX_MATERIALS; i++) {
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * 7 * Float.BYTES, new float[]{albedosPadded[i * 3], albedosPadded[i * 3 + 1], albedosPadded[i * 3 + 2]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 7 + 3) * Float.BYTES, new float[]{emissionColorPadded[i * 3], emissionColorPadded[i * 3 + 1], emissionColorPadded[i * 3 + 2]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 7 + 6) * Float.BYTES, new float[]{emissionStrengthPadded[i]});
        }
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
