package vfs;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Виртуальная файловая система в оперативной памяти.
 * <p>
 * Хранит корень дерева и текущий каталог. Все операции выполняются только
 * над узлами в памяти: исходная директория на диске читается один раз
 * при загрузке и больше не затрагивается.
 * <p>
 * Пути разбираются по тем же правилам, что в UNIX-оболочке: {@code /} ведёт
 * от корня, остальные — от текущего каталога, {@code .} означает текущий
 * каталог, {@code ..} — родительский.
 */
public class VirtualFileSystem {

    private static final String SEPARATOR = "/";
    private static final String CURRENT = ".";
    private static final String PARENT = "..";

    private final VfsNode root;
    private final String sourceName;

    private VfsNode current;

    /**
     * @param root       корневой каталог дерева
     * @param sourceName имя директории на диске, из которой загружена VFS
     */
    public VirtualFileSystem(VfsNode root, String sourceName) {
        this.root = root;
        this.sourceName = sourceName;
        this.current = root;
    }

    /**
     * @return корневой каталог
     */
    public VfsNode getRoot() {
        return root;
    }

    /**
     * @return текущий каталог
     */
    public VfsNode getCurrent() {
        return current;
    }

    /**
     * @return имя директории на диске, из которой загружена VFS
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Делает указанный каталог текущим.
     *
     * @param node каталог
     * @throws IllegalArgumentException если узел не является каталогом
     */
    public void setCurrent(VfsNode node) {
        if (!node.isDirectory()) {
            throw new IllegalArgumentException("Не каталог: " + node.getName());
        }
        this.current = node;
    }

    /**
     * Находит узел по пути.
     * <p>
     * Пустой путь означает текущий каталог. Повторяющиеся разделители
     * игнорируются, {@code ..} в корне оставляет на месте — как в UNIX.
     *
     * @param path путь, абсолютный или относительный
     * @return найденный узел или {@code null}, если такого пути нет
     */
    public VfsNode resolve(String path) {
        VfsNode node = path.startsWith(SEPARATOR) ? root : current;
        for (String part : path.split(SEPARATOR)) {
            if (part.isEmpty() || CURRENT.equals(part)) {
                continue;
            }
            if (PARENT.equals(part)) {
                node = node.getParent() == null ? node : node.getParent();
                continue;
            }
            if (!node.isDirectory()) {
                return null;
            }
            node = node.getChild(part);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /**
     * Строит абсолютный путь до узла, поднимаясь к корню по родителям.
     *
     * @param node узел дерева
     * @return путь вида {@code /docs/report.txt}; для корня — {@code /}
     */
    public String pathOf(VfsNode node) {
        Deque<String> parts = new ArrayDeque<>();
        for (VfsNode step = node; step.getParent() != null; step = step.getParent()) {
            parts.addFirst(step.getName());
        }
        return parts.isEmpty() ? SEPARATOR : SEPARATOR + String.join(SEPARATOR, parts);
    }

    /**
     * @return абсолютный путь текущего каталога
     */
    public String currentPath() {
        return pathOf(current);
    }

    /**
     * Считает количество каталогов в дереве, не считая корень.
     *
     * @return число каталогов
     */
    public int countDirectories() {
        return count(root, true) - 1;
    }

    /**
     * Считает количество файлов в дереве.
     *
     * @return число файлов
     */
    public int countFiles() {
        return count(root, false);
    }

    private int count(VfsNode node, boolean directories) {
        int total = node.isDirectory() == directories ? 1 : 0;
        for (VfsNode child : node.getChildren()) {
            total += count(child, directories);
        }
        return total;
    }
}
