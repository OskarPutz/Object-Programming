class Wolf extends Animal {
    public Wolf(int x, int y, World w) {
        super(9, 5, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'W';
    }
    
    @Override
    public Animal clone(int x, int y) {
        return new Wolf(x, y, world);
    }
}
