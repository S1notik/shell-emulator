package commands;

import vfs.VirtualFileSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Сопоставляет имена команд их реализациям.
 * При создании регистрирует команды, доступные эмулятору.
 */
public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Создаёт реестр и регистрирует доступные команды.
     *
     * @param vfs загруженная файловая система или {@code null}, если VFS не задана;
     *            передаётся командам, которым она нужна
     */
    public CommandRegistry(VirtualFileSystem vfs) {
        register("cd", new CdCommand());
        register("ls", new LsCommand());
        register("exit", new ExitCommand());
        register("vfs-tree", new VfsTreeCommand(vfs));
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
