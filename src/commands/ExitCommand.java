package commands;

import java.util.List;

/**
 * Завершает работу эмулятора. Аргументов не принимает.
 */
public class ExitCommand implements Command {

    @Override
    public String execute(List<String> args) {
        if (!args.isEmpty()) {
            return "exit: команда не принимает аргументов";
        }
        System.exit(0);
        return ""; // недостижимо: выполнение завершается строкой выше
    }
}
