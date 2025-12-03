import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFW;

public class Game {
    private boolean running = true;

    // Keyboard movement
    private boolean moveForward = false;
    private boolean moveBackward = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;

    // Mouse movement
    private double mouseX, mouseY;
    private double lastMouseX, lastMouseY;

    public void start() {
        //Setup graphical user interface
        GLFW.glfwInit();
        long window = GLFW.glfwCreateWindow(1280, 720, "VoxelGame", 0, 0);
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Keyboard callback
        GLFW.glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            boolean pressed = (action != GLFW.GLFW_RELEASE);
            switch (key) {
                case GLFW.GLFW_KEY_W -> moveForward = pressed;
                case GLFW.GLFW_KEY_S -> moveBackward = pressed;
                case GLFW.GLFW_KEY_A -> moveLeft = pressed;
                case GLFW.GLFW_KEY_D -> moveRight = pressed;
            }
        });

        // Mouse callback
        GLFW.glfwSetCursorPosCallback(window, (win, x, y) -> {
            mouseX = x;
            mouseY = y;
        });

        //Current time, used for calculations based on time passed
        long lastTime = System.nanoTime();

        //Main game loop
        while (running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            //Get input for the next frame
            GLFW.glfwPollEvents();
            //Calculate new position, animation etc. based on input from the previous frame
            update(deltaTime);
            //Draw the current state
            render();
            //Show rendered image on screen
            GLFW.glfwSwapBuffers(window);

        }
    }

    private void update(double deltaTime) {

        float speed = 5.0f; // Einheiten pro Sekunde


        if (moveForward) player.posZ -= speed * deltaTime;
        if (moveBackward) player.posZ += speed * deltaTime;
        if (moveLeft) player.posX -= speed * deltaTime;
        if (moveRight) player.posX += speed * deltaTime;

        //Update Camera with current position and changes of mouse
        Camera.updatePosition((float)player.posX, (float)player.posY, (float)player.posZ);
        Camera.updateView((float)(mouseX-lastMouseX),(float)(mouseY-lastMouseY));

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }
    private void render() {

    }

}
