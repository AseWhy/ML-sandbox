package ru.astecom.tic_tac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Игра в крестики нолики
 */
public class TicTacGame {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(TicTacGame.class);

    /** Ячейки, заполнение которых считает выйгрышным */
    private static final int[][] WINNER_CELLS = {
        {0, 4, 8},
        {2, 4, 6},
        {0, 1, 2},
        {3, 4, 5},
        {6, 7, 8},
        {0, 3, 6},
        {1, 4, 7},
        {2, 5, 8}
    };

    /** Список слушателей событий шага */
    private final List<Consumer<StepResult>> stepEventListeners;

    /** Состояние игры */
    private TicTacState state;

    /** Игрок который сейчас ходит */
    private int step;

    /**
     * Конструктор игры в крестики нолики
     * @param state состояние игры
     * @param step  игрок который сейчас ходит
     */
    public TicTacGame(TicTacState state, int step) {
        this.state = state;
        this.step = step;
        this.stepEventListeners = new ArrayList<>();
    }

    /**
     * Добавить слушатель
     * @param listener слушатель события шага
     */
    public void addStepEventListener(Consumer<StepResult> listener) {
        stepEventListeners.add(listener);
    }

    /**
     * Установить новое состояние игры
     * @param state состояние игры
     * @param step  игрок который сейчас ходит
     */
    public void setState(TicTacState state, int step) {
        this.state = state;
        this.step = step;
        dispatchStepEventListeners(StepResult.CONTINUE);
    }

    /**
     * Получить состояние игры
     * @return состояние игры
     */
    public TicTacState getState() {
        return state;
    }

    /**
     * Получить игрока который делает ход
     * @return игрок который делает ход
     */
    public synchronized int getStep() {
        return step;
    }

    /**
     * Получить данные по координате x y
     * @param x x координата хода
     * @param y y координата хода
     * @return 1 - если клетка занята первым игроком, 2 - если клетка занята вторым игркомо, 0 - если клетка пустая
     */
    public int getCell(int x, int y) {
        return this.state.getField()[y * 3 + x];
    }

    /**
     * Сделать ход на клетку x y игроком player
     * @param x x координата хода
     * @param y y координата хода
     * @param player игрок совершающий ход: 1, 2
     * @return true если ход выполнен
     */
    public synchronized StepResult step(int x, int y, int player) {
        if (player != step) {
            log.info("Ход игрока {} блокирован. Сейчас не его ход.", player);
            return StepResult.BLOCKED;
        }
        int[] field = state.getField();
        int index = y * 3 + x;
        if (field[index] != 0) {
            log.info("Ход игрока {} блокирован. Т.к. ячейка {}:{} занята", player, x, y);
            return StepResult.BLOCKED;
        }
        field[index] = player;
        var result = checkWinner();
        log.info("Игрок {} сделал ход в ячейку {}:{}. Результат: {}", player, x, y, result);
        step = step == 1 ? 2 : 1;
        log.info("Следующий ход делает игрок {}", step);
        dispatchStepEventListeners(result);
        return result;
    }

    /**
     * Получить результат шага
     * @return результат шага
     */
    public StepResult checkWinner() {
        int[] field = state.getField();
        // Выйгрышные ячейки
        for (int i = 1; i < 3; i++) {
            for (var cross : WINNER_CELLS) {
                var all = true;
                for (int index : cross) {
                    if (field[index] == i) {
                        continue;
                    }
                    all = false;
                    break;
                }
                if (all) {
                    return StepResult.getWinner(i);
                }
            }
        }
        // Не осталось свободных ячеек
        if (Arrays.stream(field).noneMatch(e -> e == 0)) {
            return StepResult.DRAW;
        }
        // Игра продолжается
        return StepResult.CONTINUE;
    }

    /**
     * Вызвать все слушатели события шага
     * @param result резульат шага
     */
    private void dispatchStepEventListeners(StepResult result) {
        stepEventListeners.forEach(e -> e.accept(result));
    }

    /**
     * Результат выполнения шага
     */
    public enum StepResult {

        /** Выйграл игрок 1 */
        WINNER_1(true),

        /** Выйграл игрок 2 */
        WINNER_2(true),

        /** Ничья */
        DRAW(true),

        /** Ход блокирован другим игроком */
        BLOCKED(false),

        /** Игра продолжается */
        CONTINUE(false);

        /** Флаг обозначающий конец игры */
        private final boolean endGame;

        /**
         * Конструктор результата шага
         * @param endGame флаг обозначающий конец игры
         */
        StepResult(boolean endGame) {
            this.endGame = endGame;
        }

        /**
         * Получить флаг обозначающий конец игры
         * @return флаг обозначающий конец игры
         */
        public boolean isEndGame() {
            return endGame;
        }

        /**
         * Получить победителя по индексу
         * @param index индекс победителя
         * @return резултат шага с победой одного из игроков
         */
        static StepResult getWinner(int index) {
            return index == 1 ? WINNER_1 : WINNER_2;
        }
    }
}
