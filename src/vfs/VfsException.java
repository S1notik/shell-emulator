package vfs;

/**
 * Ошибка загрузки виртуальной файловой системы.
 * <p>
 * Наследуется от {@link Exception}: отсутствующий или неверный источник VFS —
 * ожидаемая ситуация, поэтому её обработка обязательна для вызывающего кода.
 */
public class VfsException extends Exception {

    /**
     * @param message описание причины, показывается пользователю в окне
     */
    public VfsException(String message) {
        super(message);
    }
}
