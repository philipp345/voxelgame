import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Camera {

    public Vector3f position = new Vector3f(0, 0, 0);
    public float pitch, yaw;

    yaw   += mouseDeltaX * sensitivity;
    pitch -= mouseDeltaY * sensitivity;

    public Matrix4f getViewMatrix() {
        return new Matrix4f()
                .rotateXYZ((float)Math.toRadians(pitch),
                        (float)Math.toRadians(yaw), 0)
                .translate(-position.x, -position.y, -position.z);
    }

}
