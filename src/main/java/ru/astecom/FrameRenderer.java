package ru.astecom;

/**
 * Отрисовщик фреймов
 */
public interface FrameRenderer {

    /** Получить заголовок компонента */
    String getTitle();

    /** Запустить отрисовку компонента */
    void start();
}
