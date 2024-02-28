package ru.astecom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astecom.snake.SnakeDQNTrainer;
import ru.astecom.snake.SnakeRenderer;
import ru.astecom.support.ApplicationConfigure;
import ru.astecom.webcam.WebcamModelTrainer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

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
    private static final ModelTrainer[] MODEL_TRAINERS = {
            new WebcamModelTrainer(),
            new SnakeDQNTrainer(100, 100, SnakeRenderer.SNAKE_VISION_DISTANCE, SnakeRenderer.SNAKE_FIELD_WIDTH, SnakeRenderer.SNAKE_FIELD_HEIGHT) };

    /**
     * Точка входа в процесс
     * @param args аргументы
     */
    public static void main(String[] args) {
        log.info("Начинаю обучение {} моделей", MODEL_TRAINERS.length);
        var pool = Executors.newSingleThreadExecutor(r -> new Thread(r, "TrainingThread"));
        for (var current : MODEL_TRAINERS) {
            current.start();
            log.info("Запуск обучения модели '{}'", current.getModelName());
            CompletableFuture.runAsync(current::train, pool).join();
            log.info("Обучение завершено, вывожу метрики тестирования точности для модели '{}'", current.getModelName());
            current.printMetrics();
            log.info("Сохраняю модель '{}'", current.getModelName());
            current.save();
            current.close();
        }
        log.info("Обучение завершено.");
    }
}
