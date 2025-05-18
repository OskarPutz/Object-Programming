import java.util.Random;

abstract class Plant extends Organism {
    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    public Plant(int str, int x, int y, World w) {
        super(str, 0, x, y, w);
    }
    
    @Override
    public void action() {
        if (random.nextInt(10) == 0) {
            int nx = x + (random.nextInt(3) - 1);
            int ny = y + (random.nextInt(3) - 1);
            if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world.grid[ny][nx] == null) {
                try {
                    world.queueOrganism(this.getClass().getDeclaredConstructor(int.class, int.class, World.class)
                            .newInstance(nx, ny, world));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        age++;
    }
    
    @Override
    public void collision(Organism other) {
        alive = false;
    }
    
    @Override
    public char draw() {
        return 'P';
    }
}
