package raytracer;

import org.joml.Vector3f;

public class RayTracer {
    private final ScreenQuad screenQuad;
    private final TextureShader textureShader;
    private final RayTracerCompute rayTracerCompute;
    private final ScreenTexture screenTexture;
    private final ObjectsBuffer objectsBuffer;

    private final int width, height;

    public RayTracer(int width, int height) {
        this.width = width;
        this.height = height;

        screenQuad = new ScreenQuad();
        textureShader = new TextureShader();
        rayTracerCompute = new RayTracerCompute();
        screenTexture = new ScreenTexture(width, height);
        objectsBuffer = new ObjectsBuffer(new Vector3f[]{new Vector3f(0, -100.5f, -1), new Vector3f(0, 0, -1)}, new float[]{100f, 0.5f}, new int[]{0, 0});
    }

    public void run() {
        while (Window.shouldRun()) {
            objectsBuffer.bind();
            screenTexture.bindWrite();
            rayTracerCompute.compute(width, height, objectsBuffer.numObjects(), 0);
            screenTexture.unbindWrite();
            objectsBuffer.unbind();

            screenTexture.bindRead();
            textureShader.bind();
            screenQuad.draw();
            textureShader.unbind();
            screenTexture.unbindRead();

            Window.update();
            Window.clear();
        }
    }

    public void cleanup() {
        screenQuad.cleanup();
        textureShader.cleanup();
        rayTracerCompute.cleanup();
        screenTexture.cleanup();
        objectsBuffer.cleanup();
    }
}
