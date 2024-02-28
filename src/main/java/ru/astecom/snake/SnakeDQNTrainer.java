package ru.astecom.snake;

import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.IHistoryProcessor;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astecom.ModelTrainer;
import ru.astecom.support.ApplicationHelper;

import java.io.IOException;

/**
 * Тренер модели игры в змейку
 */
public class SnakeDQNTrainer implements ModelTrainer {

    /** Название файла модели */
    public static final String MODEL_FILE_NAME = "snake.bin";

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(SnakeDQNTrainer.class);

    /** Конфигурация обучения с подкреплением */
    private QLearningDiscreteDense<SnakeGameState> dnq;

    /** Агент */
    private SnakeEnv env;

    /** Количество шагов на 1 эпоху */
    private final int stepsPerEpoch;

    /** Количество игр */
    private final int maxGames;

    /** Дистанция виденья змейки */
    private final int visionDistance;

    /** Ширина игрового поля */
    private final int width;

    /** Высота игрового поля */
    private final int height;

    /**
     * Конструктор тренера
     * @param stepsPerEpoch  количество шагов на 1 эпоху
     * @param maxGames       количество игр
     * @param width          ширина игрового поля
     * @param height         высота игрового поля
     * @param visionDistance дистанция виденья змейки
     */
    public SnakeDQNTrainer(int stepsPerEpoch, int maxGames, int visionDistance, int width, int height) {
        this.stepsPerEpoch = stepsPerEpoch;
        this.maxGames = maxGames;
        this.visionDistance = visionDistance;
        this.width = width;
        this.height = height;
    }

    @Override
    public String getModelName() {
        return "Змейка [Обучение с подкреплением]";
    }

    public void start(SnakeEnv env) {
        this.env = env;
        this.dnq = new QLearningDiscreteDense<>(env, getDNCConfiguration(), getQLCConfiguration(stepsPerEpoch, maxGames));
        this.dnq.setHistoryProcessor(getHPConfiguration());
    }

    @Override
    public void start() {
        start(new SnakeEnv(new SnakeGame(new SnakeGameState(visionDistance, width, height))));
    }

    @Override
    public void printMetrics() {
        checkDnqAvailable();
        env.getStats().print();
    }

    @Override
    public void train() {
        checkDnqAvailable();
        dnq.train();
    }

    @Override
    public void save() {
        checkDnqAvailable();
        try {
            dnq.getPolicy().save(ApplicationHelper.getModelPath(MODEL_FILE_NAME).toString());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении модели игры в змейку", e);
        }
    }

    @Override
    public void close() {
        this.env = null;
        this.dnq = null;
    }

    /**
     * Проверить доступность детектора
     */
    private void checkDnqAvailable() {
        if (dnq == null) {
            throw new RuntimeException("dnq - null. Метод start был запущен?");
        }
    }

    /**
     * Получить конфигурацию обработчика истории
     * @return конфигурация обработчика истории
     */
    private static HistoryProcessor.Configuration getHPConfiguration() {
        return IHistoryProcessor.Configuration.builder()
            .rescaledWidth(10)
            .rescaledHeight(10)
            .croppingHeight(5)
            .croppingWidth(5)
        .build();
    }

    /**
     * Получить конфигурацию нейросети
     * @return конфигурация нейросети
     */
    private static DQNDenseNetworkConfiguration getDNCConfiguration() {
        return DQNDenseNetworkConfiguration.builder()
            .learningRate(0.01)
            .numLayers(8)
        .build();
    }

    /**
     * Получить конифгурацию обучения с подкреплением
     * @param stepsPerEpoch количество шагов на 1 эпоху
     * @param maxGames      количество игр
     * @return конифгурацию обучения с подкреплением
     */
    private static QLearningConfiguration getQLCConfiguration(int stepsPerEpoch, int maxGames) {
        return QLearningConfiguration.builder()
            .seed(1L)
            .maxEpochStep(stepsPerEpoch)
            .maxStep(stepsPerEpoch * maxGames)
            .expRepMaxSize(50000)
            .updateStart(0)
            .rewardFactor(0.01)
            .gamma(0.999)
            .errorClamp(1.0)
            .doubleDQN(true)
            .targetDqnUpdateFreq(500)
            .batchSize(32)
            .minEpsilon(0.0)
            .epsilonNbStep(128)
        .build();
    }
}
