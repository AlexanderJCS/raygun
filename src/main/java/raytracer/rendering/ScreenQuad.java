package raytracer.rendering;

import static org.lwjgl.opengl.GL43.*;

public class ScreenQuad {
    private final int vao;
    private final int vbo;
    private final int ebo;
    private final int tbo;

    private final int drawCount;
    protected static final int DIMENSIONS = 2;

    private static final float[] vertices = {
            -1f, -1f,
            1f, -1f,
            1f, 1f,
            -1f, 1f
    };

    private static final int[] indices = {
            0, 1, 2,
            2, 3, 0
    };

    private static final float[] texCoords = {
            0, 0,
            1, 0,
            1, 1,
            0, 1
    };

    public ScreenQuad() {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, DIMENSIONS, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        this.drawCount = indices.length;
        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        this.tbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.tbo);
        glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW);

        glVertexAttribPointer(1, DIMENSIONS, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void draw() {
        glBindVertexArray(this.vao);
        glDrawElements(GL_TRIANGLES, this.drawCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(this.vbo);
        glDeleteBuffers(this.ebo);
        glDeleteBuffers(this.tbo);
        glDeleteVertexArrays(this.vao);
    }
}
