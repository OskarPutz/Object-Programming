class Hogweed extends Plant {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    public Hogweed(int x, int y, World w) {
        super(10, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'h';
    }
    
    @Override
    public void action() {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
                    Organism target = world.grid[ny][nx];
                    if (target != null && target instanceof Animal) {
                        target.alive = false;
                        world.grid[ny][nx] = null;
                        world.lastCollisionMessage = draw() + " killed " + target.draw() +
                                                     " at (" + nx + ", " + ny + ")";
                    }
                }
            }
        }
        super.action();
    }
}
