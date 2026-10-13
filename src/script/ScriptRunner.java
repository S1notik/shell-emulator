package script;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * Выполняет стартовый скрипт эмулятора — текстовый файл с командами,
 * по одной в строке.
 * <p>
 * Класс не знает ни про окно, ни про сами команды: что делать с каждой
 * строкой, задаётся обработчиком, переданным в конструктор.
 */
public class ScriptRunner {

    private final Consumer<String> lineHandler;

    /**
     * @param lineHandler обработчик, вызываемый для каждой строки скрипта
     */
    public ScriptRunner(Consumer<String> lineHandler) {
        this.lineHandler = lineHandler;
    }

    /**
     * Читает скрипт и передаёт его строки обработчику по очереди.
     *
     * @param path путь к файлу скрипта
     * @throws IOException если файл не найден или не читается
     */
    public void run(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            lineHandler.accept(line);
        }
    }
}