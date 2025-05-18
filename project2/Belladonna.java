import java.io.Serializable;
class Belladonna extends Plant implements Serializable {
    private static final long serialVersionUID = 1L;
    public Belladonna(int x, int y, World w) {
        super(99, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'b';
    }
}
