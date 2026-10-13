import config.ArgsException;
import config.ArgsParser;
import config.EmulatorConfig;
import gui.EmulatorFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Точка входа. Разбирает параметры командной строки и запускает окно
 * эмулятора в потоке диспетчеризации событий Swing.
 */
public class Main {

    /**
     * @param args параметры запуска: {@code --vfs <путь>} и {@code --script <путь>},
     *             оба необязательны
     */
    public static void main(String[] args) {
        EmulatorConfig config;
        try {
            config = new ArgsParser().parse(args);
        } catch (ArgsException e) {
            reportArgsError(e.getMessage());
            return;
        }

        SwingUtilities.invokeLater(() -> {
            EmulatorFrame frame = new EmulatorFrame(config);
            frame.setVisible(true);
            frame.runStartScript();
        });
    }

    /**
     * Сообщает о неверных параметрах запуска и напоминает, как их задавать.
     *
     * @param message описание ошибки
     */
    private static void reportArgsError(String message) {
        String text = message + System.lineSeparator()
                + "Использование: java -cp out Main [--vfs <путь>] [--script <путь>]";
        System.err.println(text);
        JOptionPane.showMessageDialog(null, text, "Ошибка параметров запуска",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}