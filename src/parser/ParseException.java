package parser;

/**
 * Ошибка разбора введённой строки.
 * <p>
 * Наследуется от {@link Exception}, а не от {@code RuntimeException}:
 * ошибка ввода — ожидаемая ситуация, поэтому её обработка обязательна
 * для вызывающего кода.
 */
public class ParseException extends Exception {

    /**
     * @param message описание причины, показывается пользователю в терминале
     */
    public ParseException(String message) {
        super(message);
    }
}
