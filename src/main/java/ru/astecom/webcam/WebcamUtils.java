package ru.astecom.webcam;

import java.awt.image.BufferedImage;

/**
 * Помощьник распознования цифр с вебкамеры
 */
public class WebcamUtils {

    /**
     * Преобразовать изображение в чернобелое
     * @param image изображение
     * @return изминенное изображение
     */
    public static BufferedImage toBlackAndWhite(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int total = width * height;
        for (int i = 0; i < total; i++) {
            int x = i % width;
            int y = i / height;
            int color = image.getRGB(x, y);
            // Получам средний цвет по всем каналам
            int gray = (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) / 3;
            image.setRGB(x, y, gray > 127 ? 0 : 0xFFFFFF);
        }
        return image;
    }
}
