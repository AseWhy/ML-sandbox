package ru.astecom.snake;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Состояние игры в змейку
 */
public class SnakeGameState implements Encodable {

    /** Блок барьера */
    public static final int BARRIER = 1;

    /** Дистанция на которую видит змея */
    private final int distance;

    /** Ширина игрового поля */
    private final int width;

    /** Высота игрового поля */
    private final int height;

    /** Матрица */
    private final int[] matrix;

    /** Змейка */
    private final Snake snake;

    /** Яблоко */
    private final Apple apple;

    /**
     * Конструктор состояния
     * @param distance дистанция, на которую видит змея
     * @param height высота матрицы
     * @param width  ширина матрицы
     */
    public SnakeGameState(int distance, int width, int height) {
        this.distance = distance;
        this.matrix = new int[width * height];
        this.width = width;
        this.height = height;
        this.apple = new Apple(this);
        this.snake = new Snake(width / 2, height / 2, this);
    }

    /**
     * Конструктор состояния
     *
     * @param distance дистанция, на которую видит змея
     * @param width    ширина матрицы
     * @param height   высота матрицы
     * @param matrix   матрица
     * @param snake    змейка
     * @param apple    яблоко
     */
    private SnakeGameState(int distance, int width, int height, int[] matrix, Snake snake, Apple apple) {
        this.distance = distance;
        this.matrix = matrix;
        this.width = width;
        this.height = height;
        this.snake = snake;
        this.apple = apple;
    }

    /**
     * Сбросить состояние игры
     */
    public void reset() {
        this.apple.reset();
        this.snake.reset();
    }

    /**
     * Получить объект яблока
     * @return объект яблока
     */
    public Apple getApple() {
        return apple;
    }

    /**
     * Получить объект змейки
     * @return объект змейки
     */
    public Snake getSnake() {
        return snake;
    }

    /**
     * Получить матрицу
     * @return матрица
     */
    public int[] getMatrix() {
        return matrix;
    }

    /**
     * Получить дистанцию виденья змейки
     * @return дистанция виденья змейки
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Получить ширину матрицы
     * @return ширина матрицы
     */
    public int getWidth() {
        return width;
    }

    /**
     * Получить высоту матрицы
     * @return высота матрицы
     */
    public int getHeight() {
        return height;
    }

    /**
     * Действие перед шагом
     */
    public void beforeStep() {
        snake.resetMatrix(matrix);
        apple.resetMatrix(matrix);
    }

    /**
     * Действие после шага
     */
    public void afterStep() {
        snake.applyMatrix(matrix);
        apple.applyMatrix(matrix);
    }

    /**
     * Получить матрицу которую видит змейка
     * @return матрица которую видит змейка
     */
    public int[] getVisionMatrix() {
        var snakeHead = snake.getSnakeHead();
        var cropped = SnakeUtils.cropSnakeView(matrix, width, height, snakeHead.x, snakeHead.y, distance, BARRIER);
        SnakeUtils.rotateMatrix(cropped, distance * 2 + 1, getRotationsCount());
        return cropped;
    }

    /**
     * Получить количество поворотов матрицы
     * @return необходимое количество поворотов матрицы
     */
    private int getRotationsCount() {
        return switch (snake.getDirection()) {
            case WEST -> 0;
            case NORTH -> 1;
            case EAST -> 2;
            case SOUTH -> 3;
        };
    }

    @Override
    public double[] toArray() {
        return new double[0];
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        return Nd4j.createFromArray(getVisionMatrix());
    }

    @Override
    public Encodable dup() {
        return new SnakeGameState(distance, width, height, matrix, snake, apple);
    }
}
