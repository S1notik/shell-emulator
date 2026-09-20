package commands;

import java.util.List;
import java.util.stream.Collectors;

public class CdCommand implements Command {

    @Override
    public String execute(List<String> args) {
        return "cd " + args.stream()
                .map(a -> "\"" + a + "\"")
                .collect(Collectors.joining(", "));
    }
}