package raytracer.texture;

import static org.lwjgl.opengl.GL45C.*;

public class ArrayTexture {
    private final int id;
    private final int unit;

    public ArrayTexture(Texture[] textures, int unit) {
        this.unit = unit;

        for (Texture texture : textures) {
            if (texture.width() != textures[0].width() || texture.height() != textures[0].height()) {
                throw new IllegalArgumentException("All textures must have the same dimensions");
            }
        }

        id = glGenTextures();

        bind();
        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, textures[0].width(), textures[0].height(), textures.length);

        for (int i = 0; i < textures.length; i++) {
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, textures[i].width(), textures[i].height(), 1, GL_RGBA, GL_UNSIGNED_BYTE, textures[i].bytes());
        }

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        unbind();
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D_ARRAY, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}
