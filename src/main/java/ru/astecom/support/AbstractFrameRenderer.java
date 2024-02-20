package ru.astecom.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astecom.FrameRenderer;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;

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
            if (AbstractFrameRenderer.this instanceof Closeable c) {
                try {
                    log.info(String.format("Выполняю очистку ресурсов для окна %s", getTitle()));
                    c.close();
                    log.info(String.format("Очистка ресурсов для окна %s завершена", getTitle()));
                } catch (IOException ex) {
                    log.error(String.format("Ошибка при выполнении очистки ресурсов окна: %s", getTitle()), ex);
                }
            }
        }
    }
}
