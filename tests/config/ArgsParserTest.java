package config;

import java.nio.file.Path;

/**
 * Проверки разбора параметров командной строки для {@link ArgsParser}.
 * <p>
 * Тесты написаны без внешних библиотек, чтобы запуск требовал только JDK.
 * Класс печатает результат каждой проверки и завершается с ненулевым кодом,
 * если хотя бы одна из них не прошла.
 */
public final class ArgsParserTest {

    private static final ArgsParser PARSER = new ArgsParser();

    private static int failed = 0;

    private ArgsParserTest() {
    }

    public static void main(String[] args) throws ArgsException {
        noArguments();
        onlyVfs();
        onlyScript();
        bothOptions();
        orderDoesNotMatter();
        lastValueWins();
        missingValueThrows();
        unknownOptionThrows();

        if (failed > 0) {
            System.out.println("Провалено проверок: " + failed);
            System.exit(1);
        }
        System.out.println("Все проверки пройдены");
    }

    private static void noArguments() throws ArgsException {
        EmulatorConfig config = PARSER.parse(new String[] {});
        check("без параметров: VFS не задан", false, config.hasVfs());
        check("без параметров: скрипт не задан", false, config.hasStartScript());
    }

    private static void onlyVfs() throws ArgsException {
        EmulatorConfig config = PARSER.parse(new String[] {"--vfs", "vfs/minimal"});
        check("только --vfs: VFS задан", true, config.hasVfs());
        check("только --vfs: путь к VFS", Path.of("vfs/minimal"), config.getVfsPath());
        check("только --vfs: скрипт не задан", false, config.hasStartScript());
    }

    private static void onlyScript() throws ArgsException {
        EmulatorConfig config = PARSER.parse(new String[] {"--script", "scripts/start.txt"});
        check("только --script: скрипт задан", true, config.hasStartScript());
        check("только --script: путь к скрипту",
                Path.of("scripts/start.txt"), config.getStartScriptPath());
        check("только --script: VFS не задан", false, config.hasVfs());
    }

    private static void bothOptions() throws ArgsException {
        EmulatorConfig config = PARSER.parse(
                new String[] {"--vfs", "vfs/minimal", "--script", "scripts/start.txt"});
        check("оба параметра: путь к VFS", Path.of("vfs/minimal"), config.getVfsPath());
        check("оба параметра: путь к скрипту",
                Path.of("scripts/start.txt"), config.getStartScriptPath());
    }

    private static void orderDoesNotMatter() throws ArgsException {
        EmulatorConfig config = PARSER.parse(
                new String[] {"--script", "scripts/start.txt", "--vfs", "vfs/minimal"});
        check("порядок не важен: путь к VFS", Path.of("vfs/minimal"), config.getVfsPath());
        check("порядок не важен: путь к скрипту",
                Path.of("scripts/start.txt"), config.getStartScriptPath());
    }

    private static void lastValueWins() throws ArgsException {
        EmulatorConfig config = PARSER.parse(
                new String[] {"--vfs", "vfs/first", "--vfs", "vfs/second"});
        check("повторный параметр перезаписывает предыдущий",
                Path.of("vfs/second"), config.getVfsPath());
    }

    private static void missingValueThrows() {
        boolean thrown = false;
        try {
            PARSER.parse(new String[] {"--vfs"});
        } catch (ArgsException e) {
            thrown = true;
        }
        check("параметр без значения бросает исключение", true, thrown);
    }

    private static void unknownOptionThrows() {
        boolean thrown = false;
        try {
            PARSER.parse(new String[] {"--unknown", "value"});
        } catch (ArgsException e) {
            thrown = true;
        }
        check("неизвестный параметр бросает исключение", true, thrown);
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
