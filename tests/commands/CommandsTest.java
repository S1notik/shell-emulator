package commands;

import vfs.VfsNode;
import vfs.VirtualFileSystem;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Проверки команд этапа 4: {@code ls}, {@code cd}, {@code tail},
 * {@code echo}, {@code cal}.
 * <p>
 * Дерево VFS строится прямо в памяти, без обращения к диску, а часы
 * для {@code cal} зафиксированы на 22 октября 2026 года — поэтому
 * результат не зависит ни от окружения, ни от даты запуска.
 */
public final class CommandsTest {

    private static final String NL = System.lineSeparator();
    private static final Clock FIXED_CLOCK = Clock.fixed(
            LocalDate.of(2026, 10, 22).atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

    private static int failed = 0;

    private CommandsTest() {
    }

    public static void main(String[] args) {
        lsCommand();
        cdCommand();
        tailCommand();
        echoCommand();
        calCommand();
        withoutVfs();

        if (failed > 0) {
            System.out.println("Провалено проверок: " + failed);
            System.exit(1);
        }
        System.out.println("Все проверки пройдены");
    }

    /**
     * Строит дерево: {@code /readme.txt}, {@code /docs/manual.txt},
     * {@code /docs/inner/deep.txt}, {@code /empty/}.
     */
    private static VirtualFileSystem sample() {
        VfsNode root = VfsNode.directory("");
        root.addChild(VfsNode.file("readme.txt", "одна строка\n"));
        VfsNode docs = VfsNode.directory("docs");
        root.addChild(docs);
        docs.addChild(VfsNode.file("manual.txt", "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n"));
        VfsNode inner = VfsNode.directory("inner");
        docs.addChild(inner);
        inner.addChild(VfsNode.file("deep.txt", "глубоко\n"));
        root.addChild(VfsNode.directory("empty"));
        return new VirtualFileSystem(root, "sample");
    }

    private static void lsCommand() {
        VirtualFileSystem vfs = sample();
        LsCommand ls = new LsCommand(vfs);
        check("ls корня: алфавитный порядок, каталоги с /",
                "docs/  empty/  readme.txt", ls.execute(List.of()));
        check("ls по относительному пути", "inner/  manual.txt", ls.execute(List.of("docs")));
        check("ls по абсолютному пути", "deep.txt", ls.execute(List.of("/docs/inner")));
        check("ls пустого каталога", "", ls.execute(List.of("empty")));
        check("ls файла выводит его имя", "readme.txt", ls.execute(List.of("readme.txt")));
        check("ls несуществующего пути",
                "ls: нет: нет такого файла или каталога", ls.execute(List.of("нет")));
        check("ls с опцией", "ls: неизвестная опция -la", ls.execute(List.of("-la")));
        check("ls с двумя аргументами",
                "ls: слишком много аргументов", ls.execute(List.of("a", "b")));
    }

    private static void cdCommand() {
        VirtualFileSystem vfs = sample();
        CdCommand cd = new CdCommand(vfs);
        check("cd в каталог ничего не выводит", "", cd.execute(List.of("docs")));
        check("cd меняет текущий каталог", "/docs", vfs.currentPath());
        check("cd относительно нового текущего", "", cd.execute(List.of("inner")));
        check("текущий после второго cd", "/docs/inner", vfs.currentPath());
        cd.execute(List.of(".."));
        check("cd .. поднимает на уровень", "/docs", vfs.currentPath());
        cd.execute(List.of());
        check("cd без аргумента ведёт в корень", "/", vfs.currentPath());
        check("cd в файл", "cd: readme.txt: не является каталогом",
                cd.execute(List.of("readme.txt")));
        check("cd в несуществующий каталог",
                "cd: нет: нет такого файла или каталога", cd.execute(List.of("нет")));
        check("после ошибки каталог не меняется", "/", vfs.currentPath());
        check("cd с двумя аргументами",
                "cd: слишком много аргументов", cd.execute(List.of("a", "b")));
    }

    private static void tailCommand() {
        TailCommand tail = new TailCommand(sample());
        check("tail по умолчанию — десять строк",
                String.join(NL, "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
                tail.execute(List.of("docs/manual.txt")));
        check("tail -n 3", String.join(NL, "10", "11", "12"),
                tail.execute(List.of("-n", "3", "docs/manual.txt")));
        check("tail -n больше длины файла", "одна строка",
                tail.execute(List.of("-n", "50", "readme.txt")));
        check("tail -n 0", "", tail.execute(List.of("-n", "0", "readme.txt")));
        check("tail каталога", "tail: docs: это каталог", tail.execute(List.of("docs")));
        check("tail несуществующего файла",
                "tail: нет.txt: нет такого файла или каталога", tail.execute(List.of("нет.txt")));
        check("tail с неверным числом", "tail: неверное число строк: abc",
                tail.execute(List.of("-n", "abc", "readme.txt")));
        check("tail с отрицательным числом", "tail: неверное число строк: -1",
                tail.execute(List.of("-n", "-1", "readme.txt")));
        check("tail без аргументов", "tail: использование: tail [-n число] файл",
                tail.execute(List.of()));
    }

    private static void echoCommand() {
        EchoCommand echo = new EchoCommand();
        check("echo склеивает через пробел", "привет мир", echo.execute(List.of("привет", "мир")));
        check("echo сохраняет пробелы внутри аргумента", "a   b", echo.execute(List.of("a   b")));
        check("echo без аргументов", "", echo.execute(List.of()));
    }

    private static void calCommand() {
        CalCommand cal = new CalCommand(FIXED_CLOCK);
        String october = String.join(NL,
                "    Октябрь 2026",
                "Пн Вт Ср Чт Пт Сб Вс",
                "          1  2  3  4",
                " 5  6  7  8  9 10 11",
                "12 13 14 15 16 17 18",
                "19 20 21 22 23 24 25",
                "26 27 28 29 30 31");
        check("cal без аргументов — текущий месяц", october, cal.execute(List.of()));
        check("cal с месяцем и годом", october, cal.execute(List.of("10", "2026")));
        check("cal: месяц начинается в понедельник",
                "Пн Вт Ср Чт Пт Сб Вс" + NL + " 1  2  3  4  5  6  7",
                cal.execute(List.of("6", "2026")).lines().skip(1).limit(2)
                        .reduce((a, b) -> a + NL + b).orElse(""));
        check("cal с неверным месяцем", "cal: неверный месяц: 13", cal.execute(List.of("13", "2026")));
        check("cal с нечисловым годом", "cal: неверный год: abc", cal.execute(List.of("1", "abc")));
        check("cal с одним аргументом", "cal: использование: cal [месяц год]",
                cal.execute(List.of("2026")));
    }

    private static void withoutVfs() {
        check("ls без VFS", "ls: VFS не загружена", new LsCommand(null).execute(List.of()));
        check("cd без VFS", "cd: VFS не загружена", new CdCommand(null).execute(List.of("a")));
        check("tail без VFS", "tail: VFS не загружена",
                new TailCommand(null).execute(List.of("a")));
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
