package commands;

import java.util.List;
import java.util.stream.Collectors;

public class CdCommand implements Command {

    @Override
    public String execute(List<String> args) {
        if (args.size() > 1) {
            return "cd: слишком много аргументов";
        }
        return "cd " + args.stream()
                .map(a -> "\"" + a + "\"")
                .collect(Collectors.joining(", "));
    }
}