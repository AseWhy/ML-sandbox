package ru.astecom.snake;

import java.awt.*;
import java.util.Random;

/**
 * Класс яблока
 */
public class Apple implements GameObject {

    /** Идентификатор объекта */
    public static final int OBJECT_ID = 4;

    /** Генератор рандома */
    private final Random random;

    /** Состояние игры в змейку */
    private final SnakeGameState state;

    /** Положение яблока на игровом поле - координата х */
    private int x;

    /** Положение яблока на игровом поле - координата y */
    private int y;

    /**
     * Конструктор яблока
     * @param state состояние игры в змейку
     */
    public Apple(SnakeGameState state) {
        this.random = new Random();
        this.state = state;
        this.reset();
    }

    /**
     * Проверить, съела ли змейка яблоко
     * @param head голова змеи
     */
    public SnakeGame.StepResult step(Point head) {
        if (x == head.x && y == head.y) {
            reset();
            return SnakeGame.StepResult.APPLE_EATEN;
        } else {
            return SnakeGame.StepResult.CONTINUE;
        }
    }

    /**
     * Обновить положение яблока на карте
     */
    public void reset() {
        x = random.nextInt(state.getWidth());
        y = random.nextInt(state.getHeight());
    }

    @Override
    public void applyMatrix(int[] matrix) {
        matrix[y * state.getWidth() + x] = OBJECT_ID;
    }

    @Override
    public void resetMatrix(int[] matrix) {
        matrix[y * state.getWidth() + x] = 0;
    }
}
