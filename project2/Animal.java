import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

abstract class Animal extends Organism {
    private boolean move = true;
    private static final Random random = new Random();
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    public Animal(int str, int init, int x, int y, World w) {
        super(str, init, x, y, w);
    }
    
    public abstract Animal clone(int x, int y);
    
    @Override
    public void action() {
        int dir = random.nextInt(4);
        int nx = x, ny = y;
        
        if (dir == 0 && y > 0) ny--;
        else if (dir == 1 && y < HEIGHT - 1) ny++;
        else if (dir == 2 && x > 0) nx--;
        else if (dir == 3 && x < WIDTH - 1) nx++;
        
        if (world.grid[ny][nx] != null) collision(world.grid[ny][nx]);
        if (alive && move) {
            world.grid[y][x] = null;
            x = nx;
            y = ny;
            world.grid[y][x] = this;
        }
        age++;
        move = true;
    }
    
    @Override
    public void collision(Organism other) {
        if (other.getClass() == this.getClass()) {
            move = false;
            int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
            ArrayList<int[]> directions = new ArrayList<>();
            for (int[] dir : dirs) directions.add(dir);
            Collections.shuffle(directions);
            
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world.grid[ny][nx] == null) {
                    Animal child = clone(nx, ny);
                    child.move = false;
                    world.queueOrganism(child);
                    world.lastCollisionMessage = draw() + " reproduced at (" + nx + ", " + ny + ")";
                    break;
                }
            }
            return;
        }
        
        if (other.draw() == 'h') {
            world.lastCollisionMessage = draw() + " was killed by Hogweed at (" + other.x + ", " + other.y + ")";
            alive = false;
            world.grid[y][x] = null;
            return;
        }
        
        if (other.draw() == 'A') {
            int escapeChance = random.nextInt(2);
            if (escapeChance == 0) {
                int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}};
                ArrayList<int[]> directions = new ArrayList<>();
                for (int[] dir : dirs) directions.add(dir);
                Collections.shuffle(directions);
                
                for (int[] dir : directions) {
                    int nx = other.x + dir[0];
                    int ny = other.y + dir[1];
                    if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world.grid[ny][nx] == null) {
                        world.grid[other.y][other.x] = null;
                        other.x = nx;
                        other.y = ny;
                        world.grid[ny][nx] = other;
                        world.lastCollisionMessage = other.draw() + " escaped from " + draw() + " at (" + other.x + ", " + other.y + ")";
                        return;
                    }
                }
            }
        }
        
        if (other.draw() == 'T') {
            if (strength < 5) {
                move = false;
                world.lastCollisionMessage = draw() + " was deflected by Turtle at (" + other.x + ", " + other.y + ")";
                return;
            }
        }
        
        if (other.draw() == 'g') {
            strength += 3;
            world.lastCollisionMessage = draw() + " ate Guarana and gained 3 strength!";
            other.alive = false;
            world.grid[other.y][other.x] = null;
            return;
        }
        
        if (other.strength > strength) {
            world.lastCollisionMessage = other.draw() + " killed " + draw() + " at (" + x + ", " + y + ")";
            alive = false;
            world.grid[y][x] = null;
        } else {
            world.lastCollisionMessage = draw() + " killed " + other.draw() + " at (" + other.x + ", " + other.y + ")";
            other.alive = false;
            world.grid[other.y][other.x] = null;
        }
    }
    
    @Override
    public char draw() {
        return 'A';
    }
}
