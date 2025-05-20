class Guarana extends Plant {
    public Guarana(int x, int y, World w) {
        super(0, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'g';
    }
}
