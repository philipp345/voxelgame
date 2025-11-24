public class Game {
    private boolean running = true;

    public void start() {

        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            update(deltaTime);
            render();
        }
    }

    private void update(double deltaTime) {
        //add game logic later
    }

    private void render() {
        //add graphics logic later
    }

}
