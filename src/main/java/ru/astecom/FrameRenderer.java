package ru.astecom;

/**
 * Отрисовщик фреймов
 */
public interface FrameRenderer {

    /**
     * Получить заголовок компонента
     * @return заголовок компонента
     */
    String getTitle();

    /**
     * Запустить отрисовку компонента
     */
    void start();

    /**
     * Остановить активность
     */
    void stop();

    /**
     * Получить признак того что активность запущена
     * @return признак того что активность запущена
     */
    boolean isRunning();
}
