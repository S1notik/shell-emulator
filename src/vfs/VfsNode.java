package vfs;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Узел виртуальной файловой системы — файл или каталог.
 * <p>
 * У файла есть содержимое и нет потомков, у каталога наоборот. Потомки
 * хранятся в {@link LinkedHashMap}, поэтому порядок обхода совпадает
 * с порядком загрузки с диска.
 * <p>
 * Узел знает своего родителя: это позволяет подниматься по дереву
 * и собирать полный путь, не обходя его от корня.
 */
public class VfsNode {

    private final String name;
    private final boolean directory;
    private final String content;
    private final Map<String, VfsNode> children;

    private VfsNode parent;

    private VfsNode(String name, boolean directory, String content) {
        this.name = name;
        this.directory = directory;
        this.content = content;
        this.children = directory ? new LinkedHashMap<>() : null;
    }

    /**
     * Создаёт каталог без потомков.
     *
     * @param name имя каталога
     * @return новый узел-каталог
     */
    public static VfsNode directory(String name) {
        return new VfsNode(name, true, null);
    }

    /**
     * Создаёт файл с содержимым.
     *
     * @param name    имя файла
     * @param content содержимое файла
     * @return новый узел-файл
     */
    public static VfsNode file(String name, String content) {
        return new VfsNode(name, false, content);
    }

    /**
     * Добавляет потомка в каталог и запоминает себя его родителем.
     *
     * @param child добавляемый узел
     * @throws IllegalStateException если узел не является каталогом
     */
    public void addChild(VfsNode child) {
        if (!directory) {
            throw new IllegalStateException("Не каталог: " + name);
        }
        child.parent = this;
        children.put(child.getName(), child);
    }

    /**
     * Возвращает потомка по имени.
     *
     * @param childName имя потомка
     * @return узел или {@code null}, если потомка с таким именем нет
     */
    public VfsNode getChild(String childName) {
        return directory ? children.get(childName) : null;
    }

    /**
     * @return потомки каталога в порядке добавления; для файла — пустой список
     */
    public Collection<VfsNode> getChildren() {
        return directory ? children.values() : Collections.emptyList();
    }

    /**
     * @return имя узла без пути
     */
    public String getName() {
        return name;
    }

    /**
     * @return {@code true}, если узел является каталогом
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * @return содержимое файла или {@code null} для каталога
     */
    public String getContent() {
        return content;
    }

    /**
     * @return родительский каталог или {@code null} для корня
     */
    public VfsNode getParent() {
        return parent;
    }
}
