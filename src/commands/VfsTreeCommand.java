package commands;

import vfs.VfsNode;
import vfs.VirtualFileSystem;

import java.util.Iterator;
import java.util.List;

/**
 * Служебная команда {@code vfs-tree}: печатает дерево загруженной VFS.
 * <p>
 * Нужна, чтобы убедиться, что директория с диска действительно считана
 * в память. Состояние VFS не меняет.
 */
public class VfsTreeCommand implements Command {

    private static final String BRANCH = "├── ";
    private static final String LAST_BRANCH = "└── ";
    private static final String TRUNK = "│   ";
    private static final String GAP = "    ";

    private final VirtualFileSystem vfs;

    /**
     * @param vfs загруженная файловая система или {@code null}, если VFS не задана
     */
    public VfsTreeCommand(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    @Override
    public String execute(List<String> args) {
        if (!args.isEmpty()) {
            return "vfs-tree: команда не принимает аргументов";
        }
        if (vfs == null) {
            return "vfs-tree: VFS не загружена";
        }
        StringBuilder out = new StringBuilder("/");
        appendChildren(out, vfs.getRoot(), "");
        return out.toString();
    }

    /**
     * Дописывает потомков узла с отступами, рисующими ветви дерева.
     *
     * @param out    накопитель вывода
     * @param node   каталог, чьи потомки выводятся
     * @param indent отступ, накопленный на пути от корня
     */
    private void appendChildren(StringBuilder out, VfsNode node, String indent) {
        Iterator<VfsNode> it = node.getChildren().iterator();
        while (it.hasNext()) {
            VfsNode child = it.next();
            boolean last = !it.hasNext();
            out.append(System.lineSeparator())
                    .append(indent)
                    .append(last ? LAST_BRANCH : BRANCH)
                    .append(child.getName());
            if (child.isDirectory()) {
                out.append("/");
                appendChildren(out, child, indent + (last ? GAP : TRUNK));
            }
        }
    }
}
