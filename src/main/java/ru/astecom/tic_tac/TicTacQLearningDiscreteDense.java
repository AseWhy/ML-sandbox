package ru.astecom.tic_tac;

import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.observation.Observation;

/**
 * Площадка для тренеровки нейросети
 */
public class TicTacQLearningDiscreteDense extends QLearningDiscreteDense<TicTacState> {

    /** Агент */
    private final TicTacAgent agent;

    /** Конструктор */
    public TicTacQLearningDiscreteDense(TicTacAgent agent, DQNDenseNetworkConfiguration netConf, QLearningConfiguration conf) {
        super(agent, netConf, conf);
        this.agent = agent;
    }

    @Override
    protected QLStepReturn<Observation> trainStep(Observation obs) {
        // Ожидаем ход игрока
        agent.waitPlayerStep();
        // Делаем ход
        return super.trainStep(obs);
    }
}
