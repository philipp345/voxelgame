import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
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

    // Window class variable, will be used in init function
    private long window;

    //Shader program for HUD
    private Shader hudShaderProgram;

    public void start() {
        //Initialize graphical setup
        init();

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
        //add game logic later
        float speed = 5.0f; // Einheiten pro Sekunde

        if (moveForward)  player.posZ -= speed * deltaTime;
        if (moveBackward) player.posZ += speed * deltaTime;
        if (moveLeft)     player.posX -= speed * deltaTime;
        if (moveRight)    player.posX += speed * deltaTime;

        // Maus kann Kamera drehen
        player.rotationY = (float) mouseX;
        player.rotationX = (float) mouseY;
    }


    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        renderWorld();
        renderHUD();
    }

    private void renderHUD() {
        // Disable depth test, so HUD is always visible
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        // Activate shaders
        hudShaderProgram.bind();
        // Set window uniform
        int loc = GL20.glGetUniformLocation(hudShaderProgram.getId(), "uResolution");
        GL20.glUniform2f(loc, windowWidth, windowHeight);
        // VAO des Fadenkreuzes binden
        GL30.glBindVertexArray(crosshairVAO);
        // 4 Punkte = 2 Linien
        GL11.glDrawArrays(GL11.GL_LINES, 0, 4);
        // VAO entbinden
        GL30.glBindVertexArray(0);
        // Shader ausschalten
        hudShaderProgram.unbind();
        // Activate depth test again
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    private void renderWorld() {

    }

    private void init() {
        //Setup graphical user interface
        GLFW.glfwInit();
        window = GLFW.glfwCreateWindow(1280, 720, "VoxelGame", 0, 0);
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        // Enable raw mouse motion if supported.
        // Raw mouse input bypasses operating system mouse acceleration,
        // providing unfiltered hardware movement. This ensures consistent,
        // precise FPS-style camera control across all systems.
        // If the platform does not support raw motion, this check prevents errors.
        if (GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
        }

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

        //Define Shader
        String vertexShaderSource =
                "#version 330 core\n" +
                        "layout(location = 0) in vec2 aPos;\n" +
                        "uniform vec2 uResolution;\n" +
                        "void main() {\n" +
                        "    vec2 screenPos = aPos + (uResolution * 0.5);\n" +
                        "    vec2 ndc = (screenPos / uResolution) * 2.0 - 1.0;\n" +
                        "    gl_Position = vec4(ndc, 0.0, 1.0);\n" +
                        "}";

        String fragmentShaderSource =
                "#version 330 core\n" +
                        "out vec4 FragColor;\n" +
                        "void main() {\n" +
                        "    FragColor = vec4(1.0, 1.0, 1.0, 0.7);\n" +
                        "}";


        //Create Shader
        hudShaderProgram = new Shader(vertexShaderSource, fragmentShaderSource);



        Mesh.createCrosshairVAO();
    }





}
