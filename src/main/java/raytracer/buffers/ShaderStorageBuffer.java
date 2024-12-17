package raytracer.buffers;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public abstract class ShaderStorageBuffer {
    private final int id;
    private final int bindingPont;

    public ShaderStorageBuffer(int id, int bindingPoint) {
        this.id = id;
        this.bindingPont = bindingPoint;
    }

    protected int id() {
        return id;
    }

    public void bind() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, bindingPont, id);
    }

    public void unbind() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
    }

    public void cleanup() {
        glDeleteBuffers(id);
    }
}
