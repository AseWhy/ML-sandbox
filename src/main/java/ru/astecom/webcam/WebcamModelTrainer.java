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
    private final WebcamNumberDetector detector = new WebcamNumberDetector();

    @Override
    public String getModelName() {
        return "Классификация числовых изображений от 0 до 9";
    }

    @Override
    public void printMetrics() {
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
        detector.save(ApplicationHelper.getModelPath(MODEL_FILE_NAME));
    }
}
