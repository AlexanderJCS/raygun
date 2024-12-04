package raytracer;

import static org.lwjgl.opengl.GL45.*;

public class ScreenTexture {
    private int texture;

    public ScreenTexture(int screenWidth, int screenHeight) {
        texture = glCreateTextures(GL_TEXTURE_2D);
        glTextureParameteri(texture, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTextureParameteri(texture, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTextureParameteri(texture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTextureParameteri(texture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTextureStorage2D(texture, 1, GL_RGBA32F, screenWidth, screenHeight);
    }

    public void bindWrite() {
        glBindImageTexture(0, texture, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
    }

    public void unbindWrite() {
        glBindImageTexture(0, 0, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
    }

    public void bindRead() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void unbindRead() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        glDeleteTextures(texture);
    }
}
