import java.util.Random;
import java.io.Serializable;

class SowThistle extends Plant implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    public SowThistle(int x, int y, World w) {
        super(0, x, y, w);
    }
    
    @Override
    public char draw() {
        return 's';
    }
    
    @Override
    public void action() {
        for (int i = 0; i < 3; i++) {
            if (random.nextInt(10) == 0) {
                int nx = x + (random.nextInt(3) - 1);
                int ny = y + (random.nextInt(3) - 1);
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world.grid[ny][nx] == null) {
                    world.queueOrganism(new SowThistle(nx, ny, world));
                }
            }
        }
        age++;
    }
}
