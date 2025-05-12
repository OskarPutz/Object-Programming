import java.util.ArrayList;
import java.util.Collections;

class World {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    ArrayList<Organism> organisms = new ArrayList<>();
    ArrayList<Organism> newOrganisms = new ArrayList<>();
    Organism[][] grid = new Organism[HEIGHT][WIDTH];
    String lastCollisionMessage = "No collisions yet.";
    Human human = null;
    
    public void addOrganism(Organism org) {
        if (grid[org.y][org.x] == null) { // Only add if cell is empty
            organisms.add(org);
            grid[org.y][org.x] = org;
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
        for (Organism org : newOrganisms) {
            addOrganism(org);
        }
        newOrganisms.clear();
    }
}
