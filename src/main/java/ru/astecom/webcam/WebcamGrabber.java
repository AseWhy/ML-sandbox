package ru.astecom.webcam;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.awt.image.BufferedImage;

/**
 * Класс для сбора кадров с вебкамеры
 */
public final class WebcamGrabber {

    /** Сборщик фреймов */
    private final FrameGrabber grabber;

    /** Конвертер */
    private final Java2DFrameConverter converter;

    /**
     * Сборщик кадров из источника
     * @param deviceNumber номер устройства источника фреймов
     */
    public WebcamGrabber(int deviceNumber) {
        grabber = new OpenCVFrameGrabber(deviceNumber);
        converter = new Java2DFrameConverter();
    }

    /**
     * Запустить сборку кадров из источника
     */
    public void start() {
        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("Ошибка при запуске сборщика кадров", e);
        }
    }

    /**
     * Запустить сборку кадров из источника
     */
    public void stop() {
        try {
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("Ошибка при остановке сборщика кадров", e);
        }
    }

    /**
     * Получить фрейм из источника
     * @return фрейм из источника
     */
    public BufferedImage grab() {
        try {
            return converter.convert(this.grabber.grab());
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("Ошибка получения кадра", e);
        }
    }
}
