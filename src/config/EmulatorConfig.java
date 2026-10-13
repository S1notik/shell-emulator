package config;

import java.nio.file.Path;

/**
 * Параметры запуска эмулятора, заданные в командной строке.
 * Любой из путей может отсутствовать — тогда соответствующее поле равно
 * {@code null}, а метод проверки возвращает {@code false}.
 */
public class EmulatorConfig {

    private final Path vfsPath;
    private final Path startScriptPath;

    /**
     * @param vfsPath         путь к физическому расположению VFS или {@code null}
     * @param startScriptPath путь к стартовому скрипту или {@code null}
     */
    public EmulatorConfig(Path vfsPath, Path startScriptPath) {
        this.vfsPath = vfsPath;
        this.startScriptPath = startScriptPath;
    }

    /**
     * @return путь к расположению VFS или {@code null}, если параметр не задан
     */
    public Path getVfsPath() {
        return vfsPath;
    }

    /**
     * @return путь к стартовому скрипту или {@code null}, если параметр не задан
     */
    public Path getStartScriptPath() {
        return startScriptPath;
    }

    /**
     * @return {@code true}, если путь к VFS задан
     */
    public boolean hasVfs() {
        return vfsPath != null;
    }

    /**
     * @return {@code true}, если путь к стартовому скрипту задан
     */
    public boolean hasStartScript() {
        return startScriptPath != null;
    }
}