package ru.astecom.snake;

import com.twelvemonkeys.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Хелпер для игры в змейку
 */
public class SnakeUtils {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(SnakeUtils.class);

    /**
     * Получить матрицу виденья змейки
     * @param matrix исходная матрица
     * @param width ширина матрицы
     * @param height высота матрицы
     * @param snakeX x координата змейки
     * @param snakeY y координата змейки
     * @param distance дистанция виденья
     * @param barrier барьер, указывающий на препятствие
     * @return матрица виденья высотой и шириной distance * 2 + 1
     */
    public static int[] cropSnakeView(int[] matrix, int width, int height, int snakeX, int snakeY, int distance, int barrier) {
        int distanceD = distance * 2 + 1;
        int[] field = new int[distanceD * distanceD];
        for (int i = 0; i < field.length; i++) {
            int y = i / distanceD;
            int x = i % distanceD;
            int ox = snakeX + x - distance;
            int oy = snakeY + y - distance;
            if (ox < 0 || ox >= width || oy < 0 || oy >= height) {
                field[i] = barrier;
            } else {
                field[i] = matrix[oy * width + ox];
            }
        }
        return field;
    }

    /**
     * Повернуть матрицу против часовой стрелки
     * @param matrix матрица
     * @param w ширина матрицы
     * @param n количество раз которое нужно повернуть
     */
    public static void rotateMatrix(int[] matrix, int w, int n) {
        if ((matrix.length % w) != 0 || matrix.length < 4) {
            throw new IllegalArgumentException("Переданный массив не является матрицей, матрица должна иметь монимальную " +
                    "длинну 4 и делится на ширину матрицы без остатка.");
        }
        int h = matrix.length / w;
        int[] temp = Arrays.copyOf(matrix, matrix.length);
        for (int rotation = 0; rotation < n; rotation++) {
            int newW = h;
            int newH = w;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    matrix[i + j * h] = temp[w - j - 1 + i * w];
                }
            }
            w = newW;
            h = newH;
            System.arraycopy(matrix, 0, temp, 0, matrix.length);
        }
    }

    /**
     * Напечатать матрицу
     * @param matrix матрица
     * @param width  ширина матрицы
     */
    public static void printMatrix(int[] matrix, int width) {
        var builder = new StringBuilder();
        var max = String.valueOf(Arrays.stream(matrix).max().orElse(0)).length();
        for (int i = 0; i < matrix.length; i += width) {
            for (int j = 0; j < width; j++) {
                builder.append(StringUtil.pad(String.valueOf(matrix[i + j]), max, "0", true));
                if (j + 1 < width) {
                    builder.append(" ");
                }
            }
            if (i + 1 < matrix.length) {
                builder.append("\n");
            }
        }
        log.info("\n" + builder);
    }
}
