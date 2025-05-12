abstract class Organism {
    int strength, initiative, age;
    int x, y;
    World world;
    boolean alive = true;
    
    public Organism(int str, int init, int x, int y, World w) {
        this.strength = str;
        this.initiative = init;
        this.x = x;
        this.y = y;
        this.world = w;
        this.age = 0;
    }
    
    public abstract void action();
    public abstract void collision(Organism other);
    public abstract char draw();
    
    public boolean isAlive() {
        return alive;
    }
}
