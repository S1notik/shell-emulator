package commands;

import vfs.VfsNode;
import vfs.VirtualFileSystem;

import java.util.List;

/**
 * Команда {@code cd}: меняет текущий каталог VFS.
 * <p>
 * Без аргумента переходит в корень. Путь может быть абсолютным
 * или относительным и содержать {@code .} и {@code ..}.
 */
public class CdCommand extends VfsCommand {

    /**
     * @param vfs загруженная файловая система или {@code null}
     */
    public CdCommand(VirtualFileSystem vfs) {
        super("cd", vfs);
    }

    @Override
    protected String run(VirtualFileSystem vfs, List<String> args) {
        if (args.size() > 1) {
            return error("слишком много аргументов");
        }
        if (args.isEmpty()) {
            vfs.setCurrent(vfs.getRoot());
            return "";
        }
        String path = args.get(0);
        VfsNode node = vfs.resolve(path);
        if (node == null) {
            return error(path + ": нет такого файла или каталога");
        }
        if (!node.isDirectory()) {
            return error(path + ": не является каталогом");
        }
        vfs.setCurrent(node);
        return "";
    }
}
