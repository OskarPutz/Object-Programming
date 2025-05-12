import javax.swing.*;

public class WorldSimulation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WorldFrame();
        });
    }
}