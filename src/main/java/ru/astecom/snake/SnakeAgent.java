package ru.astecom.snake;

import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.nd4j.linalg.factory.Nd4j;
import ru.astecom.support.ApplicationHelper;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Агент для игры в змейку
 */
public class SnakeAgent {

    /** Тренер агента */
    private final SnakeDQNTrainer trainer;

    /**
     * Конструктор агента
     * @param stepsPerEpoch  количество шагов на 1 эпоху
     * @param maxGames       количество игр
     * @param width          ширина игрового поля
     * @param height         высота игрового поля
     * @param visionDistance дистанция виденья змейки
     */
    public SnakeAgent(int stepsPerEpoch, int maxGames, int visionDistance, int width, int height) {
        trainer = new SnakeDQNTrainer(stepsPerEpoch, maxGames, visionDistance, width, height);
    }

    /**
     * Запустить тренеровку агента
     * @param game игра
     * @return завершаемый поток
     */
    public CompletableFuture<Void> liveTraining(SnakeGame game) {
        trainer.start(new SnakeEnv(game));
        return CompletableFuture.runAsync(trainer::train)
                .thenRun(trainer::close);
    }

    /**
     * Запустить игру в змейку, используя обученную модель
     * @param game игра
     * @return завершаемый поток
     */
    public CompletableFuture<Void> playTrained(SnakeGame game) {
        try {
            var politic = DQNPolicy.load(ApplicationHelper.getModelPath(SnakeDQNTrainer.MODEL_FILE_NAME).toString());
            return CompletableFuture.runAsync(() -> {
                while (true) {
                    int integer = politic.nextAction(Nd4j.expandDims(game.getState().getData(), 0));
                    var action = SnakeGame.Action.encode(integer);
                    var result = game.step(action);
                    if (result.isGameEnd()) {
                        game.reset();
                    }
                    try {
                        Thread.sleep(32);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
