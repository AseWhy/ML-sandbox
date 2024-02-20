package ru.astecom.webcam;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import ru.astecom.support.DeepLearningUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Класс для обнаружения номеров на входящих изображениях
 */
public final class WebcamNumberDetector {

    /** Сид */
    public static final long RNG_SEED = 123L;

    /** Загрузчик нативного изображения */
    private final NativeImageLoader loader;

    /** Помощьник изминения размера загруженного изображения */
    private final ImagePreProcessingScaler scale;

    /** Модель */
    private final MultiLayerNetwork model;

    /**
     * Конструктор, который принимает высоту и ширину входных данных
     * @param width  ширина входного изображения
     * @param height высота входного изображения
     */
    public WebcamNumberDetector(int width, int height) {
        this.loader = new NativeImageLoader(height, width, 1L);
        this.scale = new ImagePreProcessingScaler(0, 1);
        this.model = new MultiLayerNetwork(makeConfig(width, height));
    }

    /**
     * Конструктор, который принимает высоту и ширину входных данных
     * @param width  ширина входного изображения
     * @param height высота входного изображения
     * @param pathToModel путь до модели
     */
    public WebcamNumberDetector(int width, int height, Path pathToModel) {
        this.loader = new NativeImageLoader(height, width, 1L);
        this.scale = new ImagePreProcessingScaler(0, 1);
        this.model = load(pathToModel);
    }

    /**
     * Конструктор без параметров
     */
    public WebcamNumberDetector() {
        this(28, 28);
    }

    /**
     * Обучать модель
     * @param iterator итератор обучающей выборки
     * @param epochs   количество эпох обучения
     */
    public void train(DataSetIterator iterator, int epochs) {
        this.model.init();
        this.model.setListeners(new ScoreIterationListener(1));
        this.model.fit(iterator, epochs);
    }

    /**
     * Получить параметры выполнения классификации
     * @param iterator итератор проверяющей выборки
     * @return результат выполнения
     */
    public Evaluation evaluate(DataSetIterator iterator) {
        return model.evaluate(iterator);
    }

    /**
     * Сохранить модель
     * @param path путь до модели
     */
    public void save(Path path) {
        try {
            this.model.save(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохраненении модели", e);
        }
    }

    /**
     * Обучать модель на примере набор данных МНИСТ
     */
    public void trainMnist() {
        try {
            var ds = new MnistDataSetIterator(128, true, (int) RNG_SEED);
            try {
                this.train(ds, 5);
            } finally {
                ds.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обучении модели на базе МНИСТ", e);
        }
    }

    /**
     * Обнаружить число в переданном избражении
     * @param bitmap изображение
     * @return число от 0 до 9 или -1 если ничего не найдено
     */
    public int detect(BufferedImage bitmap) {
        try (var matrix = this.loader.asMatrix(bitmap)) {
            var reshaped = matrix.reshape(1L, 784L);
            this.scale.transform(reshaped);
            var result = model.output(reshaped);
            return DeepLearningUtils.getIndexOfLargestValue(new double[] {result.getDouble(0,0),result.getDouble(0,1),result.getDouble(0,2),
                    result.getDouble(0,3),result.getDouble(0,4),result.getDouble(0,5),result.getDouble(0,6),
                    result.getDouble(0,7),result.getDouble(0,8),result.getDouble(0,9)});
        } catch (IOException e) {
            throw new RuntimeException("Ошибка распознавания числа переданного в изображении", e);
        }
    }

    /**
     * Загрузить натренерованную модель
     * @param pathToModel путь до модели
     * @return загруженная модель
     */
    private static MultiLayerNetwork load(Path pathToModel) {
        try {
            return MultiLayerNetwork.load(pathToModel.toFile(), false);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохраненении модели", e);
        }
    }

    /**
     * Создать конфигурацию нейронной сети
     * @param width  ширина входного изображения
     * @param height высота входного изображения
     * @return конфигурация нейронной сети
     */
    private static MultiLayerConfiguration makeConfig(int width, int height) {
        return new NeuralNetConfiguration.Builder()
            .seed(RNG_SEED)
            .l2(1.0E-4)
            .updater(new Nesterovs(0.006, 0.9))
            .list().layer(
                new DenseLayer.Builder()
                    .nIn(width * height)
                    .nOut(1000)
                    .activation(Activation.RELU)
                    .weightInit(WeightInit.XAVIER)
                .build()
            )
            .layer(
                new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nIn(1000)
                    .nOut(10)
                    .activation(Activation.SOFTMAX)
                    .weightInit(WeightInit.XAVIER)
                .build()
            )
        .build();
    }
}
