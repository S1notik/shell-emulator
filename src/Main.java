import gui.EmulatorFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmulatorFrame frame = new EmulatorFrame();
            frame.setVisible(true);
        });
    }
}