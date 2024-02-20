package ru.astecom.support;

import java.awt.*;
import java.nio.file.Path;

/**
 * Помощьник приложения
 */
public class ApplicationHelper {

    /**
     * Получить текущий каталог
     * @return текущий каталог
     */
    public static Path getRoot() {
        return Path.of("");
    }

    /**
     * Получить путь до модели name
     * @param name название модели
     * @return путь до модели в ресурсах
     */
    public static Path getModelPath(String name) {
        return getRoot().resolve("models").resolve(name);
    }

    /**
     * Получить половину от ширины экрана с высотой равной соотношению 16 / 9
     * @return половину от ширины экрана с высотой равной соотношению 16 / 9
     */
    public static Dimension get16to9ScreenSize() {
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        var height = screenSize.getHeight() / 2;
        return new Dimension((int) (height * 16 / 9), (int) (height));
    }

    /**
     * Получить половину от минимальной компоненты экрана 1 к 1
     * @return половину от минимальной компоненты экрана
     */
    public static Dimension get1to1ScreenSize() {
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        var min = (int) Math.min(screenSize.getHeight(), screenSize.getWidth()) / 2;
        return new Dimension(min, min);
    }
}
