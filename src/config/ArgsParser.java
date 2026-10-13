package config;

import java.nio.file.Path;

/**
 * Разбирает параметры командной строки в {@link EmulatorConfig}.
 * <p>
 * Поддерживаются два именованных параметра, оба необязательны:
 * {@code --vfs <путь>} — расположение VFS,
 * {@code --script <путь>} — стартовый скрипт.
 * Порядок параметров значения не имеет.
 */
public class ArgsParser {

    private static final String VFS_OPTION = "--vfs";
    private static final String SCRIPT_OPTION = "--script";

    /**
     * Разбирает массив аргументов.
     *
     * @param args аргументы, полученные методом {@code main}
     * @return конфигурация запуска; незаданные параметры равны {@code null}
     * @throws ArgsException если параметр неизвестен или у него нет значения
     */
    public EmulatorConfig parse(String[] args) throws ArgsException {
        Path vfsPath = null;
        Path startScriptPath = null;

        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if (VFS_OPTION.equals(option)) {
                vfsPath = Path.of(valueOf(args, i));
                i++;
            } else if (SCRIPT_OPTION.equals(option)) {
                startScriptPath = Path.of(valueOf(args, i));
                i++;
            } else {
                throw new ArgsException("Неизвестный параметр: " + option);
            }
        }

        return new EmulatorConfig(vfsPath, startScriptPath);
    }

    /**
     * Возвращает значение параметра — элемент, следующий за его именем.
     *
     * @param args  массив аргументов
     * @param index позиция имени параметра
     * @return значение параметра
     * @throws ArgsException если значение отсутствует
     */
    private String valueOf(String[] args, int index) throws ArgsException {
        if (index + 1 >= args.length) {
            throw new ArgsException("У параметра " + args[index] + " не указано значение");
        }
        return args[index + 1];
    }
}