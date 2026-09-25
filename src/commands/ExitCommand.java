package commands;

import java.util.List;

public class ExitCommand implements Command {
    @Override
    public String execute(List<String> args) {
        if (!args.isEmpty()) {
            return "exit: команда не принимает аргументов";
        }
        System.exit(0);
        return "";
    }
}