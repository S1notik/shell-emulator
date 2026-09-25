package commands;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Заглушка команды {@code ls}: выводит своё имя и полученные аргументы.
 * Работа с файловой системой появится на следующих этапах.
 */
public class LsCommand implements Command {

    @Override
    public String execute(List<String> args) {
        return "ls " + args.stream()
                .map(a -> "\"" + a + "\"")
                .collect(Collectors.joining(", "));
    }
}
