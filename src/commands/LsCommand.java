package commands;


import java.util.List;
import java.util.stream.Collectors;

public class LsCommand implements Command {

    @Override
    public String execute(List<String> args) {
        return "ls " + args.stream()
                .map(a -> "\"" + a + "\"")
                .collect(Collectors.joining(", "));
    }

}