package raytracer.ssbo;

import static org.lwjgl.opengl.GL43.*;
import org.joml.Vector3f;
import raytracer.ArrayUtil;


public class MaterialsBuffer extends ShaderStorageBuffer {
    private static final int MAX_MATERIALS = 100;

    public MaterialsBuffer(Vector3f[] albedos, Vector3f[] emissionColor, float[] emissionStrength, int[] materialType, float[] fuzz) {
        super(glGenBuffers(), 0);


        float[] albedosFloat = ArrayUtil.toVec3FloatArray(albedos);
        float[] emissionColorFloat = ArrayUtil.toVec3FloatArray(emissionColor);

        float[] albedosPadded = new float[MAX_MATERIALS * 3];
        System.arraycopy(albedosFloat, 0, albedosPadded, 0, albedosFloat.length);

        float[] emissionColorPadded = new float[MAX_MATERIALS * 3];
        System.arraycopy(emissionColorFloat, 0, emissionColorPadded, 0, emissionColorFloat.length);

        float[] emissionStrengthPadded = new float[MAX_MATERIALS];
        System.arraycopy(emissionStrength, 0, emissionStrengthPadded, 0, emissionStrength.length);

        float[] fuzzPadded = new float[MAX_MATERIALS];
        System.arraycopy(fuzz, 0, fuzzPadded, 0, fuzz.length);

        int[] materialTypePadded = new int[MAX_MATERIALS];
        System.arraycopy(materialType, 0, materialTypePadded, 0, materialType.length);

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id());
        glBufferData(GL_SHADER_STORAGE_BUFFER, (long) (MAX_MATERIALS * 10) * Float.BYTES, GL_DYNAMIC_DRAW);

        for (int i = 0; i < MAX_MATERIALS; i++) {
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) i * 12 * Float.BYTES, new float[]{albedosPadded[i * 3], albedosPadded[i * 3 + 1], albedosPadded[i * 3 + 2]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 12 + 4) * Float.BYTES, new float[]{emissionColorPadded[i * 3], emissionColorPadded[i * 3 + 1], emissionColorPadded[i * 3 + 2]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 12 + 7) * Float.BYTES, new float[]{emissionStrengthPadded[i]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 12 + 8) * Float.BYTES, new int[]{materialTypePadded[i]});
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, (long) (i * 12 + 9) * Float.BYTES, new float[]{fuzzPadded[i]});
        }
    }
}
