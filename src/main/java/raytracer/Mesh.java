package raytracer;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.*;
import java.util.Arrays;

public record Mesh(Vector3f[] vertices, Vector3i[] indices, int materialIndex) {
    /**
     * Loads an obj file from the given path.
     * @param path the path to the obj file
     * @return the mesh
     */
    public static Mesh load(String path, int materialIndex) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            Vector3f[] vertices = reader.lines()
                    .filter(line -> line.startsWith("v "))
                    .map(line -> {
                        String[] split = line.split(" ");
                        return new Vector3f(
                                Float.parseFloat(split[1]),
                                Float.parseFloat(split[2]),
                                Float.parseFloat(split[3])
                        );
                    })
                    .toArray(Vector3f[]::new);

            fis.getChannel().position(0);
            reader = new BufferedReader(new InputStreamReader(fis));

            Vector3i[] indices = reader.lines()
                    .filter(line -> line.startsWith("f "))
                    .map(line -> {
                        String[] split = line.split(" ");

                        String[][] split2 = new String[3][];
                        for (int i = 0; i < 3; i++) {
                            split2[i] = split[i + 1].split("/");
                        }

                        return new Vector3i(
                                Integer.parseInt(split2[0][0]) - 1,
                                Integer.parseInt(split2[1][0]) - 1,
                                Integer.parseInt(split2[2][0]) - 1
                        );
                    })
                    .toArray(Vector3i[]::new);

            return new Mesh(vertices, indices, materialIndex);
        }
    }

    public void transform(Vector3f translation, Vector3f rotation, Vector3f scale) {
        for (Vector3f vertex : vertices) {
            vertex.mul(scale);
            vertex.rotateX(rotation.x);
            vertex.rotateY(rotation.y);
            vertex.rotateZ(rotation.z);
            vertex.add(translation);
        }
    }

    public Vector3f[] minBounds() {
        Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
        for (Vector3f vertex : vertices) {
            min.min(vertex);
        }
        return new Vector3f[]{min};
    }

    public Vector3f[] maxBounds() {
        Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);
        for (Vector3f vertex : vertices) {
            max.max(vertex);
        }
        return new Vector3f[]{max};
    }

    public String toString() {
        return "Mesh{vertices=" + Arrays.toString(vertices) + ", indices=" + Arrays.toString(indices) + ", materialIndex=" + materialIndex + "}";
    }
}
