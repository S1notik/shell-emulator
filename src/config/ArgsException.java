package config;

/**
 * Ошибка разбора параметров командной строки.
 * <p>
 * Наследуется от {@link Exception}, а не от {@code RuntimeException}:
 * неверные параметры — ожидаемая ситуация, поэтому их обработка обязательна
 * для вызывающего кода.
 */
public class ArgsException extends Exception {

    /**
     * @param message описание причины, показывается пользователю при запуске
     */
    public ArgsException(String message) {
        super(message);
    }
}