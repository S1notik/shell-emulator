import gui.EmulatorFrame;

import javax.swing.SwingUtilities;

/**
 * Точка входа. Запускает окно эмулятора в потоке диспетчеризации событий Swing.
 */
public class Main {

    /**
     * @param args аргументы командной строки; на этом этапе не используются
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmulatorFrame frame = new EmulatorFrame();
            frame.setVisible(true);
        });
    }
}