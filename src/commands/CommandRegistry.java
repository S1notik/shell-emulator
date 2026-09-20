package commands;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry() {
        register("cd", new CdCommand());
        register("ls", new LsCommand());
        register("exit", new ExitCommand());
    }

    public boolean contains(String name) {
        return commands.containsKey(name);
    }

    public Command get(String name) {
        return commands.get(name);
    }

    public void register(String name, Command command) {
        commands.put(name, command);
    }
}