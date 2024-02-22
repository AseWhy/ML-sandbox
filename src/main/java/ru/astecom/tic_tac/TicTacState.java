package ru.astecom.tic_tac;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Состояние игры в крестики нолики
 */
public class TicTacState implements Encodable {

    /** Игровое поле */
    private final int[] field;

    /**
     * Конструктор состояния
     */
    public TicTacState() {
        this.field = new int[9];
    }

    /**
     * Конструктор состояния
     * @param field игровое поле
     */
    public TicTacState(int[] field) {
        this.field = field;
    }

    /**
     * Получить игровое поле
     * @return игровое поле
     */
    public int[] getField() {
        return field;
    }

    @Override
    public double[] toArray() {
        return new double[0];
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        return Nd4j.createFromArray(field);
    }

    @Override
    public Encodable dup() {
        return new TicTacState(field);
    }
}
