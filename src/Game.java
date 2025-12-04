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

    // Camera field
    private Camera camera;

    // Player field
    private Player player;

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

        //Instantiate new camera object, the same object will be used during the entire main game loop
        camera = new Camera();

        //Instantiate new player object, the same object will be used ruing the entire main game loop
        player = new Player();

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
        double deltaPosition = speed * deltaTime;


        if (moveForward) player.setPositionZ((player.getPositionZ() - deltaPosition));
        if (moveBackward) player.setPositionZ((player.getPositionZ() + deltaPosition));
        if (moveLeft) player.setPositionX((player.getPositionX() - deltaPosition));
        if (moveRight) player.setPositionX((player.getPositionX() + deltaPosition));

        //Update Camera with current position and changes of mouse
        camera.updatePosition((float)player.getPositionX(), (float)player.getPositionY(), (float)player.getPositionZ());
        camera.updateView((float)(mouseX-lastMouseX),(float)(mouseY-lastMouseY));

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }
    private void render() {

    }

}
