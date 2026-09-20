package gui;

import commands.Command;
import commands.CommandRegistry;
import parser.CommandParser;
import parser.ParseException;
import parser.ParsedCommand;
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


public class EmulatorFrame extends JFrame {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 500;
    private static final int FONT_SIZE = 13;
    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private final JButton sendButton = new JButton(">");
    private final CommandParser parser = new CommandParser();
    private final CommandRegistry registry = new CommandRegistry();

    public EmulatorFrame() {
        super();
        setTitle(buildTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildOutputArea(), BorderLayout.CENTER);
        getContentPane().add(buildInputPanel(), BorderLayout.SOUTH);
        printLine("Эмулятор запущен. Введите команду.");
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

    private String promptText() {

        return userName() + "@" + hostName() + ":~$ ";
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
}