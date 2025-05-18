import java.io.Serializable;

class Grass extends Plant implements Serializable {
    private static final long serialVersionUID = 1L;
    public Grass(int x, int y, World w) {
        super(0, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'G';
    }
}
