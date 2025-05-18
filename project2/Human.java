import java.io.Serializable;

class Human extends Animal implements Serializable {
    private static final long serialVersionUID = 1L;
    private int dx = 0, dy = 0;
    int abilityCooldown = 0;
    int abilityDuration = 0;
    
    public Human(int x, int y, World w) {
        super(5, 4, x, y, w);
    }
    
    @Override
    public Animal clone(int x, int y) {
        return new Human(x, y, world);
    }
    
    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    public void activateAbility() {
        if (abilityCooldown == 0) {
            abilityDuration = 5;
            abilityCooldown = 10;
            world.lastCollisionMessage = "Ability activated!";
            strength += 5;
        }
    }
    
    @Override
    public void action() {
        int nx = x + dx, ny = y + dy;
        dx = dy = 0;
        
        if (nx >= 0 && ny >= 0 && nx < 40 && ny < 20) {
            if (world.grid[ny][nx] != null && (nx != x || ny != y)) {
                collision(world.grid[ny][nx]);
            }
            if (alive) {
                world.grid[y][x] = null;
                x = nx;
                y = ny;
                world.grid[y][x] = this;
            }
        }
        
        if (abilityCooldown > 0) abilityCooldown--;
        if (abilityDuration > 0) {
            abilityDuration--;
            strength--;
        }
        
        age++;
    }
    
    @Override
    public char draw() {
        return 'H';
    }
}
