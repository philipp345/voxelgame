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
    private int hudShaderProgram;

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
        GL20.glUseProgram(hudShaderProgram);
        // Set window uniform
        int loc = GL20.glGetUniformLocation(hudShaderProgram, "uResolution");
        GL20.glUniform2f(loc, windowWidth, windowHeight);
        // VAO des Fadenkreuzes binden
        GL30.glBindVertexArray(crosshairVAO);
        // 4 Punkte = 2 Linien
        GL11.glDrawArrays(GL11.GL_LINES, 0, 4);
        // VAO entbinden
        GL30.glBindVertexArray(0);
        // Shader ausschalten
        GL20.glUseProgram(0);
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

        createHUDShader();
        createCrosshairVAO();
    }

    private void createCrosshairVAO() {
        float[] vertices = {
                // x, y, z
                0.0f,  0.05f, 0.0f,
                -0.05f, -0.05f, 0.0f,
                0.05f, -0.05f, 0.0f
        };

        // VAO erzeugen und binden
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // VBO erzeugen und Daten hochladen
        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();

        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        // Vertex-Attribute definieren (Position = Location 0)
        GL20.glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        // Aufräumen
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        MemoryUtil.memFree(buffer);
    }

    private void createHUDShader(){
        // Vertex-Shader-Code
        String vertexShaderSource =
                "#version 330 core\n" +
                        "layout(location = 0) in vec2 aPos;\n" +
                        "uniform vec2 uResolution;\n" +
                        "void main() {\n" +
                        "    vec2 screenPos = aPos + (uResolution * 0.5);\n" +
                        "    vec2 ndc = (screenPos / uResolution) * 2.0 - 1.0;\n" +
                        "    gl_Position = vec4(ndc, 0.0, 1.0);\n" +
                        "}";

        // Fragment-Shader-Code
        String fragmentShaderSource =
                "#version 330 core\n" +
                        "out vec4 FragColor;\n" +
                        "void main() {\n" +
                        "    FragColor = vec4(1.0, 1.0, 1.0, 0.7);\n" +
                        "}";

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        // Shader-Programm erstellen
        hudShaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(hudShaderProgram, vertexShader);
        GL20.glAttachShader(hudShaderProgram, fragmentShader);
        GL20.glLinkProgram(hudShaderProgram);
        checkCompileErrors(hudShaderProgram, "PROGRAM");

        // Einzelne Shader löschen (sind jetzt im Programm drin)
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }
    private void checkCompileErrors(int shader, String type) {
        int success;
        if (type.equals("PROGRAM")) {
            success = GL20.glGetProgrami(shader, GL20.GL_LINK_STATUS);
            if (success == GL11.GL_FALSE) {
                String infoLog = GL20.glGetProgramInfoLog(shader);
                System.err.println("ERROR::PROGRAM_LINKING_ERROR\n" + infoLog);
            }
        } else {
            success = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
            if (success == GL11.GL_FALSE) {
                String infoLog = GL20.glGetShaderInfoLog(shader);
                System.err.println("ERROR::SHADER_COMPILATION_ERROR of type: " + type + "\n" + infoLog);
            }
        }
    }

}
