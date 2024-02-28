package ru.astecom.snake;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.astecom.support.ApplicationConfigure;

/**
 * Тест игры в змейку
 */
class SnakeUtilsTest {

    /**
     * Перед тестами обновляем конфигурацию
     */
    @BeforeAll
    public static void before() {
        ApplicationConfigure.configure();
    }

    /**
     * Протестировать обрезание матрицы к центру от змеи
     */
    @Test
    void CropSnakeView() {
        var cropped = SnakeUtils.cropSnakeView(new int[] {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 1, Snake.OBJECT_ID,
                0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        }, 10, 10, 9, 4, 2, SnakeGameState.BARRIER);
        Assertions.assertEquals(Snake.OBJECT_ID, cropped[12]);
        Assertions.assertEquals(SnakeGameState.BARRIER, cropped[13]);
        Assertions.assertEquals(SnakeGameState.BARRIER, cropped[3]);
        Assertions.assertEquals(SnakeGameState.BARRIER, cropped[8]);
        Assertions.assertEquals(SnakeGameState.BARRIER, cropped[18]);
        Assertions.assertEquals(SnakeGameState.BARRIER, cropped[23]);
        SnakeUtils.printMatrix(cropped, 5);
    }

    /**
     * Протестировать работу поворота матрицы
     */
    @Test
    void RotateMatrix() {
        var width = 4;
        var matrix = new int[] {
            1, 2, 3, 4,
            0, 2, 1, 5,
            3, 2, 3, 6
        };
        SnakeUtils.printMatrix(matrix, width);
        try {
            SnakeUtils.rotateMatrix(matrix, width, 1);
            Assertions.assertArrayEquals(matrix, new int[] {
                4, 5, 6,
                3, 1, 3,
                2, 2, 2,
                1, 0, 3
            });
            SnakeUtils.rotateMatrix(matrix, width = 3, 1);
            Assertions.assertArrayEquals(matrix, new int[] {
                6, 3, 2, 3,
                5, 1, 2, 0,
                4, 3, 2, 1
            });
            SnakeUtils.rotateMatrix(matrix, width = 4, 1);
            Assertions.assertArrayEquals(matrix, new int[] {
                3, 0, 1,
                2, 2, 2,
                3, 1, 3,
                6, 5, 4
            });
        } finally {
            SnakeUtils.printMatrix(matrix, width);
        }
    }
}