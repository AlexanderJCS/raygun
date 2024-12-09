package raytracer.ssbo;

import org.joml.Vector3f;
import org.joml.Vector3i;

public record Mesh(Vector3f[] vertices, Vector3i indices, int materialIndex) {
}
