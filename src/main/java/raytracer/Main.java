package raytracer;

public class Main {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    public static void main(String[] args) {
        Window.init(WIDTH, HEIGHT);

        RayTracer rt = new RayTracer(WIDTH, HEIGHT);
        rt.run();
        rt.cleanup();

        Window.close();
    }
}