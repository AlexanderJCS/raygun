package raytracer.rendering;

import static org.lwjgl.opengl.GL43.*;

public class TextureShader {
    private static final String vertexShaderSource = """
            #version 450 core
            layout(location = 0) in vec3 position;
            layout(location = 1) in vec2 texCoord;
            out vec2 fragTexCoord;
            void main() {
                gl_Position = vec4(position, 1.0);
                fragTexCoord = texCoord;
            }
            """;

    private static final String fragmentShaderSource = """
            #version 450 core
            in vec2 fragTexCoord;
            out vec4 color;
            uniform sampler2D tex;
            void main() {
                color = texture(tex, fragTexCoord);
            }
            """;

    private final int program;

    public TextureShader() {
        // Compile vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Failed to compile vertex shader:\n" + glGetShaderInfoLog(vertexShader));
        }

        // Compile fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Failed to compile fragment shader:\n" + glGetShaderInfoLog(fragmentShader));
        }

        // Link shaders
        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Failed to link program:\n" + glGetProgramInfoLog(program));
        }

        // Validate program
        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Failed to validate program:\n" + glGetProgramInfoLog(program));
        }

        // Clean up
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void bind() {
        int texLocation = glGetUniformLocation(program, "tex");
        if (texLocation != -1) {
            glUniform1i(texLocation, 0);
        }

        glUseProgram(program);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        glDeleteProgram(program);
    }
}
