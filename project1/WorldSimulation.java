import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class WorldSimulation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WorldFrame();
        });
    }
}

class WorldFrame extends JFrame {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    private static final int CELL_SIZE = 25;
    
    private World world;
    private WorldPanel worldPanel;
    private JLabel statusLabel;
    private JLabel humanStatusLabel;
    private JLabel strengthLabel;
    
    public WorldFrame() {
        super("World Simulation - Oskar Putz ID: 203475");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        world = new World();
        initializeWorld();
        
        setLayout(new BorderLayout());
        
        worldPanel = new WorldPanel();
        add(worldPanel, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusLabel = new JLabel("No collisions yet.");
        humanStatusLabel = new JLabel("Ability status: OFF");
        strengthLabel = new JLabel("Current strength: 5");
        statusPanel.add(humanStatusLabel);
        statusPanel.add(strengthLabel);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        JPanel controlsPanel = new JPanel();
        JButton upButton = new JButton("Up");
        JButton downButton = new JButton("Down");
        JButton leftButton = new JButton("Left");
        JButton rightButton = new JButton("Right");
        JButton abilityButton = new JButton("Activate Ability");
        JButton nextTurnButton = new JButton("Next Turn");
        
        upButton.addActionListener(e -> moveHuman(0, -1));
        downButton.addActionListener(e -> moveHuman(0, 1));
        leftButton.addActionListener(e -> moveHuman(-1, 0));
        rightButton.addActionListener(e -> moveHuman(1, 0));
        abilityButton.addActionListener(e -> activateHumanAbility());
        nextTurnButton.addActionListener(e -> nextTurn());
        
        controlsPanel.add(upButton);
        controlsPanel.add(downButton);
        controlsPanel.add(leftButton);
        controlsPanel.add(rightButton);
        controlsPanel.add(abilityButton);
        controlsPanel.add(nextTurnButton);
        add(controlsPanel, BorderLayout.NORTH);
        
        // Set up keyboard shortcuts
        KeyStroke upKey = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        KeyStroke downKey = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        KeyStroke leftKey = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        KeyStroke rightKey = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        KeyStroke pKey = KeyStroke.getKeyStroke(KeyEvent.VK_P, 0);
        KeyStroke spaceKey = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
        
        getRootPane().registerKeyboardAction(e -> moveHuman(0, -1), upKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> moveHuman(0, 1), downKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> moveHuman(-1, 0), leftKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> moveHuman(1, 0), rightKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> activateHumanAbility(), pKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> nextTurn(), spaceKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        setSize(WIDTH * CELL_SIZE + 16, HEIGHT * CELL_SIZE + 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void moveHuman(int dx, int dy) {
        if (world.human != null) {
            world.human.setDirection(dx, dy);
            nextTurn();
        }
    }
    
    private void activateHumanAbility() {
        if (world.human != null) {
            world.human.activateAbility();
            updateStatus();
            worldPanel.repaint();
        }
    }
    
    private void nextTurn() {
        world.makeTurn();
        updateStatus();
        worldPanel.repaint();
    }
    
    private void updateStatus() {
        statusLabel.setText(world.lastCollisionMessage);
        humanStatusLabel.setText("Ability status: " + (world.human.abilityDuration > 0 ? "ON" : "OFF"));
        strengthLabel.setText("Current strength: " + world.human.strength);
    }
    
    private void initializeWorld() {
        Random random = new Random();
        world.human = new Human(0, 0, world);
        world.addOrganism(world.human);
        
        for (int i = 0; i < 2; i++) world.addOrganism(new Sheep(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new Wolf(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new Fox(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new Turtle(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new Antelope(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new Grass(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new SowThistle(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new Guarana(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new Belladonna(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
        for (int i = 0; i < 2; i++) world.addOrganism(new Hogweed(random.nextInt(WIDTH), random.nextInt(HEIGHT), world));
    }
    
    class WorldPanel extends JPanel {
        private static final HashMap<Character, Color> COLORS = new HashMap<>();
        
        static {
            COLORS.put('H', Color.BLUE);
            COLORS.put('S', Color.WHITE);
            COLORS.put('W', Color.DARK_GRAY);
            COLORS.put('F', Color.ORANGE);
            COLORS.put('T', Color.GREEN.darker().darker());
            COLORS.put('A', Color.YELLOW);
            COLORS.put('G', Color.GREEN);
            COLORS.put('s', Color.GREEN.darker());
            COLORS.put('g', Color.RED.darker());
            COLORS.put('b', Color.MAGENTA);
            COLORS.put('h', Color.RED);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw grid
            g.setColor(Color.LIGHT_GRAY);
            for (int x = 0; x <= WIDTH; x++) {
                g.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, HEIGHT * CELL_SIZE);
            }
            for (int y = 0; y <= HEIGHT; y++) {
                g.drawLine(0, y * CELL_SIZE, WIDTH * CELL_SIZE, y * CELL_SIZE);
            }
            
            // Draw organisms
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    if (world.grid[y][x] != null) {
                        char symbol = world.grid[y][x].draw();
                        g.setColor(COLORS.getOrDefault(symbol, Color.BLACK));
                        g.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
                        g.setColor(Color.BLACK);
                        g.drawString(String.valueOf(symbol), x * CELL_SIZE + CELL_SIZE/2 - 4, y * CELL_SIZE + CELL_SIZE/2 + 4);
                    }
                }
            }
        }
    }
}

class World {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    ArrayList<Organism> organisms = new ArrayList<>();
    ArrayList<Organism> newOrganisms = new ArrayList<>();
    Organism[][] grid = new Organism[HEIGHT][WIDTH];
    String lastCollisionMessage = "No collisions yet.";
    Human human = null;
    
    public void addOrganism(Organism org) {
        if (grid[org.y][org.x] == null) { // Only add if cell is empty
            organisms.add(org);
            grid[org.y][org.x] = org;
        }
    }
    
    public void queueOrganism(Organism org) {
        newOrganisms.add(org);
    }
    
    public void makeTurn() {
        Collections.sort(organisms, (a, b) -> {
            if (a.initiative != b.initiative) return b.initiative - a.initiative;
            return b.age - a.age;
        });
        
        for (Organism org : new ArrayList<>(organisms)) {
            if (org.isAlive()) org.action();
        }
        
        organisms.removeIf(o -> !o.isAlive());
        for (Organism org : newOrganisms) {
            addOrganism(org);
        }
        newOrganisms.clear();
    }
}

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

abstract class Animal extends Organism {
    private boolean move = true;
    private static final Random random = new Random();
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    public Animal(int str, int init, int x, int y, World w) {
        super(str, init, x, y, w);
    }
    
    public abstract Animal clone(int x, int y);
    
    @Override
    public void action() {
        int dir = random.nextInt(4);
        int nx = x, ny = y;
        
        if (dir == 0 && y > 0) ny--;
        else if (dir == 1 && y < HEIGHT - 1) ny++;
        else if (dir == 2 && x > 0) nx--;
        else if (dir == 3 && x < WIDTH - 1) nx++;
        
        if (world.grid[ny][nx] != null) collision(world.grid[ny][nx]);
        if (alive && move) {
            world.grid[y][x] = null;
            x = nx;
            y = ny;
            world.grid[y][x] = this;
        }
        age++;
        move = true;
    }
    
    @Override
    public void collision(Organism other) {
        if (other.getClass() == this.getClass()) {
            move = false;
            int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
            ArrayList<int[]> directions = new ArrayList<>();
            for (int[] dir : dirs) directions.add(dir);
            Collections.shuffle(directions);
            
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world.grid[ny][nx] == null) {
                    Animal child = clone(nx, ny);
                    child.move = false;
                    world.queueOrganism(child);
                    world.lastCollisionMessage = draw() + " reproduced at (" + nx + ", " + ny + ")";
                    break;
                }
            }
            return;
        }
        
        if (other.draw() == 'h') {
            world.lastCollisionMessage = draw() + " was killed by Hogweed at (" + other.x + ", " + other.y + ")";
            alive = false;
            world.grid[y][x] = null;
            return;
        }
        
        if (other.draw() == 'A') {
            int escapeChance = random.nextInt(2);
            if (escapeChance == 0) {
                int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}};
                ArrayList<int[]> directions = new ArrayList<>();
                for (int[] dir : dirs) directions.add(dir);
                Collections.shuffle(directions);
                
                for (int[] dir : directions) {
                    int nx = other.x + dir[0];
                    int ny = other.y + dir[1];
                    if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world.grid[ny][nx] == null) {
                        world.grid[other.y][other.x] = null;
                        other.x = nx;
                        other.y = ny;
                        world.grid[ny][nx] = other;
                        world.lastCollisionMessage = other.draw() + " escaped from " + draw() + " at (" + other.x + ", " + other.y + ")";
                        return;
                    }
                }
            }
        }
        
        if (other.draw() == 'T') {
            if (strength < 5) {
                move = false;
                world.lastCollisionMessage = draw() + " was deflected by Turtle at (" + other.x + ", " + other.y + ")";
                return;
            }
        }
        
        if (other.draw() == 'g') {
            strength += 3;
            world.lastCollisionMessage = draw() + " ate Guarana and gained 3 strength!";
            other.alive = false;
            world.grid[other.y][other.x] = null;
            return;
        }
        
        if (other.strength > strength) {
            world.lastCollisionMessage = other.draw() + " killed " + draw() + " at (" + x + ", " + y + ")";
            alive = false;
            world.grid[y][x] = null;
        } else {
            world.lastCollisionMessage = draw() + " killed " + other.draw() + " at (" + other.x + ", " + other.y + ")";
            other.alive = false;
            world.grid[other.y][other.x] = null;
        }
    }
    
    @Override
    public char draw() {
        return 'A';
    }
}

abstract class Plant extends Organism {
    private static final Random random = new Random();
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    public Plant(int str, int x, int y, World w) {
        super(str, 0, x, y, w);
    }
    
    @Override
    public void action() {
        if (random.nextInt(10) == 0) {
            int nx = x + (random.nextInt(3) - 1);
            int ny = y + (random.nextInt(3) - 1);
            if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world.grid[ny][nx] == null) {
                try {
                    world.queueOrganism(this.getClass().getDeclaredConstructor(int.class, int.class, World.class)
                            .newInstance(nx, ny, world));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        age++;
    }
    
    @Override
    public void collision(Organism other) {
        alive = false;
    }
    
    @Override
    public char draw() {
        return 'P';
    }
}

class Human extends Animal {
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
            abilityDuration = 6;
            abilityCooldown = 11;
            world.lastCollisionMessage = "Ability activated!";
            strength += 6;
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
            if (abilityDuration == 0) strength -= 6;
        }
        
        age++;
    }
    
    @Override
    public char draw() {
        return 'H';
    }
}

class Sheep extends Animal {
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

class Fox extends Animal {
    private static final Random random = new Random();
    
    public Fox(int x, int y, World w) {
        super(3, 7, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'F';
    }
    
    @Override
    public Animal clone(int x, int y) {
        return new Fox(x, y, world);
    }
    
    @Override
    public void action() {
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        ArrayList<int[]> dirs = new ArrayList<>();
        for (int[] dir : directions) dirs.add(dir);
        Collections.shuffle(dirs);
        
        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            
            if (nx >= 0 && ny >= 0 && nx < 40 && ny < 20) {
                Organism target = world.grid[ny][nx];
                if (target == null || target.strength <= strength) {
                    if (target != null) collision(target);
                    if (alive) {
                        world.grid[y][x] = null;
                        x = nx;
                        y = ny;
                        world.grid[y][x] = this;
                    }
                    break;
                }
            }
        }
        age++;
    }
}

class Turtle extends Animal {
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

class Antelope extends Animal {
    private static final Random random = new Random();
    
    public Antelope(int x, int y, World w) {
        super(4, 4, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'A';
    }
    
    @Override
    public Animal clone(int x, int y) {
        return new Antelope(x, y, world);
    }
    
    @Override
    public void action() {
        int[][] directions = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        ArrayList<int[]> dirs = new ArrayList<>();
        for (int[] dir : directions) dirs.add(dir);
        Collections.shuffle(dirs);
        
        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            
            if (nx >= 0 && ny >= 0 && nx < 40 && ny < 20) {
                Organism target = world.grid[ny][nx];
                if (target != null) collision(target);
                if (alive) {
                    world.grid[y][x] = null;
                    x = nx;
                    y = ny;
                    world.grid[y][x] = this;
                }
                break;
            }
        }
        age++;
    }
}

class Grass extends Plant {
    public Grass(int x, int y, World w) {
        super(0, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'G';
    }
}

class SowThistle extends Plant {
    private static final Random random = new Random();
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    public SowThistle(int x, int y, World w) {
        super(0, x, y, w);
    }
    
    @Override
    public char draw() {
        return 's';
    }
    
    @Override
    public void action() {
        for (int i = 0; i < 3; i++) {
            if (random.nextInt(10) == 0) {
                int nx = x + (random.nextInt(3) - 1);
                int ny = y + (random.nextInt(3) - 1);
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && world.grid[ny][nx] == null) {
                    world.queueOrganism(new SowThistle(nx, ny, world));
                }
            }
        }
        age++;
    }
}

class Guarana extends Plant {
    public Guarana(int x, int y, World w) {
        super(0, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'g';
    }
}

class Belladonna extends Plant {
    public Belladonna(int x, int y, World w) {
        super(99, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'b';
    }
}

class Hogweed extends Plant {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    
    public Hogweed(int x, int y, World w) {
        super(10, x, y, w);
    }
    
    @Override
    public char draw() {
        return 'h';
    }
    
    @Override
    public void action() {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT) {
                    Organism target = world.grid[ny][nx];
                    if (target != null && target instanceof Animal) {
                        target.alive = false;
                        world.grid[ny][nx] = null;
                        world.lastCollisionMessage = draw() + " killed " + target.draw() +
                                                     " at (" + nx + ", " + ny + ")";
                    }
                }
            }
        }
        super.action();
    }
}