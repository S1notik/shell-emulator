package commands;

import vfs.VirtualFileSystem;

import java.util.List;

/**
 * Основа для команд, работающих с виртуальной файловой системой.
 * <p>
 * Проверяет, что VFS загружена, и только после этого передаёт управление
 * самой команде. Благодаря этому каждая команда не повторяет одну и ту же
 * проверку, а сообщение об отсутствии VFS у всех одинаковое.
 */
public abstract class VfsCommand implements Command {

    private final String name;
    private final VirtualFileSystem vfs;

    /**
     * @param name имя команды, которым начинаются её сообщения об ошибках
     * @param vfs  загруженная файловая система или {@code null}
     */
    protected VfsCommand(String name, VirtualFileSystem vfs) {
        this.name = name;
        this.vfs = vfs;
    }

    @Override
    public final String execute(List<String> args) {
        if (vfs == null) {
            return name + ": VFS не загружена";
        }
        return run(vfs, args);
    }

    /**
     * Выполняет команду над загруженной VFS.
     *
     * @param vfs  файловая система, гарантированно не {@code null}
     * @param args аргументы команды
     * @return текст для вывода в терминал
     */
    protected abstract String run(VirtualFileSystem vfs, List<String> args);

    /**
     * Формирует сообщение об ошибке с префиксом из имени команды.
     *
     * @param message текст ошибки
     * @return строка вида {@code имя: текст}
     */
    protected String error(String message) {
        return name + ": " + message;
    }
}
