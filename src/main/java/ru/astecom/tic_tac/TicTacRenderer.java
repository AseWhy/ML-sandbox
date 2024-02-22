package ru.astecom.tic_tac;

import ru.astecom.support.AbstractFrameRenderer;
import ru.astecom.support.ApplicationHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Отрисовщик игры "крестики нолики"
 */
public class TicTacRenderer extends AbstractFrameRenderer {

    /** Номер игрока пользователя */
    private static final int USER_PLAYER = 1;

    /** Номер игрока агента */
    private static final int AGENT_PLAYER = 2;

    /** Игровое поле */
    private Panel board;

    /** Список кнопок на поле */
    private JButton[] buttons;

    /** Игра в крестики нолики */
    private TicTacGame game;

    /** Тренер нейросети */
    private TicTacDQNTrainer trainer;

    /** Агент нейросети */
    private TicTacAgent agent;

    /**
     * Конструктор
     */
    public TicTacRenderer() {
        setTitle("Крестики нолики [Обучение с подкреплением]");
    }

    @Override
    public void stop() {
        getContentPane().remove(board);
        agent.close();
        agent = null;
        trainer = null;
        game = null;
        board = null;
    }

    @Override
    public boolean isRunning() {
        return board != null;
    }

    @Override
    public void start() {
        pack();
        setSize(ApplicationHelper.get1to1ScreenSize());
        setLocationRelativeTo(null);
        setVisible(true);
        initGame();
        buildBoard();
        trainer.train();
    }

    /**
     * Инициализировать игру
     */
    private void initGame() {
        game = new TicTacGame(new TicTacState(), 1);
        game.addStepEventListener(result -> update(game.getState().getField()));
        agent = new TicTacAgent(game, AGENT_PLAYER);
        trainer = new TicTacDQNTrainer(1000, 500, agent);
    }

    /**
     * Построить игровое поле
     */
    private void buildBoard() {
        board = new Panel();
        board.setLayout(new GridLayout(3, 3));
        buttons = new JButton[9];
        for (int i = 0; i < buttons.length; i++) {
            var button = new JButton();
            button.addActionListener(new TicTacToeActionListener(i % 3, i / 3));
            board.add(button);
            buttons[i] = button;
        }
        getContentPane().add(board);
    }

    /**
     * Обновить состояние поля
     * @param board состояние доски
     */
    private void update(int[] board) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setText(board[i] == USER_PLAYER ? "X" : board[i] == 0 ? null : "0");
        }
    }

    /**
     * Слушатель события нажатия на кнопку поля
     */
    private class TicTacToeActionListener implements ActionListener {

        /** x координата кнопки */
        private final int x;

        /** y координата кнопки */
        private final int y;

        /**
         * Конструктор
         * @param x x координата кнопки
         * @param y y координата кнопки
         */
        public TicTacToeActionListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            game.step(x, y, USER_PLAYER);
        }
    }
}
