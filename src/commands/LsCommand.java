package commands;

import vfs.VfsNode;
import vfs.VirtualFileSystem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда {@code ls}: выводит содержимое каталога.
 * <p>
 * Без аргумента показывает текущий каталог, с аргументом — указанный.
 * Каталоги помечаются косой чертой в конце имени, записи выводятся
 * в алфавитном порядке. Если путь ведёт к файлу, выводится его имя.
 */
public class LsCommand extends VfsCommand {

    private static final String ENTRY_SEPARATOR = "  ";
    private static final String OPTION_PREFIX = "-";

    /**
     * @param vfs загруженная файловая система или {@code null}
     */
    public LsCommand(VirtualFileSystem vfs) {
        super("ls", vfs);
    }

    @Override
    protected String run(VirtualFileSystem vfs, List<String> args) {
        if (args.size() > 1) {
            return error("слишком много аргументов");
        }
        String path = args.isEmpty() ? "" : args.get(0);
        if (path.startsWith(OPTION_PREFIX)) {
            return error("неизвестная опция " + path);
        }
        VfsNode node = vfs.resolve(path);
        if (node == null) {
            return error(path + ": нет такого файла или каталога");
        }
        if (!node.isDirectory()) {
            return node.getName();
        }
        return node.getChildren().stream()
                .map(child -> child.isDirectory() ? child.getName() + "/" : child.getName())
                .sorted()
                .collect(Collectors.joining(ENTRY_SEPARATOR));
    }
}
