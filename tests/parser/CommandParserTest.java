package parser;

import java.util.List;

/**
 * Проверки разбора строки для {@link CommandParser}.
 * <p>
 * Тесты написаны без внешних библиотек, чтобы запуск требовал только JDK.
 * Класс печатает результат каждой проверки и завершается с ненулевым кодом,
 * если хотя бы одна из них не прошла.
 */
public final class CommandParserTest {

    private static final CommandParser PARSER = new CommandParser();

    private static int failed = 0;

    private CommandParserTest() {
    }

    public static void main(String[] args) throws ParseException {
        commandWithoutArgs();
        commandWithSeveralArgs();
        doubleQuotedArgument();
        singleQuotedArgument();
        repeatedSpacesIgnored();
        blankLineIsEmpty();
        emptyQuotedArgument();
        unterminatedQuoteThrows();

        if (failed > 0) {
            System.out.println("Провалено проверок: " + failed);
            System.exit(1);
        }
        System.out.println("Все проверки пройдены");
    }

    private static void commandWithoutArgs() throws ParseException {
        ParsedCommand result = PARSER.parse("ls");
        check("имя команды без аргументов", "ls", result.getName());
        check("аргументов нет", 0, result.getArgs().size());
    }

    private static void commandWithSeveralArgs() throws ParseException {
        ParsedCommand result = PARSER.parse("cd docs downloads");
        check("имя команды с аргументами", "cd", result.getName());
        check("число аргументов", 2, result.getArgs().size());
        check("первый аргумент", "docs", result.getArgs().get(0));
        check("второй аргумент", "downloads", result.getArgs().get(1));
    }

    private static void doubleQuotedArgument() throws ParseException {
        List<String> args = PARSER.parse("cd \"My Documents\"").getArgs();
        check("двойные кавычки дают один аргумент", 1, args.size());
        check("кавычки убраны, пробел сохранён", "My Documents", args.get(0));
    }

    private static void singleQuotedArgument() throws ParseException {
        List<String> args = PARSER.parse("ls 'one two' three").getArgs();
        check("одинарные кавычки: число аргументов", 2, args.size());
        check("одинарные кавычки: первый аргумент", "one two", args.get(0));
        check("одинарные кавычки: второй аргумент", "three", args.get(1));
    }

    private static void repeatedSpacesIgnored() throws ParseException {
        List<String> args = PARSER.parse("ls    -la").getArgs();
        check("повторяющиеся пробелы игнорируются", 1, args.size());
        check("аргумент после лишних пробелов", "-la", args.get(0));
    }

    private static void blankLineIsEmpty() throws ParseException {
        check("строка из пробелов считается пустой", true, PARSER.parse("   ").isEmpty());
    }

    private static void emptyQuotedArgument() throws ParseException {
        List<String> args = PARSER.parse("ls \"\"").getArgs();
        check("пустые кавычки дают один аргумент", 1, args.size());
        check("пустой аргумент", "", args.get(0));
    }

    private static void unterminatedQuoteThrows() {
        boolean thrown = false;
        try {
            PARSER.parse("cd \"unterminated");
        } catch (ParseException e) {
            thrown = true;
        }
        check("незакрытая кавычка бросает исключение", true, thrown);
    }

    private static void check(String label, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.println("OK   " + label);
        } else {
            failed++;
            System.out.println("FAIL " + label + ": ожидалось <" + expected + ">, получено <" + actual + ">");
        }
    }
}
