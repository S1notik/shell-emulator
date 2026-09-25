package commands;

import java.util.HashMap;
import java.util.Map;

/**
 * Сопоставляет имена команд их реализациям.
 * При создании регистрирует команды, доступные эмулятору.
 */
public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Создаёт реестр и регистрирует в нём команды {@code cd}, {@code ls}
     * и {@code exit}.
     */
    public CommandRegistry() {
        register("cd", new CdCommand());
        register("ls", new LsCommand());
        register("exit", new ExitCommand());
    }

    /**
     * Проверяет, зарегистрирована ли команда с таким именем.
     *
     * @param name имя команды
     * @return {@code true}, если команда найдена
     */
    public boolean contains(String name) {
        return commands.containsKey(name);
    }

    /**
     * Возвращает реализацию команды.
     *
     * @param name имя команды
     * @return реализация или {@code null}, если команда не зарегистрирована
     */
    public Command get(String name) {
        return commands.get(name);
    }

    /**
     * Регистрирует команду под указанным именем, заменяя прежнюю, если она была.
     *
     * @param name    имя, по которому команда вызывается
     * @param command реализация команды
     */
    public void register(String name, Command command) {
        commands.put(name, command);
    }
}
