package commands;

import vfs.VfsNode;
import vfs.VirtualFileSystem;

import java.util.List;

/**
 * Команда {@code tail}: выводит последние строки файла из VFS.
 * <p>
 * Форматы вызова: {@code tail файл} — последние десять строк,
 * {@code tail -n число файл} — указанное число строк.
 */
public class TailCommand extends VfsCommand {

    private static final int DEFAULT_LINES = 10;
    private static final int SHORT_FORM_ARGS = 1;
    private static final int FULL_FORM_ARGS = 3;
    private static final String LINES_OPTION = "-n";
    private static final String USAGE = "использование: tail [-n число] файл";

    /**
     * @param vfs загруженная файловая система или {@code null}
     */
    public TailCommand(VirtualFileSystem vfs) {
        super("tail", vfs);
    }

    @Override
    protected String run(VirtualFileSystem vfs, List<String> args) {
        if (args.size() == SHORT_FORM_ARGS) {
            return tail(vfs, args.get(0), DEFAULT_LINES);
        }
        if (args.size() == FULL_FORM_ARGS && LINES_OPTION.equals(args.get(0))) {
            Integer count = parseCount(args.get(1));
            if (count == null) {
                return error("неверное число строк: " + args.get(1));
            }
            return tail(vfs, args.get(2), count);
        }
        return error(USAGE);
    }

    /**
     * Находит файл и возвращает его последние строки.
     *
     * @param vfs   файловая система
     * @param path  путь к файлу
     * @param count сколько строк вывести
     * @return строки файла или сообщение об ошибке
     */
    private String tail(VirtualFileSystem vfs, String path, int count) {
        VfsNode node = vfs.resolve(path);
        if (node == null) {
            return error(path + ": нет такого файла или каталога");
        }
        if (node.isDirectory()) {
            return error(path + ": это каталог");
        }
        List<String> lines = node.getContent().lines().toList();
        int from = Math.max(0, lines.size() - count);
        return String.join(System.lineSeparator(), lines.subList(from, lines.size()));
    }

    /**
     * Разбирает число строк.
     *
     * @param text значение параметра {@code -n}
     * @return неотрицательное число или {@code null}, если значение неверно
     */
    private Integer parseCount(String text) {
        try {
            int value = Integer.parseInt(text);
            return value < 0 ? null : value;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
