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
        Mesh mesh = new Mesh(new Vector3f[]{new Vector3f(0, 0, -1), new Vector3f(0, 0.5f, -1f), new Vector3f(-0.5f, 0, -1f), new Vector3f(-0.5f, 0.5f, -1)}, new Vector3i[]{new Vector3i(1, 2, 3), new Vector3i(0, 1, 2)}, 0);
        objectsBuffer = new ObjectsBuffer(new Mesh[]{mesh});
        materialsBuffer = new MaterialsBuffer(new Vector3f[]{new Vector3f(1, 0.2f, 0.2f), new Vector3f(1, 1, 1)}, new Vector3f[]{new Vector3f(0, 0, 0), new Vector3f(0.7f, 0.7f, 0.9f)}, new float[]{0, 1.5f}, new int[]{0, 0}, new float[]{0, 0});
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
