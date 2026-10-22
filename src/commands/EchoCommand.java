package commands;

import java.util.List;

/**
 * Команда {@code echo}: выводит аргументы через пробел.
 * <p>
 * Кавычки разбирает парсер ещё до вызова команды, поэтому
 * {@code echo "a   b"} выведет пробелы внутри кавычек как есть.
 */
public class EchoCommand implements Command {

    @Override
    public String execute(List<String> args) {
        return String.join(" ", args);
    }
}
