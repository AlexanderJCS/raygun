package raytracer;

import org.joml.Vector3f;
import org.joml.Vector3i;
import raytracer.ssbo.MaterialsBuffer;
import raytracer.ssbo.Mesh;
import raytracer.ssbo.ObjectsBuffer;

public class RayTracer {
    private final ScreenQuad screenQuad;
    private final TextureShader textureShader;
    private final RayTracerCompute rayTracerCompute;
    private final ScreenTexture screenTexture;
    private final ObjectsBuffer objectsBuffer;
    private final MaterialsBuffer materialsBuffer;

    private final int width, height;

    public RayTracer(int width, int height) {
        this.width = width;
        this.height = height;

        screenQuad = new ScreenQuad();
        textureShader = new TextureShader();
        rayTracerCompute = new RayTracerCompute();
        screenTexture = new ScreenTexture(width, height);

        Mesh cornellFloor = new Mesh(
                new Vector3f[]{
                        new Vector3f(-2, -0.99f, -0),
                        new Vector3f(2, -0.99f, -0),
                        new Vector3f(2, -0.99f, -4),
                        new Vector3f(-2, -0.99f, -4)
                },
                new Vector3i[]{
                        new Vector3i(0, 1, 2),
                        new Vector3i(0, 2, 3)
                },
                0
        );

        Mesh cornellCeiling = new Mesh(
                new Vector3f[]{
                        new Vector3f(-1, 0.99f, -1),
                        new Vector3f(1, 0.99f, -1),
                        new Vector3f(1, 0.99f, -3),
                        new Vector3f(-1, 0.99f, -3)
                },
                new Vector3i[]{
                        new Vector3i(0, 2, 1),
                        new Vector3i(0, 3, 2)
                },
                0
        );

        Mesh cornellBackWall = new Mesh(
                new Vector3f[]{
                        new Vector3f(-1, -1, -2.999f),
                        new Vector3f(1, -1, -2.999f),
                        new Vector3f(1, 1, -2.999f),
                        new Vector3f(-1, 1, -2.999f)
                },
                new Vector3i[]{
                        new Vector3i(0, 1, 2),
                        new Vector3i(0, 2, 3)
                },
                0
        );

        Mesh cornellLeftWall = new Mesh(
                new Vector3f[]{
                        new Vector3f(-0.999f, -2, -1),
                        new Vector3f(-0.999f, -2, -3),
                        new Vector3f(-0.999f, 2, -3),
                        new Vector3f(-0.999f, 2, -1)
                },
                new Vector3i[]{
                        new Vector3i(0, 1, 2),
                        new Vector3i(0, 2, 3)
                },
                0
        );

        Mesh cornellRightWall = new Mesh(
                new Vector3f[]{
                        new Vector3f(0.999f, -1, -1),
                        new Vector3f(0.999f, -1, -3),
                        new Vector3f(0.999f, 1, -3),
                        new Vector3f(0.999f, 1, -1)
                },
                new Vector3i[]{
                        new Vector3i(0, 2, 1),
                        new Vector3i(0, 3, 2)
                },
                0
        );

        Mesh cornellFrontWall = new Mesh(
                new Vector3f[]{
                        new Vector3f(-1, -1, -1.001f),
                        new Vector3f(1, -1, -1.001f),
                        new Vector3f(1, 1, -1.001f),
                        new Vector3f(-1, 1, -1.001f)
                },
                new Vector3i[]{
                        new Vector3i(0, 2, 1),
                        new Vector3i(0, 3, 2)
                },
                0
        );

        Mesh cornellLight = new Mesh(
                new Vector3f[]{
                        new Vector3f(-0.15f, 0.989f, -1.5f),
                        new Vector3f(0.15f, 0.989f, -1.5f),
                        new Vector3f(0.15f, 0.989f, -2.5f),
                        new Vector3f(-0.15f, 0.989f, -2.5f)
                },
                new Vector3i[]{
                        new Vector3i(0, 1, 2),
                        new Vector3i(0, 2, 3)
                },
                1
        );

        objectsBuffer = new ObjectsBuffer(new Mesh[]{cornellFloor, cornellCeiling, cornellBackWall, cornellLeftWall, cornellRightWall, cornellFrontWall, cornellLight});
        materialsBuffer = new MaterialsBuffer(new Vector3f[]{new Vector3f(0.76f, 0.76f, 0.8f), new Vector3f(1, 1, 1)}, new Vector3f[]{new Vector3f(0, 0, 0), new Vector3f(0.7f, 0.7f, 0.9f)}, new float[]{0, 5f}, new int[]{0, 0}, new float[]{0, 0});
    }

    public void run() {
        Clock clock = new Clock();

        while (Window.shouldRun()) {
            materialsBuffer.bind();
            objectsBuffer.bind();
            screenTexture.bindWrite();
            rayTracerCompute.compute(width, height, objectsBuffer.numObjects(), 0, clock.getFrameCount());
            screenTexture.unbindWrite();
            objectsBuffer.unbind();
            materialsBuffer.unbind();

            screenTexture.bindRead();
            textureShader.bind();
            screenQuad.draw();
            textureShader.unbind();
            screenTexture.unbindRead();

            Window.update();
            Window.clear();
            clock.update();

            Window.setTitle("Ray Tracer | FPS: " + Math.round(clock.getSmoothedFps()));
        }
    }

    public void cleanup() {
        screenQuad.cleanup();
        textureShader.cleanup();
        rayTracerCompute.cleanup();
        screenTexture.cleanup();
        objectsBuffer.cleanup();
        materialsBuffer.cleanup();
    }
}
