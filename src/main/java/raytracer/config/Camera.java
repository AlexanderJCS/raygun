package raytracer.config;

/*
   "camera": {
    "origin": [0, 0, 3],
    "look_at": [0, 0, -1],
    "up": [0, 1, 0],
    "fov": 36,
    "focus_dist": 3,
    "defocus_angle": 2
  },
 */
public class Camera {
    private final float[] origin;
    private final float[] lookAt;
    private final float[] up;
    private final float fov;
    private final float focusDist;
    private final float defocusAngle;

    public Camera(float[] origin, float[] lookAt, float[] up, float fov, float focusDist, float defocusAngle) {
        if (origin.length != 3) {
            throw new IllegalArgumentException("Origin must have 3 components");
        } else if (lookAt.length != 3) {
            throw new IllegalArgumentException("LookAt must have 3 components");
        } else if (up.length != 3) {
            throw new IllegalArgumentException("Up must have 3 components");
        }

        this.origin = origin;
        this.lookAt = lookAt;
        this.up = up;
        this.fov = fov;
        this.focusDist = focusDist;
        this.defocusAngle = defocusAngle;
    }

    public float[] origin() {
        return origin;
    }

    public float[] lookAt() {
        return lookAt;
    }

    public float[] up() {
        return up;
    }

    public float fov() {
        return (float) Math.toRadians(fov);
    }

    public float focusDist() {
        return focusDist;
    }

    public float defocusAngle() {
        return (float) Math.toRadians(defocusAngle);
    }
}
