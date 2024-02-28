package ru.astecom.snake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Игра в змейку
 */
public class SnakeGame {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(SnakeGame.class);

    /** Состояние игры */
    private final SnakeGameState state;

    /** Результат последнего шага */
    private StepResult lastResult;

    /** Список слушателей событий шага */
    private final List<Consumer<StepResult>> stepEventListeners;

    /**
     * Конструктор
     * @param state состояние игры
     */
    public SnakeGame(SnakeGameState state) {
        this.state = state;
        this.lastResult = StepResult.CONTINUE;
        this.stepEventListeners = new ArrayList<>();
    }

    /**
     * Сделать шаг игры
     * @param action тип шага
     */
    public StepResult step(Action action) {
        var snake = state.getSnake();
        var apple = state.getApple();
        state.beforeStep();
        lastResult = snake.step(action);
        if (lastResult == StepResult.CONTINUE) {
            lastResult = apple.step(snake.getSnakeHead());
            if (lastResult == StepResult.APPLE_EATEN) {
                snake.addSegment();
            }
        }
        state.afterStep();
        dispatchStepEventListeners(lastResult);
        return lastResult;
    }

    /**
     * Сбросить состояние игры
     * @return состояние игры
     */
    public SnakeGameState reset() {
        log.info("Состояние игры сброшено: {}, длинна змейки до зброса: {}", lastResult, state.getSnake().getLength());
        state.beforeStep();
        state.reset();
        state.afterStep();
        dispatchStepEventListeners(lastResult);
        lastResult = StepResult.CONTINUE;
        return state;
    }

    /**
     * Получить результат последнего шага
     * @return результат последнего шага
     */
    public StepResult getLastResult() {
        return lastResult;
    }

    /**
     * Получить состояние игры
     * @return состояние игры
     */
    public SnakeGameState getState() {
        return state;
    }

    /**
     * Добавить слушатель
     * @param listener слушатель события шага
     */
    public void addStepEventListener(Consumer<StepResult> listener) {
        stepEventListeners.add(listener);
    }

    /**
     * Вызвать все слушатели события шага
     * @param result резульат шага
     */
    private void dispatchStepEventListeners(StepResult result) {
        stepEventListeners.forEach(e -> e.accept(result));
    }

    /**
     * Результат шага
     */
    public enum StepResult {

        /** Столкновение смим с собой */
        SELF_ACCIDENT(true),

        /** Столкнопвение */
        ACCIDENT(true),

        /** Яблоко было съедено */
        APPLE_EATEN(false),

        /** Продолжаем */
        CONTINUE(false);

        /** Признак окончания игры */
        private final boolean gameEnd;

        /**
         * Конструктор
         * @param gameEnd признак окончания игры
         */
        StepResult(boolean gameEnd) {
            this.gameEnd = gameEnd;
        }

        /**
         * Получить признак окончания игры
         * @return признак окончания игры
         */
        public boolean isGameEnd() {
            return gameEnd;
        }
    }

    /**
     * Действие
     */
    public enum Action {

        /** Повернуть влево */
        LEFT,

        /** Повернуть вправо */
        RIGHT,

        /** Прямо */
        FORWARD;

        /**
         * Получить действие из цифрового эквивалента
         * @param integer цифровой эквивалент действия
         * @return действие
         */
        public static Action encode(int integer) {
            var action = SnakeGame.Action.FORWARD;
            if (integer == 1) {
                action = SnakeGame.Action.LEFT;
            } else if (integer == 2) {
                action = SnakeGame.Action.RIGHT;
            }
            return action;
        }
    }

    /**
     * Направление движения
     */
    public enum Direction {

        /** Запад < */
        WEST("SOUTH", "NORTH"),

        /** Север ^ */
        NORTH("WEST", "EAST"),

        /** Восток > */
        EAST("NORTH", "SOUTH"),

        /** Юг */
        SOUTH("EAST", "WEST");

        /** Направление, которое будет если повернуть влево */
        private final String left;

        /** Направление, которое будет если повернуть вправо */
        private final String right;

        /**
         * Конструктор
         * @param left  направление, которое будет если повернуть влево
         * @param right направление, которое будет если повернуть вправо
         */
        Direction(String left, String right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Получить направление, которое будет если повернуть влево
         * @return направление, которое будет если повернуть влево
         */
        public Direction getLeft() {
            return Direction.valueOf(left);
        }

        /**
         * Получить направление, которое будет если повернуть вправо
         * @return направление, которое будет если повернуть вправо
         */
        public Direction getRight() {
            return Direction.valueOf(right);
        }
    }
}
