package ru.astecom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astecom.support.ApplicationConfigure;
import ru.astecom.webcam.WebcamModelTrainer;

/**
 * Задача трененовки моделей
 */
public class Trainer {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(Trainer.class);

    /** Конфигурация приложения */
    static {
        ApplicationConfigure.configure();
    }

    /** Набор обучающих обхектов */
    private static final ModelTrainer[] MODEL_TRAINERS = { new WebcamModelTrainer() };

    /**
     * Точка входа в процесс
     * @param args аргументы
     */
    public static void main(String[] args) {
        log.info("Начинаю обучение {} моделей", MODEL_TRAINERS.length);
        for (var current : MODEL_TRAINERS) {
            log.info("Запуск обучения модели '{}'", current.getModelName());
            current.train();
            log.info("Обучение завершено, вывожу метрики тестирования точности для модели '{}'", current.getModelName());
            current.printMetrics();
            log.info("Сохраняю модель '{}'", current.getModelName());
            current.save();
        }
        log.info("Обучение завершено.");
    }
}
