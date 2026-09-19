package parser;

import java.util.List;

public class ParsedCommand {

    private final String name;
    private final List<String> args;

    public ParsedCommand(String name, List<String> args) {
        this.name = name;
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return name.isEmpty();
    }
}