import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

class WorldFrame extends JFrame {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;
    private static final int CELL_SIZE = 30;
    
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
        strengthLabel = new JLabel("Current strength: " + world.human.strength);
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
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        
        upButton.addActionListener(e -> moveHuman(0, -1));
        downButton.addActionListener(e -> moveHuman(0, 1));
        leftButton.addActionListener(e -> moveHuman(-1, 0));
        rightButton.addActionListener(e -> moveHuman(1, 0));
        abilityButton.addActionListener(e -> activateHumanAbility());
        nextTurnButton.addActionListener(e -> nextTurn());
        saveButton.addActionListener(e -> saveWorld());
        loadButton.addActionListener(e -> loadWorld());
        
        controlsPanel.add(upButton);
        controlsPanel.add(downButton);
        controlsPanel.add(leftButton);
        controlsPanel.add(rightButton);
        controlsPanel.add(abilityButton);
        controlsPanel.add(nextTurnButton);
        controlsPanel.add(saveButton);
        controlsPanel.add(loadButton);
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
        private void saveWorld() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save World");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(world);
                statusLabel.setText("World saved successfully to " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving world: " + e.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
        private void loadWorld() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load World");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                world = (World) ois.readObject();
                statusLabel.setText("World loaded successfully from " + file.getName());
                updateStatus();
                worldPanel.repaint();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading world: " + e.getMessage(), 
                    "Load Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
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
            int width2 = 40;
            int height2 = 20;
            // Draw grid
            g.setColor(Color.LIGHT_GRAY);
            for (int x = 0; x <= width2; x++) {
                g.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, height2 * CELL_SIZE);
            }
            for (int y = 0; y <= height2; y++) {
                g.drawLine(0, y * CELL_SIZE, width2 * CELL_SIZE, y * CELL_SIZE);
            }
            
            // Draw organisms
            for (int y = 0; y < height2; y++) {
                for (int x = 0; x < width2; x++) {
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
