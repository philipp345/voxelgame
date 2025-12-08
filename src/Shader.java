import org.lwjgl.opengl.GL20;

public class Shader {

    private final int programId;

    public Shader(String vertexSource, String fragmentSource) {
        // Shader kompilieren
        int vertexShaderId = compileShader(GL20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShaderId = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource);

        // Shader-Programm erstellen
        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);
        GL20.glLinkProgram(programId);

        checkLinkErrors(programId);

        // Shader-Objekte werden nicht mehr benötigt
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
    }

    private int compileShader(int type, String source) {
        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);

        checkCompileErrors(shaderId, type == GL20.GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT");

        return shaderId;
    }

    private void checkCompileErrors(int shaderId, String type) {
        int status = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS);
        if (status == GL20.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shaderId);
            throw new RuntimeException(type + " SHADER COMPILATION FAILED:\n" + log);
        }
    }

    private void checkLinkErrors(int programId) {
        int status = GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS);
        if (status == GL20.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(programId);
            throw new RuntimeException("SHADER PROGRAM LINKING FAILED:\n" + log);
        }
    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public int getId() {
        return programId;
    }
}
