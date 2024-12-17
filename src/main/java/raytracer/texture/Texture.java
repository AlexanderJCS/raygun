package raytracer.texture;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Texture {
    private final ByteBuffer bytes;
    private final int width;
    private final int height;


    public Texture(String path) {
        if (!path.endsWith(".png")) {
            throw new IllegalArgumentException("Only PNG images are supported");
        }

        BufferedImage img;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        width = img.getWidth();
        height = img.getHeight();

        // Check if the image has an alpha channel
        boolean hasAlpha = img.getColorModel().hasAlpha();

        // Extract raw pixel data
        int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);

        // Allocate a ByteBuffer based on whether alpha is present
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixels[i * width + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                byteBuffer.put((byte) (pixel & 0xFF));         // Blue
                byteBuffer.put(hasAlpha ? (byte) ((pixel >> 24) & 0xFF) : (byte) 0xFF); // Alpha
            }
        }

        // Copy ByteBuffer content to a byte array
        byteBuffer.flip();
        bytes = byteBuffer;
    }

    public ByteBuffer bytes() {
        return bytes;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
