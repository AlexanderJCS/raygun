package raytracer;

import raytracer.config.RenderConfig;

public class Main {
    public static void main(String[] args) {
        RenderConfig config = RenderConfig.fromFile("src/main/resources/config.json");

        Window.init(config.quality().width(), config.quality().height());

        RayTracer rt = new RayTracer(config);
        rt.run();
        rt.cleanup();

        Window.close();
    }
}