package raytracer;

import org.joml.Vector3f;
import org.joml.Vector3i;
import raytracer.config.RenderConfig;
import raytracer.config.Savepoint;
import raytracer.ssbo.MaterialsBuffer;
import raytracer.ssbo.Mesh;
import raytracer.ssbo.ObjectsBuffer;

import java.io.IOException;

public class RayTracer {
    private final ScreenQuad screenQuad;
    private final TextureShader textureShader;
    private final RayTracerCompute rayTracerCompute;
    private final ScreenTexture screenTexture;
    private final ObjectsBuffer objectsBuffer;
    private final MaterialsBuffer materialsBuffer;

    private final RenderConfig config;

    public RayTracer(RenderConfig config) {
        this.config = config;

        screenQuad = new ScreenQuad();
        textureShader = new TextureShader();
        rayTracerCompute = new RayTracerCompute();
        screenTexture = new ScreenTexture(config.quality().width(), config.quality().height());

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

        try {
            Mesh mesh = Mesh.load("src/main/resources/suzanne.obj", 2);
            mesh.transform(new Vector3f(0, 0, -3), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f));

            objectsBuffer = new ObjectsBuffer(new Mesh[]{cornellFloor, cornellCeiling, cornellBackWall, cornellLeftWall, cornellRightWall, cornellFrontWall, mesh, cornellLight});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load mesh", e);
        }

        materialsBuffer = new MaterialsBuffer(
                new Vector3f[]{
                        new Vector3f(0.8f),
                        new Vector3f(1, 1, 1),
                        new Vector3f(0.9f, 0.2f, 0.2f)
                },
                new Vector3f[]{
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f, 0.7f, 0.9f),
                        new Vector3f(0.9f, 0.1f, 0.1f)
                },
                new float[]{0, 4f, 0},
                new int[]{0, 0, 2},
                new float[]{0.001f, 0, 0.025f});
    }

    private void computeFrame(Clock clock) {
        materialsBuffer.bind();
        objectsBuffer.bind();
        screenTexture.bindWrite();
        rayTracerCompute.compute(config.quality().width(), config.quality().height(), objectsBuffer.numObjects(), clock.getFrameCount(), config.quality().bounces());
        screenTexture.unbindWrite();
        objectsBuffer.unbind();
        materialsBuffer.unbind();
    }

    private void drawFrame() {
        screenTexture.bindRead();
        textureShader.bind();
        screenQuad.draw();
        textureShader.unbind();
        screenTexture.unbindRead();
    }

    public void run() {
        Clock clock = new Clock();

        while (Window.shouldRun()) {
            computeFrame(clock);
            drawFrame();

            Window.update();
            Window.clear();
            clock.update();

            Window.setTitle("Ray Tracer | FPS: " + Math.round(clock.getSmoothedFps()) + " | Frame Time: " + (Math.round(1 / clock.getSmoothedFps() * 10000) / 10f) + "ms");

            for (Savepoint savepoint : config.savepoints()) {
                if (savepoint.readyToSave(clock.getTimef(), clock.getFrameCount())) {
                    screenTexture.saveToFile(savepoint.path());
                    savepoint.markSaved();
                }
            }
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
