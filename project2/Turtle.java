import java.util.Random;

class Turtle extends Animal {
    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    
    public Turtle(int x, int y, World w) {
        super(2, 1, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'T';
    }
    
    @Override
    public Animal clone(int x, int y) {
        return new Turtle(x, y, world);
    }
    
    @Override
    public void action() {
        int chance = random.nextInt(4);
        if (chance == 0) {
            super.action();
        }
        age++;
    }
}
