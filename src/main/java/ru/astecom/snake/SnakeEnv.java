package ru.astecom.snake;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import ru.astecom.support.AgentStats;

/**
 * Агент для игры в змейку
 */
public class SnakeEnv implements MDP<SnakeGameState, Integer, DiscreteSpace> {

    /** Статистика агента */
    public final AgentStats<Integer> stats;

    /** Игра в змейку */
    private final SnakeGame game;

    /** Генератор действий */
    private final DiscreteSpace discreteSpace;

    /** Статистика максимальной длинны змейки */
    private final AgentStats.AgentStat<Integer> maxSnakeLength;

    /** Статистика средней длинны змейки */
    private final AgentStats.AgentStat<Integer> avgSnakeLength;

    /** Статистика количества шагов */
    private final AgentStats.AgentStat<Integer> stepsCount;

    /** Состояние игрового поля */
    private final ArrayObservationSpace<SnakeGameState> gameArrayObservationSpace;

    /** Последнее действие */
    private SnakeGame.Action lastAction;

    /**
     * Конструктор
     * @param game игра в змейку
     */
    public SnakeEnv(SnakeGame game) {
        this.game = game;
        this.stats = new AgentStats<>();
        this.discreteSpace = new DiscreteSpace(3);
        this.gameArrayObservationSpace = new ArrayObservationSpace<>(new int[] { (int) Math.pow(game.getState().getDistance() * 2 + 1, 2) });
        this.stepsCount = stats.get("SnakeSteps").setStat(0).setDescription("Количество шагов тренеровки");
        this.maxSnakeLength = stats.get("MaxSnakeLength").setStat(0).setDescription("Максимальная длинна змейки");
        this.avgSnakeLength = stats.get("AvgSnakeLength").setStat(0).setDescription("Средняя длинна змейки")
                .addPrintPreProcessor(stat -> stat / stepsCount.getStat());
    }

    /**
     * Получить статистику агента
     * @return статистика агнета
     */
    public AgentStats<Integer> getStats() {
        return stats;
    }

    @Override
    public ObservationSpace<SnakeGameState> getObservationSpace() {
        return gameArrayObservationSpace;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return discreteSpace;
    }

    @Override
    public SnakeGameState reset() {
        return game.reset();
    }

    @Override
    public void close() {

    }

    @Override
    public StepReply<SnakeGameState> step(Integer integer) {
        var action = SnakeGame.Action.encode(integer);
        var result = game.step(action);
        var reward = getReward(result);
        if (action == lastAction) {
            reward -= 1;
        }
        lastAction = action;
        var snake = game.getState().getSnake();
        stepsCount.reduceState(e -> e + 1);
        maxSnakeLength.reduceState(e -> Math.max(e, snake.getLength()));
        avgSnakeLength.reduceState(e -> e + snake.getLength());
        return new StepReply<>(game.getState(), reward, result.isGameEnd(), null);
    }

    /**
     * Получить вознаграждение
     * @param result результат шага
     * @return вознаграждение
     */
    public int getReward(SnakeGame.StepResult result) {
        var state = game.getState();
        var snake = state.getSnake();
        return switch (result) {
            case SELF_ACCIDENT, ACCIDENT -> -20;
            case APPLE_EATEN -> snake.getLength() * 10;
            case CONTINUE -> 0;
        };
    }

    @Override
    public boolean isDone() {
        return game.getLastResult().isGameEnd();
    }

    @Override
    public MDP<SnakeGameState, Integer, DiscreteSpace> newInstance() {
        return new SnakeEnv(game);
    }
}
