package raytracer;

import static org.lwjgl.opengl.GL45.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class ScreenTexture {
    private int texture;
    private final int width, height;

    public ScreenTexture(int screenWidth, int screenHeight) {
        width = screenWidth;
        height = screenHeight;

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

    public float[] readData() {
        float[] data = new float[width * height * 4];
        glGetTextureImage(texture, 0, GL_RGBA, GL_FLOAT, data);
        return data;
    }

    public void saveToFile(String filename) throws RuntimeException {
        float[] data = readData();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < data.length; i += 4) {
            // clamp from HDR to LDR
            data[i] = Math.min(1, Math.max(0, data[i]));
            data[i + 1] = Math.min(1, Math.max(0, data[i + 1]));
            data[i + 2] = Math.min(1, Math.max(0, data[i + 2]));

            int r = (int) (data[i] * 255);
            int g = (int) (data[i + 1] * 255);
            int b = (int) (data[i + 2] * 255);
            int rgb = (r << 16) | (g << 8) | b;
            image.setRGB(i / 4 % width, height - i / 4 / width - 1, rgb);
        }

        try {
            ImageIO.write(image, "png", new java.io.File(filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public void cleanup() {
        glDeleteTextures(texture);
    }
}
