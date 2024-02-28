package ru.astecom.snake;

import ru.astecom.support.AbstractFrameRenderer;
import ru.astecom.support.ApplicationHelper;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

/**
 * Отрисовщик игры в змейку
 */
public class SnakeRenderer extends AbstractFrameRenderer  {

    /** Дистанция виденья змейки */
    public static final int SNAKE_VISION_DISTANCE = 2;

    /** Ширина игрового поля */
    public static final int SNAKE_FIELD_WIDTH = 10;

    /** Высота игрового поля */
    public static final int SNAKE_FIELD_HEIGHT = 10;

    /** Обучающий поток */
    private CompletableFuture<Void> trainingThread;

    /** Панель с кнопками */
    private Panel buttonsPane;

    /** Компонент отрисовки интерфейса */
    private Drawer drawer;

    /** Игра в змейку */
    private SnakeAgent agent;

    /** Игра в змейку */
    private SnakeGame game;

    /**
     * Конструктор
     */
    public SnakeRenderer() {
        setTitle("Змейка [Обучение с подкреплением]");
    }

    @Override
    public void start() {
        pack();
        setSize(ApplicationHelper.get1to1ScreenSize());
        setLocationRelativeTo(null);
        setVisible(true);
        drawer = new Drawer();
        agent = new SnakeAgent(1000, 500, SNAKE_VISION_DISTANCE, SNAKE_FIELD_WIDTH, SNAKE_FIELD_HEIGHT);
        game = new SnakeGame(new SnakeGameState(SNAKE_VISION_DISTANCE, SNAKE_FIELD_WIDTH, SNAKE_FIELD_HEIGHT));
        game.addStepEventListener(e -> drawer.step());
        startButtonsPane();
    }

    /**
     * Показать панель с кнопками выбора режима
     */
    private void startButtonsPane() {
        buttonsPane = new Panel(new GridBagLayout());
        var subButtonsPane = new Panel(new GridLayout(0, 1, 0, 15));
        var trainingMode = new JButton("Режим тренеровки в реальном времени");
        trainingMode.addActionListener(e -> start(0));
        subButtonsPane.add(trainingMode);
        var trainedMode = new JButton("Использовать обученную модель");
        trainedMode.addActionListener(e -> start(1));
        subButtonsPane.add(trainedMode);
        buttonsPane.add(subButtonsPane);
        buttonsPane.setBackground(Color.PINK);
        getContentPane().add(buttonsPane);
    }

    @Override
    public void stop() {
        getContentPane().remove(drawer);
        if (trainingThread != null) {
            trainingThread.cancel(true);
        }
        buttonsPane = null;
        game = null;
        agent = null;
        drawer = null;
    }

    @Override
    public boolean isRunning() {
        return game != null;
    }

    /**
     * Запустить выбранный режим игры
     * @param mode режим игры 0 - обучение в реальном времени, 1 - обучение с подкреплением
     */
    private void start(int mode) {
        getContentPane().remove(buttonsPane);
        getContentPane().add(drawer);
        if (mode == 0) {
            trainingThread = agent.liveTraining(game);
        } else {
            trainingThread = agent.playTrained(game);
        }
    }

    /**
     * Компонент отрисовки интерфейса
     */
    private class Drawer extends JPanel {

        /**
         * Шаг отрисовки
         */
        public void step() {
            repaint();
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            var graphics = g.create();
            var state = game.getState();
            var matrix = state.getMatrix();
            var width = state.getWidth();
            var cellW = drawer.getWidth() / width;
            var cellH = drawer.getHeight() / state.getHeight();
            try {
                graphics.fillRect(0, 0, drawer.getWidth(), drawer.getHeight());
                for (int i = 0; i < matrix.length; i++) {
                    int x = i % width;
                    int y = i / width;
                    switch (matrix[i]) {
                        default: continue;
                        case Snake.OBJECT_ID: graphics.setColor(Color.GRAY); break;
                        case Snake.HEAD_OBJECT_ID: graphics.setColor(Color.YELLOW); break;
                        case Apple.OBJECT_ID: graphics.setColor(Color.GREEN); break;
                    }
                    graphics.fillRect(x * cellW, y * cellH, cellW, cellH);
                }
                var visionMatrix = state.getVisionMatrix();
                var vMw = drawer.getWidth() / 4;
                var vMh = drawer.getHeight() / 4;
                var vCellW = vMw / 5;
                var vCellH = vMh / 5;
                for (int i = 0; i < visionMatrix.length; i++) {
                    int x = i % 5;
                    int y = i / 5;
                    switch (visionMatrix[i]) {
                        default: continue;
                        case Snake.OBJECT_ID: graphics.setColor(Color.GRAY); break;
                        case Snake.HEAD_OBJECT_ID: graphics.setColor(Color.YELLOW); break;
                        case Apple.OBJECT_ID: graphics.setColor(Color.GREEN); break;
                        case SnakeGameState.BARRIER: graphics.setColor(Color.RED); break;
                    }
                    graphics.fillRect(vMw * 3 + x * vCellW, vMh * 3 + y * vCellH, vCellW, vCellH);
                }
                graphics.setColor(Color.BLACK);
                graphics.drawRect(vMw * 3, vMh * 3, vMw, vMw);
            } finally {
                graphics.dispose();
            }
        }
    }
}
