package gui;

import commands.Command;
import commands.CommandRegistry;
import config.EmulatorConfig;
import parser.CommandParser;
import parser.ParseException;
import parser.ParsedCommand;
import script.ScriptRunner;
import vfs.VfsException;
import vfs.VfsLoader;
import vfs.VirtualFileSystem;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Окно эмулятора оболочки ОС.
 * <p>
 * Состоит из области вывода, имитирующей терминал, и нижней панели ввода
 * с полем команды и кнопкой отправки. Команда выполняется как по нажатию
 * Enter, так и по клику на кнопку. Заголовок окна и приглашение к вводу
 * формируются из реальных данных ОС, в которой запущен эмулятор.
 */
public class EmulatorFrame extends JFrame {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 500;
    private static final int FONT_SIZE = 13;
    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private final JButton sendButton = new JButton(">");
    private final CommandParser parser = new CommandParser();
    private final CommandRegistry registry;

    private VirtualFileSystem vfs;
    private static final String NOT_SET = "не задан";
    private static final String HOME = "~";
    private final EmulatorConfig config;
    /**
     * Собирает окно, выводит параметры запуска, загружает VFS и печатает
     * приглашение к вводу.
     *
     * @param config параметры запуска, разобранные из командной строки
     */
    public EmulatorFrame(EmulatorConfig config) {
        super();
        this.config = config;
        setTitle(buildTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildOutputArea(), BorderLayout.CENTER);
        getContentPane().add(buildInputPanel(), BorderLayout.SOUTH);
        printLine("Эмулятор запущен. Введите команду.");
        printConfig();
        String vfsReport = loadVfs();
        this.registry = new CommandRegistry(vfs);
        printLine(vfsReport);
        print(promptText());
    }

    private JScrollPane buildOutputArea() {
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
        output.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return new JScrollPane(output);
    }

    private JPanel buildInputPanel() {
        JLabel label = new JLabel("cmd");
        label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 6));
        input.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
        input.addActionListener(this::onSubmit);
        sendButton.setToolTipText("Выполнить команду");
        sendButton.addActionListener(this::onSubmit);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panel.add(label, BorderLayout.WEST);
        panel.add(input, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        return panel;
    }

    private void onSubmit(ActionEvent event) {
        String line = input.getText();
        input.setText("");
        printLine(line);
        runLine(line);
        print(promptText());
    }

    private void runLine(String line) {
        ParsedCommand parsed;
        try {
            parsed = parser.parse(line);
        } catch (ParseException e) {
            printLine("Ошибка разбора: " + e.getMessage());
            return;
        }
        if (parsed.isEmpty()) {
            return;
        }
        if (!registry.contains(parsed.getName())) {
            printLine(parsed.getName() + ": команда не найдена");
            return;
        }
        Command command = registry.get(parsed.getName());
        String result = command.execute(parsed.getArgs());
        if (!result.isEmpty()) {
            printLine(result);
        }
    }

    private void printLine(String text) {
        output.append(text + System.lineSeparator());
        output.setCaretPosition(output.getDocument().getLength());
    }

    private void print(String text) {
        output.append(text);
        output.setCaretPosition(output.getDocument().getLength());
    }

    /**
     * Формирует приглашение вида {@code user@host:~/docs$}.
     * Корень VFS обозначается как {@code ~}, вложенные каталоги — путём от него.
     *
     * @return текст приглашения
     */
    private String promptText() {
        return userName() + "@" + hostName() + ":" + currentDirectory() + "$ ";
    }

    private String currentDirectory() {
        if (vfs == null || vfs.getCurrent() == vfs.getRoot()) {
            return HOME;
        }
        return HOME + vfs.currentPath();
    }

    private String buildTitle() {
        return "Эмулятор - [" + userName() + "@" + hostName() + "]";
    }

    private String userName() {
        return System.getProperty("user.name", "user");
    }

    private String hostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }

    /**
     * Загружает VFS, если её путь задан в параметрах запуска.
     *
     * @return строка о результате загрузки для вывода в окно
     */
    private String loadVfs() {
        if (!config.hasVfs()) {
            return "VFS не загружена: путь не задан.";
        }
        try {
            vfs = new VfsLoader().load(config.getVfsPath());
            return "VFS загружена из " + config.getVfsPath()
                    + ": каталогов " + vfs.countDirectories()
                    + ", файлов " + vfs.countFiles() + ".";
        } catch (VfsException e) {
            return "Ошибка загрузки VFS: " + e.getMessage();
        }
    }

    /**
     * Выполняет стартовый скрипт, если он задан в параметрах запуска.
     * Вызывается после того, как окно показано.
     */
    public void runStartScript() {
        if (!config.hasStartScript()) {
            return;
        }
        ScriptRunner runner = new ScriptRunner(this::runScriptLine);
        try {
            runner.run(config.getStartScriptPath());
        } catch (IOException e) {
            printLine("Ошибка выполнения скрипта: не удалось прочитать "
                    + config.getStartScriptPath());
            print(promptText());
        }
    }

    private void printConfig() {
        printLine("Параметры запуска:");
        printLine("  VFS:    " + (config.hasVfs() ? config.getVfsPath() : NOT_SET));
        printLine("  Скрипт: " + (config.hasStartScript()
                ? config.getStartScriptPath() : NOT_SET));
    }

    private void runScriptLine(String line) {
        printLine(line);
        runLine(line);
        print(promptText());
    }
}