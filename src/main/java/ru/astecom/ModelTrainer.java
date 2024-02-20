package ru.astecom;

/**
 * Интерфейс обучения модели
 */
public interface ModelTrainer {

    /**
     * Получить название модели
     * @return название модели
     */
    String getModelName();

    /**
     * Напечатать метрики обучения модели
     */
    void printMetrics();

    /**
     * Запуск процесса обучени модели
     */
    void train();

    /**
     * Сохраняет модель в папку с моделями
     */
    void save();
}
