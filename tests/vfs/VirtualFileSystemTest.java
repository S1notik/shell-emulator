package vfs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Проверки загрузки VFS и разрешения путей.
 * <p>
 * Дерево для проверок создаётся во временном каталоге и удаляется после
 * прогона. Тесты написаны без внешних библиотек, чтобы запуск требовал
 * только JDK.
 */
public final class VirtualFileSystemTest {

    private static final VfsLoader LOADER = new VfsLoader();

    private static int failed = 0;

    private VirtualFileSystemTest() {
    }

    public static void main(String[] args) throws Exception {
        Path sample = createSample();
        try {
            VirtualFileSystem vfs = LOADER.load(sample);
            loadsWholeTree(vfs);
            resolvesRelativePath(vfs);
            resolvesAbsolutePath(vfs);
            resolvesDotAndParent(vfs);
            parentOfRootStaysAtRoot(vfs);
            missingPathReturnsNull(vfs);
            doesNotDescendIntoFile(vfs);
            readsFileContent(vfs);
            buildsAbsolutePath(vfs);
            countsNodes(vfs);
            changesCurrentDirectory(vfs);
        } finally {
            deleteRecursively(sample);
        }

        missingSourceThrows();
        fileAsSourceThrows();

        if (failed > 0) {
            System.out.println("Провалено проверок: " + failed);
            System.exit(1);
        }
        System.out.println("Все проверки пройдены");
    }

    private static void loadsWholeTree(VirtualFileSystem vfs) {
        check("в корне три записи", 3, vfs.getRoot().getChildren().size());
        check("корень — каталог", true, vfs.getRoot().isDirectory());
    }

    private static void resolvesRelativePath(VirtualFileSystem vfs) {
        VfsNode node = vfs.resolve("docs/manual.txt");
        check("относительный путь найден", true, node != null);
        check("имя найденного файла", "manual.txt", node.getName());
        check("найденный узел — файл", false, node.isDirectory());
    }

    private static void resolvesAbsolutePath(VirtualFileSystem vfs) {
        VfsNode node = vfs.resolve("/docs/inner/deep.txt");
        check("абсолютный путь найден", true, node != null);
        check("файл на третьем уровне", "deep.txt", node.getName());
    }

    private static void resolvesDotAndParent(VirtualFileSystem vfs) {
        VfsNode node = vfs.resolve("/docs/./inner/../manual.txt");
        check("точка и две точки в пути", "manual.txt", node.getName());
    }

    private static void parentOfRootStaysAtRoot(VirtualFileSystem vfs) {
        VfsNode node = vfs.resolve("/../../");
        check("подъём выше корня остаётся в корне", true, node == vfs.getRoot());
    }

    private static void missingPathReturnsNull(VirtualFileSystem vfs) {
        check("несуществующий путь даёт null", true, vfs.resolve("docs/нет.txt") == null);
    }

    private static void doesNotDescendIntoFile(VirtualFileSystem vfs) {
        check("спуск внутрь файла даёт null",
                true, vfs.resolve("readme.txt/дальше") == null);
    }

    private static void readsFileContent(VirtualFileSystem vfs) {
        VfsNode node = vfs.resolve("readme.txt");
        check("содержимое файла прочитано", "корень", node.getContent().trim());
    }

    private static void buildsAbsolutePath(VirtualFileSystem vfs) {
        VfsNode node = vfs.resolve("docs/inner/deep.txt");
        check("построенный путь", "/docs/inner/deep.txt", vfs.pathOf(node));
        check("путь корня", "/", vfs.pathOf(vfs.getRoot()));
    }

    private static void countsNodes(VirtualFileSystem vfs) {
        check("каталогов без корня", 2, vfs.countDirectories());
        check("файлов", 4, vfs.countFiles());
    }

    private static void changesCurrentDirectory(VirtualFileSystem vfs) {
        check("текущий каталог при старте", "/", vfs.currentPath());
        vfs.setCurrent(vfs.resolve("/docs"));
        check("текущий каталог после перехода", "/docs", vfs.currentPath());
        check("относительный путь от нового текущего",
                "deep.txt", vfs.resolve("inner/deep.txt").getName());
        vfs.setCurrent(vfs.getRoot());
    }

    private static void missingSourceThrows() {
        boolean thrown = false;
        try {
            LOADER.load(Path.of("vfs-samples", "no-such-dir"));
        } catch (VfsException e) {
            thrown = true;
        }
        check("несуществующий источник бросает исключение", true, thrown);
    }

    private static void fileAsSourceThrows() throws IOException {
        Path file = Files.createTempFile("vfs-source", ".txt");
        boolean thrown = false;
        try {
            LOADER.load(file);
        } catch (VfsException e) {
            thrown = true;
        } finally {
            Files.deleteIfExists(file);
        }
        check("файл вместо директории бросает исключение", true, thrown);
    }

    /**
     * Создаёт во временном каталоге дерево из трёх уровней.
     *
     * @return корень созданного дерева
     * @throws IOException если каталог не удалось создать
     */
    private static Path createSample() throws IOException {
        Path root = Files.createTempDirectory("vfs-test");
        Files.writeString(root.resolve("readme.txt"), "корень", StandardCharsets.UTF_8);
        Files.writeString(root.resolve("notes.txt"), "заметки", StandardCharsets.UTF_8);
        Path docs = Files.createDirectory(root.resolve("docs"));
        Files.writeString(docs.resolve("manual.txt"), "руководство", StandardCharsets.UTF_8);
        Path inner = Files.createDirectory(docs.resolve("inner"));
        Files.writeString(inner.resolve("deep.txt"), "глубоко", StandardCharsets.UTF_8);
        return root;
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var entries = Files.list(path)) {
                for (Path entry : entries.toList()) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    private static void check(String label, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.println("OK   " + label);
        } else {
            failed++;
            System.out.println("FAIL " + label + ": ожидалось <" + expected
                    + ">, получено <" + actual + ">");
        }
    }
}
