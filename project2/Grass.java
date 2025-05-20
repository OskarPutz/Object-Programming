class Grass extends Plant {
    public Grass(int x, int y, World w) {
        super(0, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'G';
    }
}
