import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

class Fox extends Animal implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public Fox(int x, int y, World w) {
        super(3, 7, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'F';
    }
    
    @Override
    public Animal clone(int x, int y) {
        return new Fox(x, y, world);
    }
    
    @Override
    public void action() {
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        ArrayList<int[]> dirs = new ArrayList<>();
        for (int[] dir : directions) dirs.add(dir);
        Collections.shuffle(dirs);
        
        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            
            if (nx >= 0 && ny >= 0 && nx < 40 && ny < 20) {
                Organism target = world.grid[ny][nx];
                if (target == null || target.strength <= strength) {
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
        }
        age++;
    }
}
