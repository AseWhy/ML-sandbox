//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.astecom.tic_tac;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Агент для игры в крестики нолики
 */
public class TicTacAgent implements MDP<TicTacState, Integer, DiscreteSpace> {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(TicTacAgent.class);

    /** Менеджер игры в крестики нолики */
    private final TicTacGame game;

    /** Генератор действий */
    private final DiscreteSpace discreteSpace;

    /** Номер игрока агента */
    private final int agentPlayer;

    /** Состояние игрового поля */
    private final ArrayObservationSpace<TicTacState> gameArrayObservationSpace;

    /**
     * Конструктор
     * @param game игры в крестики нолики
     */
    public TicTacAgent(TicTacGame game, int agentPlayer) {
        this.game = game;
        this.agentPlayer = agentPlayer;
        // 9 действий
        this.discreteSpace = new DiscreteSpace(9);
        // поле размеро 3 на 3 - 9
        this.gameArrayObservationSpace = new ArrayObservationSpace<>(new int[] { 9 });
    }

    /**
     * Ожидать пока наступит ход агента
     */
    public void waitPlayerStep() {
        while (game.getStep() != agentPlayer) {
            // Ждем дальше
            continue;
        }
    }

    @Override
    public ObservationSpace<TicTacState> getObservationSpace() {
        return gameArrayObservationSpace;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return discreteSpace;
    }

    @Override
    public TicTacState reset() {
        var state = new TicTacState();
        game.setState(state, 1);
        return state;
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<TicTacState> step(Integer i) {
        int x = i % 3, y = i / 3;
        var result = game.step(x, y, agentPlayer);
        return new StepReply<>(game.getState(), getReward(x, y, result), result.isEndGame(), null);
    }

    @Override
    public boolean isDone() {
        return game.checkWinner().isEndGame();
    }

    /**
     * Посчитать вознаграждение за результат шага
     * @param x      координата x шага
     * @param y      координата y шага
     * @param result результат шага
     * @return вознаграждение
     */
    private double getReward(int x, int y, TicTacGame.StepResult result) {
        if (result == TicTacGame.StepResult.CONTINUE) {
            double reward = 0;
            int allied = 0, enemy = 0;
            // Добавляем вознаградение, если соседние клетки заняты текущим игроком и отнимает если заняты другим игроком
            for (int ox = -1; ox < 2; ox++) {
                for (int oy = -1; oy < 2; oy++) {
                    int cx = x - ox;
                    int cy = y - oy;
                    if (cx < 0 || cx > 2 || cy < 0 || cy > 2 || cx == x && cy == y) {
                        continue;
                    }
                    var val = this.game.getCell(cx, cy);
                    if (val == agentPlayer) {
                        reward += 1;
                        allied ++;
                    } else if (val != 0) {
                        reward -= 0.5;
                        enemy++;
                    }
                }
            }
            log.info("Ход защитан, вознаграждение агенту: {}. За {} союзных и {} вражеских клеток", reward, allied, enemy);
            return reward;
        }
        return switch (result) {
            case WINNER_1 -> -2.5;
            case WINNER_2 -> 5;
            case BLOCKED -> -5;
            case DRAW -> 0;
            default -> throw new IllegalStateException("Неожиданное значение: " + result);
        };
    }

    @Override
    public MDP<TicTacState, Integer, DiscreteSpace> newInstance() {
        return new TicTacAgent(game, agentPlayer);
    }
}
