package ru.astecom.webcam;

import com.twelvemonkeys.image.ImageUtil;
import ru.astecom.support.AbstractFrameRenderer;
import ru.astecom.support.ApplicationHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;

/**
 * Класс для отрисовки изображения с вебкамеры и результатов обнаружения
 */
public class WebcamRenderer extends AbstractFrameRenderer implements Closeable {

    /** Таймер отрисовки */
    private final Timer timer;

    /** Компонент отрисовки интерфейса */
    private final Drawer drawer;

    /** Сборщик кадров */
    private final WebcamGrabber webcamGrabber = new WebcamGrabber(0);

    /** Обнаружитель чисел */
    private final WebcamNumberDetector detector;

    /**
     * Конструктор отрисовщика
     */
    public WebcamRenderer() {
        super();
        setTitle("Распознование чисел с веб камеры");
        detector = new WebcamNumberDetector(28, 28, ApplicationHelper.getModelPath(WebcamModelTrainer.MODEL_FILE_NAME));
        timer = new Timer(50, new DrawerListener());
        drawer = new Drawer();
        getContentPane().add(drawer);
    }

    /**
     * Начать отрисовку изображения с вебкамеры
     */
    private void startDraw() {
        webcamGrabber.start();
        timer.start();
    }

    @Override
    public void start() {
        pack();
        setSize(ApplicationHelper.get1to1ScreenSize());
        setLocationRelativeTo(null);
        setVisible(true);
        startDraw();
    }

    @Override
    public void close() {
        timer.stop();
        webcamGrabber.stop();
    }

    /**
     * Компонент отрисовки интерфейса
     */
    private class Drawer extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            if (!timer.isRunning()) {
                return;
            }
            var graphics = g.create();
            try {
                var image = webcamGrabber.grab();
                var min = Math.min(image.getWidth(), image.getHeight());
                var rect = image = image.getSubimage((image.getWidth() - min) / 2, (image.getHeight() - min) / 2, min, min);
                var scaled = ImageUtil.createScaled(image, 28, 28, 0);
                var detected = detector.detect(scaled);
                // Рисуем что поступает в обработчик
                graphics.drawImage(rect, 0, 0, getWidth(), getHeight(), null);
                // Рисуем что видит нейронка
                graphics.drawImage(scaled, getWidth() - 258, getHeight() - 258, 256, 256, null);
                // Выводим что обнаружили
                graphics.drawString("Обнаружено: " + detected, 2, 18);
            } finally {
                graphics.dispose();
            }
        }
    }

    /**
     * Поток отрисовки
     */
    private class DrawerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            drawer.repaint();
        }
    }
}
