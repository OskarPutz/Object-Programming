import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

class World implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    ArrayList<Organism> organisms = new ArrayList<>();
    ArrayList<Organism> newOrganisms = new ArrayList<>();
    Organism[][] grid = new Organism[HEIGHT][WIDTH];
    String lastCollisionMessage = "No collisions yet.";
    Human human = null;
    
    public void addOrganism(Organism org) {
        // Check bounds before adding
        if (org.y >= 0 && org.y < HEIGHT && org.x >= 0 && org.x < WIDTH) {
            if (grid[org.y][org.x] == null) { // Only add if cell is empty
                organisms.add(org);
                grid[org.y][org.x] = org;
            }
        }
    }
    
    public void queueOrganism(Organism org) {
        newOrganisms.add(org);
    }
    
    public void makeTurn() {
        Collections.sort(organisms, (a, b) -> {
            if (a.initiative != b.initiative) return b.initiative - a.initiative;
            return b.age - a.age;
        });
        
        for (Organism org : new ArrayList<>(organisms)) {
            if (org.isAlive()) org.action();
        }
        
        organisms.removeIf(o -> !o.isAlive());
        
        // Update grid references for all organisms
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = null;
            }
        }
        
        for (Organism org : organisms) {
            if (org.y >= 0 && org.y < HEIGHT && org.x >= 0 && org.x < WIDTH) {
                grid[org.y][org.x] = org;
            }
        }
        
        for (Organism org : newOrganisms) {
            addOrganism(org);
        }
        newOrganisms.clear();
    }
    
    // Utility methods to get dimensions
    public int getWidth() {
        return WIDTH;
    }
    
    public int getHeight() {
        return HEIGHT;
    }
}
