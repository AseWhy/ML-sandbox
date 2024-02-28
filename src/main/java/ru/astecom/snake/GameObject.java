package ru.astecom.snake;

/**
 * Игровой объект
 */
public interface GameObject {

    /**
     * Применить данные объекта к матрице
     * @param matrix матрица
     */
    void applyMatrix(int[] matrix);

    /**
     * Удалить свои данные с матрицы
     * @param matrix матрица
     */
    void resetMatrix(int[] matrix);
}
