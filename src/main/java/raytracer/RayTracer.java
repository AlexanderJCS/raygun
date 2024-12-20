package raytracer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import raytracer.buffers.*;
import raytracer.config.RenderConfig;
import raytracer.config.Savepoint;
import raytracer.rendering.ScreenQuad;
import raytracer.shaders.RayTracerCompute;
import raytracer.rendering.TextureShader;
import raytracer.rendering.ScreenTexture;
import raytracer.texture.ArrayTexture;
import raytracer.texture.Texture;
import raytracer.util.Clock;

import java.io.IOException;

public class RayTracer {
    private final ScreenQuad screenQuad;
    private final TextureShader textureShader;
    private final RayTracerCompute rayTracerCompute;
    private final ScreenTexture screenTexture;
    private final ObjectsBuffer objectsBuffer;
    private final MaterialsBuffer materialsBuffer;
    private final SpheresBuffer spheresBuffer;
    private final ArrayTexture arrayTextureDiffuse;
    private final ArrayTexture arrayTextureNormal;
    private final ArrayTexture arrayTextureParallax;

    private final RenderConfig config;
    private final CameraBuffer cameraBuffer;

    public RayTracer(RenderConfig config) {
        this.config = config;

        arrayTextureDiffuse = new ArrayTexture(new Texture[]{new Texture("src/main/resources/textures/bricks2/diffuse.png")}, 0);
        arrayTextureNormal = new ArrayTexture(new Texture[]{new Texture("src/main/resources/textures/bricks2/normal.png")}, 1);
        arrayTextureParallax = new ArrayTexture(new Texture[]{new Texture("src/main/resources/textures/bricks2/parallax.png")}, 2);

        screenQuad = new ScreenQuad();
        textureShader = new TextureShader();
        rayTracerCompute = new RayTracerCompute();
        screenTexture = new ScreenTexture(config.quality().width(), config.quality().height());
        cameraBuffer = new CameraBuffer(config.camera());

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
                new Vector2f[]{},
                new Vector3i[]{},
                2
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
                new Vector2f[]{},
                new Vector3i[]{},
                2
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
                new Vector2f[]{},
                new Vector3i[]{},
                2
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
                new Vector2f[]{},
                new Vector3i[]{},
                3
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
                new Vector2f[]{},
                new Vector3i[]{},
                4
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
                new Vector2f[]{},
                new Vector3i[]{},
                2
        );

        Mesh cornellLight = new Mesh(
                new Vector3f[]{
                        new Vector3f(-0.2f, 0.989f, -1.5f),
                        new Vector3f(0.2f, 0.989f, -1.5f),
                        new Vector3f(0.2f, 0.989f, -2.5f),
                        new Vector3f(-0.2f, 0.989f, -2.5f)
                },
                new Vector3i[]{
                        new Vector3i(0, 1, 2),
                        new Vector3i(0, 2, 3)
                },
                new Vector2f[]{},
                new Vector3i[]{},
                5
        );

        try {
            Mesh bottom = Mesh.load("src/main/resources/tree_bottom.obj", 0);
            Mesh top = Mesh.load("src/main/resources/tree_top.obj", 1);
            bottom.transform(new Vector3f(0, -1, -2f), new Vector3f(0, 0, 0), new Vector3f(0.5f));
            top.transform(new Vector3f(0, -1, -2f), new Vector3f(0, 0, 0), new Vector3f(0.5f));
            objectsBuffer = new ObjectsBuffer(new Mesh[]{cornellBackWall, cornellFloor, cornellCeiling, cornellFrontWall, cornellLeftWall, cornellRightWall, cornellLight, top});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load mesh", e);
        }

        materialsBuffer = new MaterialsBuffer(
                new Vector3f[]{
                        new Vector3f(105/255f, 75/255f, 55/255f),
                        new Vector3f(95/255f, 99/255f, 68/255f),
                        new Vector3f(0.95f),
                        new Vector3f(0.7f, 0.95f, 0.7f),
                        new Vector3f(0.7f, 0.95f, 0.7f),
                        new Vector3f(1, 1, 1),
                        new Vector3f(233/255f, 191/255f, 4/255f),
                        new Vector3f(1)
                },
                new Vector3f[]{
                        new Vector3f(0),
                        new Vector3f(0),
                        new Vector3f(0),
                        new Vector3f(0),
                        new Vector3f(0),
                        new Vector3f(1),
                        new Vector3f(233/255f, 191/255f, 4/255f),
                        new Vector3f(0)
                },
                new float[]{0, 0, 0, 0, 0, 1, 0.1f, 0},
                new int[]{0, 0, 1, 1, 1, 0, 1, 1},
                new float[]{0, 0, 0.005f, 0.01f, 0.005f, 0, 0.9f, 0.8f},
                new float[]{0, 0, 0, 0, 0, 0, 0.05f, 0.1f},
                new int[]{-1, -1, -1, -1, -1, -1, -1, -1}
        );

        spheresBuffer = new SpheresBuffer(
                new Vector3f[]{
                        new Vector3f(-0.414f, -0.1279f, -1.656f),
                        new Vector3f(-0.122f, -0.22f, -1.63f),
                        new Vector3f(-0.122f, 0.417f, -1.76f),
                        new Vector3f(0.28f, 0.058f, -1.832f),
                        new Vector3f(0.28f, 0.336f, -2.015f),
                        new Vector3f(0.373f, -0.444f, -1.74f),
                        new Vector3f(-0.43f, -0.72f, -1.70f),
                        new Vector3f(-0f, -0.53f, -2.44f),
                        new Vector3f(-0.34f, -0.02f, -2.2f),
                },
                new float[]{
                        0.047f,
                        0.056f,
                        0.051f,
                        0.05f,
                        0.062f,
                        0.043f,
                        0.053f,
                        0.051f,
                        0.058f,
                },
                new int[]{
                        7,
                        6,
                        7,
                        6,
                        6,
                        7,
                        6,
                        7,
                        6,
                }
        );
    }

    private void computeFrame(Clock clock) {
        materialsBuffer.bind();
        objectsBuffer.bind();
        spheresBuffer.bind();
        cameraBuffer.bind();
        arrayTextureDiffuse.bind();
        arrayTextureNormal.bind();
        arrayTextureParallax.bind();
        screenTexture.bindWrite();
        rayTracerCompute.compute(config.quality().width(), config.quality().height(), objectsBuffer.numObjects(), spheresBuffer.numSpheres(), clock.getFrameCount(), config.quality().bounces());
        screenTexture.unbindWrite();
        arrayTextureParallax.unbind();
        arrayTextureNormal.unbind();
        arrayTextureDiffuse.bind();
        cameraBuffer.unbind();
        spheresBuffer.unbind();
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
        arrayTextureNormal.cleanup();
        arrayTextureDiffuse.cleanup();
        arrayTextureParallax.cleanup();
        cameraBuffer.cleanup();
        spheresBuffer.cleanup();
        screenQuad.cleanup();
        textureShader.cleanup();
        rayTracerCompute.cleanup();
        screenTexture.cleanup();
        objectsBuffer.cleanup();
        materialsBuffer.cleanup();
    }
}
