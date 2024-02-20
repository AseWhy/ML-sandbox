package ru.astecom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astecom.support.ApplicationConfigure;
import ru.astecom.support.ApplicationHelper;
import ru.astecom.webcam.WebcamRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Closeable;
import java.io.IOException;

/**
 * Класс точка входа в приложение
 */
public class Sandbox extends JFrame {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(Sandbox.class);

    /** Отрисовщики дочерних приложений */
    private final FrameRenderer[] renderers;

    /**
     * Конструктор
     */
    public Sandbox(FrameRenderer[] renderers) {
        setTitle("ml-sandbox");
        this.renderers = renderers;
        log.info("Инициализация активности ml-snabox с {} отрисовщиками", renderers.length);
    }

    /**
     * Начать отображение фрейма
     */
    public void start() {
        pack();
        setSize(ApplicationHelper.get16to9ScreenSize());
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        buildButtonList();
        log.info("Запуск активности ml-snabox с {} отрисовщиками", renderers.length);
    }

    /**
     * Построить список кнопок запускающие другие отрисовщики
     */
    public void buildButtonList() {
        var pane = new Panel();
        for (var current : renderers) {
            var button = new JButton();
            button.setAction(new FrameRendererAction(current));
            pane.add(button);
        }
        getContentPane().add(pane);
    }

    /** Конфигурация приложения */
    static {
        ApplicationConfigure.configure();
    }

    /** Список отрисовщиков */
    private static final FrameRenderer[] FRAME_RENDERERS = { new WebcamRenderer() };

    /**
     * Точка входа в приложение
     * @param args аргументы запуска
     */
    public static void main(String[] args) {
        var application = new Sandbox(FRAME_RENDERERS);
        Runtime.getRuntime().addShutdownHook(new Thread(Sandbox::close));
        application.start();
    }

    /**
     * Метод очистки ресурсов всех отрисовщиков
     */
    private static void close() {
        for (var current : FRAME_RENDERERS) {
            if (current instanceof Closeable c) {
                try {
                    log.info(String.format("Выполняю очистку ресурсов для окна %s", current.getTitle()));
                    c.close();
                    log.info(String.format("Очистка ресурсов для окна %s завершена", current.getTitle()));
                } catch (IOException e) {
                    log.error(String.format("Ошибка при выполнении очистки ресурсов окна: %s", current.getTitle()), e);
                }
            }
        }
    }

    /**
     * Действие для кнопки запуска отрисовщика
     */
    private static class FrameRendererAction extends AbstractAction {

        /** Отрисовщик */
        private final FrameRenderer renderer;

        /**
         * Конструктор
         * @param renderer отрсовщик
         */
        private FrameRendererAction(FrameRenderer renderer) {
            this.renderer = renderer;
            this.putValue(Action.NAME, renderer.getTitle());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            log.info("Запуск действия: {}", renderer.getTitle());
            try {
                renderer.start();
            } catch (Throwable t) {
                log.error("Ошибка запуска действия", t);
            }
        }
    }
}
