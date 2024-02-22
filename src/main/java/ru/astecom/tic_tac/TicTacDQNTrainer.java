package ru.astecom.tic_tac;

import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.nd4j.linalg.learning.config.Nadam;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

/**
 * Тренер игры в крестики нолики
 */
public class TicTacDQNTrainer implements Closeable {

    /** Конфигурация обучения с подкреплением */
    private final TicTacQLearningDiscreteDense dnq;

    /** Обучающий поток */
    private CompletableFuture<Void> completableFuture;

    /**
     * Конструктор тренера
     * @param stepsPerEpoch количество шагов на 1 эпоху
     * @param maxGames      количество игр
     * @param agent         агент
     */
    public TicTacDQNTrainer(int stepsPerEpoch, int maxGames, TicTacAgent agent) {
        this.dnq = new TicTacQLearningDiscreteDense(agent, getDNCConfiguration(), getQLCConfiguration(stepsPerEpoch, maxGames));
    }

    /**
     * Запустить обучение
     */
    public void train() {
        if (completableFuture != null) {
            throw new RuntimeException("Обучение уже запущено");
        }
        completableFuture = CompletableFuture.runAsync(dnq::train)
                .thenRun(() -> completableFuture = null);
    }

    @Override
    public void close() {
        if (completableFuture != null) {
            completableFuture.cancel(true);
        }
    }

    /**
     * Получить конфигурацию нейросети
     * @return конфигурация нейросети
     */
    private static DQNDenseNetworkConfiguration getDNCConfiguration() {
        return DQNDenseNetworkConfiguration.builder()
            .updater(new Nadam(Math.pow(10, -3.5)))
            .numHiddenNodes(20)
            .numLayers(6)
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
            .updateStart(0)
            .rewardFactor(1.0)
            .gamma(0.999)
            .errorClamp(1.0)
            .batchSize(16)
            .minEpsilon(0.0)
            .epsilonNbStep(128)
            .expRepMaxSize(128 * 16)
        .build();
    }
}
