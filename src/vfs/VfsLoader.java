package vfs;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Загружает виртуальную файловую систему из директории на диске.
 * <p>
 * Директория читается один раз и целиком помещается в память. Исходные файлы
 * не изменяются, не перемещаются и не распаковываются — загрузчик только
 * читает их.
 */
public class VfsLoader {

    private static final String ROOT_NAME = "";

    /**
     * Читает директорию и строит дерево в памяти.
     *
     * @param source директория на диске — источник VFS
     * @return загруженная файловая система
     * @throws VfsException если путь не существует, не является директорией
     *                      или её содержимое не удалось прочитать
     */
    public VirtualFileSystem load(Path source) throws VfsException {
        if (!Files.exists(source)) {
            throw new VfsException("источник не найден: " + source);
        }
        if (!Files.isDirectory(source)) {
            throw new VfsException("источник не является директорией: " + source);
        }

        VfsNode root = VfsNode.directory(ROOT_NAME);
        fill(root, source);
        return new VirtualFileSystem(root, source.getFileName().toString());
    }

    /**
     * Рекурсивно переносит содержимое директории в узел дерева.
     *
     * @param node      узел-каталог, который наполняется
     * @param directory соответствующая директория на диске
     * @throws VfsException если каталог или файл не удалось прочитать
     */
    private void fill(VfsNode node, Path directory) throws VfsException {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(directory)) {
            for (Path entry : entries) {
                String name = entry.getFileName().toString();
                if (Files.isDirectory(entry)) {
                    VfsNode child = VfsNode.directory(name);
                    node.addChild(child);
                    fill(child, entry);
                } else {
                    node.addChild(VfsNode.file(name, readText(entry)));
                }
            }
        } catch (IOException e) {
            throw new VfsException("не удалось прочитать каталог: " + directory);
        }
    }

    /**
     * Читает файл как текст в кодировке UTF-8.
     *
     * @param file файл на диске
     * @return содержимое файла
     * @throws VfsException если файл содержит не текст в UTF-8 или не читается
     */
    private String readText(Path file) throws VfsException {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            throw new VfsException("неверный формат файла, ожидается текст UTF-8: " + file);
        } catch (IOException e) {
            throw new VfsException("не удалось прочитать файл: " + file);
        }
    }
}
