import java.io.Serializable;

class Sheep extends Animal implements Serializable {
    private static final long serialVersionUID = 1L;
    public Sheep(int x, int y, World w) {
        super(4, 4, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'S';
    }
    
    @Override
    public Animal clone(int x, int y) {
        return new Sheep(x, y, world);
    }
}
