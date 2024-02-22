package ru.astecom.webcam;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astecom.ModelTrainer;
import ru.astecom.support.ApplicationHelper;

import java.io.IOException;

/**
 * Задача трененовки модели распознования чисел
 */
public class WebcamModelTrainer implements ModelTrainer {

    /** Название файла модели */
    public static final String MODEL_FILE_NAME = "number-classification.bin";

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(WebcamModelTrainer.class);

    /** Обнаружитель чисел */
    private WebcamNumberDetector detector;

    @Override
    public String getModelName() {
        return "Классификация числовых изображений от 0 до 9";
    }

    @Override
    public void start() {
        detector = new WebcamNumberDetector();
    }

    @Override
    public void printMetrics() {
        checkDetectorAvailable();
        try {
            var ds = new MnistDataSetIterator(128, false, (int) WebcamNumberDetector.RNG_SEED);
            try {
                log.info(detector.evaluate(ds).stats());
            } finally {
                ds.close();
            }
        } catch (IOException e) {
            log.error("Ошибка при попытке получить статистику обучения", e);
        }
    }

    @Override
    public void train() {
        checkDetectorAvailable();
        try {
            var ds = new MnistDataSetIterator(128, true, (int) WebcamNumberDetector.RNG_SEED);
            try {
                detector.train(ds, 10);
            } finally {
                ds.close();
            }
        } catch (IOException e) {
            log.error("Ошибка при обучении модели на базе МНИСТ", e);
        }
    }

    @Override
    public void save() {
        checkDetectorAvailable();
        detector.save(ApplicationHelper.getModelPath(MODEL_FILE_NAME));
    }

    @Override
    public void close() {
        detector = null;
    }

    /**
     * Проверить доступность детектора
     */
    private void checkDetectorAvailable() {
        if (detector == null) {
            throw new RuntimeException("detector - null. Метод start был запущен?");
        }
    }
}
