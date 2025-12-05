import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Camera {

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch, yaw;


    private float sensitivity = 1;

    public float getSensitivity() {
        return sensitivity;
    }
    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }


    public void updateView(float mouseDeltaX, float mouseDeltaY) {
        this.yaw += mouseDeltaX * sensitivity;
        this.pitch -= mouseDeltaY * sensitivity;
        //To prevent the camera from rotating over the player's head
        this.pitch = Math.clamp(this.pitch, -89f, 89f);
    }

    public void updatePosition(float x, float y, float z){
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }


    public Matrix4f getViewMatrix() {
        return new Matrix4f()
                .rotateXYZ((float)Math.toRadians(pitch),
                        (float)Math.toRadians(yaw), 0)
                .translate(-position.x, -position.y, -position.z);
    }

}
