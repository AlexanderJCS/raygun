package raytracer.config;

public class RenderQuality {
    private final int width;
    private final int height;
    private final int bounces;

    public RenderQuality(int width, int height, int bounces) {
        this.width = width;
        this.height = height;
        this.bounces = bounces;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int bounces() {
        return bounces;
    }
}
