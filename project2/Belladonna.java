class Belladonna extends Plant {
    public Belladonna(int x, int y, World w) {
        super(99, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'b';
    }
}
