package commands;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Команда {@code cal}: выводит календарь на месяц.
 * <p>
 * Без аргументов показывает текущий месяц, с двумя аргументами —
 * указанный: {@code cal 10 2026}. Неделя начинается с понедельника,
 * названия месяцев и дней — по-русски.
 * <p>
 * Текущая дата берётся из переданных часов, а не напрямую из системы:
 * так команду можно проверить в тестах на фиксированной дате.
 */
public class CalCommand implements Command {

    private static final Locale RUSSIAN = Locale.of("ru");
    private static final String WEEK_HEADER = "Пн Вт Ср Чт Пт Сб Вс";
    private static final int DAYS_IN_WEEK = 7;
    private static final int CELL_WIDTH = 3;
    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;
    private static final int MIN_YEAR = 1;
    private static final int MAX_YEAR = 9999;
    private static final int EXPLICIT_ARGS = 2;
    private static final String USAGE = "cal: использование: cal [месяц год]";

    private final Clock clock;

    /**
     * @param clock часы, по которым определяется текущий месяц
     */
    public CalCommand(Clock clock) {
        this.clock = clock;
    }

    @Override
    public String execute(List<String> args) {
        if (args.isEmpty()) {
            return render(YearMonth.now(clock));
        }
        if (args.size() != EXPLICIT_ARGS) {
            return USAGE;
        }
        Integer month = parseInRange(args.get(0), MIN_MONTH, MAX_MONTH);
        if (month == null) {
            return "cal: неверный месяц: " + args.get(0);
        }
        Integer year = parseInRange(args.get(1), MIN_YEAR, MAX_YEAR);
        if (year == null) {
            return "cal: неверный год: " + args.get(1);
        }
        return render(YearMonth.of(year, month));
    }

    /**
     * Строит календарь месяца: заголовок, дни недели и сетку чисел.
     *
     * @param month месяц
     * @return календарь в несколько строк
     */
    private String render(YearMonth month) {
        StringBuilder out = new StringBuilder(header(month));
        out.append(System.lineSeparator()).append(WEEK_HEADER);

        int offset = month.atDay(1).getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        StringBuilder week = new StringBuilder(" ".repeat(offset * CELL_WIDTH));
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            week.append(String.format("%2d ", day));
            if ((offset + day) % DAYS_IN_WEEK == 0) {
                out.append(System.lineSeparator()).append(week.toString().stripTrailing());
                week.setLength(0);
            }
        }
        if (!week.isEmpty()) {
            out.append(System.lineSeparator()).append(week.toString().stripTrailing());
        }
        return out.toString();
    }

    /**
     * Формирует заголовок вида «Октябрь 2026», выровненный по центру сетки.
     *
     * @param month месяц
     * @return строка заголовка
     */
    private String header(YearMonth month) {
        String name = month.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, RUSSIAN);
        String title = Character.toUpperCase(name.charAt(0)) + name.substring(1)
                + " " + month.getYear();
        int width = WEEK_HEADER.length();
        int padding = Math.max(0, (width - title.length()) / 2);
        return " ".repeat(padding) + title;
    }

    /**
     * Разбирает целое число и проверяет, что оно в допустимых границах.
     *
     * @param text строка с числом
     * @param min  наименьшее допустимое значение
     * @param max  наибольшее допустимое значение
     * @return число или {@code null}, если строка не число или вне границ
     */
    private Integer parseInRange(String text, int min, int max) {
        try {
            int value = Integer.parseInt(text);
            return value < min || value > max ? null : value;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
