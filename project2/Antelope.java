import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Antelope extends Animal {
    private static final Random random = new Random();
    
    public Antelope(int x, int y, World w) {
        super(4, 4, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'A';
    }
    
    @Override
    public Animal clone(int x, int y) {
        return new Antelope(x, y, world);
    }
    
    @Override
    public void action() {
        int[][] directions = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        ArrayList<int[]> dirs = new ArrayList<>();
        for (int[] dir : directions) dirs.add(dir);
        Collections.shuffle(dirs);
        
        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            
            if (nx >= 0 && ny >= 0 && nx < 40 && ny < 20) {
                Organism target = world.grid[ny][nx];
                if (target != null) collision(target);
                if (alive) {
                    world.grid[y][x] = null;
                    x = nx;
                    y = ny;
                    world.grid[y][x] = this;
                }
                break;
            }
        }
        age++;
    }
}
