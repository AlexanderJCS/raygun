package raytracer;

import org.joml.Vector3f;

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
        objectsBuffer = new ObjectsBuffer(new Vector3f[]{new Vector3f(0, -100.5f, -1), new Vector3f(100, 160, -1), new Vector3f(0, 0, -1)}, new float[]{100f, 110f, 0.5f}, new int[]{0, 1, 0});
        materialsBuffer = new MaterialsBuffer(new Vector3f[]{new Vector3f(1, 0.2f, 0.2f), new Vector3f(1, 1, 1)}, new Vector3f[]{new Vector3f(0, 0, 0), new Vector3f(0.7f, 0.7f, 0.9f)}, new float[]{0, 1.5f}, new int[]{0, 0}, new float[]{0, 0});
    }

    public void run() {
        int frameCounter = 0;

        while (Window.shouldRun()) {
            materialsBuffer.bind();
            objectsBuffer.bind();
            screenTexture.bindWrite();
            rayTracerCompute.compute(width, height, objectsBuffer.numObjects(), 0, frameCounter);
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
            frameCounter++;
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
