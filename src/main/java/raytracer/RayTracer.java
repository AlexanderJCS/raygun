package raytracer;

import org.joml.Vector3f;
import raytracer.buffers.*;
import raytracer.config.RenderConfig;
import raytracer.config.Savepoint;
import raytracer.rendering.ScreenQuad;
import raytracer.compute.RayTracerCompute;
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

//        Mesh cornellFloor = new Mesh(
//                new Vector3f[]{
//                        new Vector3f(-2, -0.99f, -0),
//                        new Vector3f(2, -0.99f, -0),
//                        new Vector3f(2, -0.99f, -4),
//                        new Vector3f(-2, -0.99f, -4)
//                },
//                new Vector3i[]{
//                        new Vector3i(0, 1, 2),
//                        new Vector3i(0, 2, 3)
//                },
//                0
//        );
//
//        Mesh cornellCeiling = new Mesh(
//                new Vector3f[]{
//                        new Vector3f(-1, 0.99f, -1),
//                        new Vector3f(1, 0.99f, -1),
//                        new Vector3f(1, 0.99f, -3),
//                        new Vector3f(-1, 0.99f, -3)
//                },
//                new Vector3i[]{
//                        new Vector3i(0, 2, 1),
//                        new Vector3i(0, 3, 2)
//                },
//                0
//        );
//
//        Mesh cornellBackWall = new Mesh(
//                new Vector3f[]{
//                        new Vector3f(-1, -1, -2.999f),
//                        new Vector3f(1, -1, -2.999f),
//                        new Vector3f(1, 1, -2.999f),
//                        new Vector3f(-1, 1, -2.999f)
//                },
//                new Vector3i[]{
//                        new Vector3i(0, 1, 2),
//                        new Vector3i(0, 2, 3)
//                },
//                0
//        );
//
//        Mesh cornellLeftWall = new Mesh(
//                new Vector3f[]{
//                        new Vector3f(-0.999f, -2, -1),
//                        new Vector3f(-0.999f, -2, -3),
//                        new Vector3f(-0.999f, 2, -3),
//                        new Vector3f(-0.999f, 2, -1)
//                },
//                new Vector3i[]{
//                        new Vector3i(0, 1, 2),
//                        new Vector3i(0, 2, 3)
//                },
//                0
//        );
//
//        Mesh cornellRightWall = new Mesh(
//                new Vector3f[]{
//                        new Vector3f(0.999f, -1, -1),
//                        new Vector3f(0.999f, -1, -3),
//                        new Vector3f(0.999f, 1, -3),
//                        new Vector3f(0.999f, 1, -1)
//                },
//                new Vector3i[]{
//                        new Vector3i(0, 2, 1),
//                        new Vector3i(0, 3, 2)
//                },
//                0
//        );
//
//        Mesh cornellFrontWall = new Mesh(
//                new Vector3f[]{
//                        new Vector3f(-1, -1, -1.001f),
//                        new Vector3f(1, -1, -1.001f),
//                        new Vector3f(1, 1, -1.001f),
//                        new Vector3f(-1, 1, -1.001f)
//                },
//                new Vector3i[]{
//                        new Vector3i(0, 2, 1),
//                        new Vector3i(0, 3, 2)
//                },
//                0
//        );
//
//        Mesh cornellLight = new Mesh(
//                new Vector3f[]{
//                        new Vector3f(-0.15f, 0.989f, -1.5f),
//                        new Vector3f(0.15f, 0.989f, -1.5f),
//                        new Vector3f(0.15f, 0.989f, -2.5f),
//                        new Vector3f(-0.15f, 0.989f, -2.5f)
//                },
//                new Vector3i[]{
//                        new Vector3i(0, 1, 2),
//                        new Vector3i(0, 2, 3)
//                },
//                1
//        );

        try {
            Mesh cube = Mesh.load("src/main/resources/quad.obj", 0);
            cube.transform(new Vector3f(0, 0, -4), new Vector3f(0, 1f, 0), new Vector3f(1f));
            objectsBuffer = new ObjectsBuffer(new Mesh[]{cube});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load mesh", e);
        }

        materialsBuffer = new MaterialsBuffer(
                new Vector3f[]{
                        new Vector3f(0.9f, 0.3f, 0.3f),
                        new Vector3f(1, 1, 1),
                        new Vector3f(1f, 0, 0),
                        new Vector3f(167/255f, 199/255f, 231/255f)
                },
                new Vector3f[]{
                        new Vector3f(0),
                        new Vector3f(0.8f, 0.8f, 0.9f),
                        new Vector3f(0.8f),
                        new Vector3f(0)
                },
                new float[]{0, 0, 10f, 0},
                new int[]{0, 0, 0, 1},
                new float[]{0.9f, 0, 0f, 0.8f},
                new float[]{0, 0, 0.4f, 0},
                new int[]{0, 0, 0, 0}
        );

        spheresBuffer = new SpheresBuffer(
                new Vector3f[]{
//                        new Vector3f(0, 0, -2)
                },
                new float[]{
//                        1
                },
                new int[]{
//                        1
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