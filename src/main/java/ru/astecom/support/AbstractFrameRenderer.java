package ru.astecom.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astecom.FrameRenderer;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Абстрактный класс для отрисовки фрейма
 */
public abstract class AbstractFrameRenderer extends JFrame implements FrameRenderer {

    /** Логгер */
    protected static final Logger log = LoggerFactory.getLogger(AbstractFrameRenderer.class);

    /**
     * Конструктор
     */
    protected AbstractFrameRenderer() {
        addWindowListener(new FrameWindowAdapter());
    }

    /**
     * Слушатель событий окна
     */
    private class FrameWindowAdapter extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            var renderer = AbstractFrameRenderer.this;
            if (!renderer.isRunning()) {
                return;
            }
            log.info(String.format("Выполняю очистку ресурсов для окна %s", renderer.getTitle()));
            renderer.stop();
            log.info(String.format("Очистка ресурсов для окна %s завершена", renderer.getTitle()));
        }
    }
}
