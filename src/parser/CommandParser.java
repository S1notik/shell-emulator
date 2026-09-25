package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Разбирает введённую строку на имя команды и список аргументов.
 * <p>
 * Аргумент может быть заключён в одинарные или двойные кавычки — тогда
 * пробелы внутри него не считаются разделителями. Сами кавычки являются
 * разметкой и в результат не попадают. Повторяющиеся пробелы между
 * аргументами игнорируются.
 */
public class CommandParser {

    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';

    /**
     * Разбирает строку на команду и аргументы.
     *
     * @param line строка, введённая пользователем
     * @return разобранная команда; при пустом вводе — с пустым именем
     * @throws ParseException если кавычка осталась незакрытой
     */
    public ParsedCommand parse(String line) throws ParseException {
        if (line.isBlank()) {
            return new ParsedCommand("", new ArrayList<>());
        }
        List<String> tokens = tokenize(line);
        String command = tokens.get(0);
        List<String> args = new ArrayList<>();
        for (int i = 1; i < tokens.size(); i++) {
            args.add(tokens.get(i));
        }
        return new ParsedCommand(command, args);
    }

    /**
     * Разбивает строку на токены, учитывая кавычки.
     *
     * @param line непустая строка, введённая пользователем
     * @return список токенов; первый из них — имя команды
     * @throws ParseException если кавычка осталась незакрытой
     */
    private List<String> tokenize(String line) throws ParseException {
        StringBuilder current = new StringBuilder();
        List<String> tokens = new ArrayList<>();
        Character quote = null;
        boolean started = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quote != null) {
                if (c == quote) {
                    quote = null;
                    started = true;
                } else {
                    current.append(c);
                }
                continue;
            }
            if (c == DOUBLE_QUOTE || c == SINGLE_QUOTE) {
                quote = c;
                continue;
            }
            if (Character.isWhitespace(c)) {
                if (started) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                    started = false;
                }
                continue;
            }
            current.append(c);
            started = true;
        }
        if (quote != null) {
            throw new ParseException("Незакрытая кавычка: " + line);
        }
        if (started) {
            tokens.add(current.toString());
        }
        return tokens;
    }
}
