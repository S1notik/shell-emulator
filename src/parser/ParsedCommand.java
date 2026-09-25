package parser;

import java.util.List;

/**
 * Результат разбора введённой строки: имя команды и её аргументы.
 * Создаётся парсером и передаётся команде на выполнение.
 */
public class ParsedCommand {

    private final String name;
    private final List<String> args;

    /**
     * @param name имя команды — первый токен строки
     * @param args аргументы команды, без её имени
     */
    public ParsedCommand(String name, List<String> args) {
        this.name = name;
        this.args = args;
    }

    /**
     * @return список аргументов; пустой, если аргументов не было
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * @return имя команды
     */
    public String getName() {
        return name;
    }

    /**
     * Проверяет, был ли ввод пустым.
     *
     * @return {@code true}, если имя команды пустое
     */
    public boolean isEmpty() {
        return name.isEmpty();
    }
}
