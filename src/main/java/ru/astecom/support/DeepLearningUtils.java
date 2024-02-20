package ru.astecom.support;

/**
 * Помощьник для работы с нейросетями
 */
public class DeepLearningUtils {

    /**
     * Получить инлекс наибольшего значения в массиве чисел
     * @param array массив чисел
     * @return индекс наибольшего значения
     */
    public static int getIndexOfLargestValue( double[] array ) {
        if (array == null || array.length == 0) {
            return -1;
        }
        int largest = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[largest]) {
                largest = i;
            }
        }
        return largest;
    }
}
