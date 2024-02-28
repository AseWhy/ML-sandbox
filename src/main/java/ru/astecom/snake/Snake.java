package ru.astecom.snake;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Класс змеи
 */
public class Snake implements GameObject {

    /** Идентификатор объекта */
    public static final int OBJECT_ID = 2;

    /** Идентификатор объекта */
    public static final int HEAD_OBJECT_ID = 3;

    /** Клетки змеи */
    private final LinkedList<Point> cells;

    /** Состояние игры в змейку */
    private final SnakeGameState state;

    /** Направление движения змейки */
    private SnakeGame.Direction direction;

    /** Точки коллизии */
    private final Set<String> collision;

    /**
     * Создать новый объект змеи
     * @param x x координата головы змейки
     * @param y y координата головы змейки
     * @param state состояние игры в змейку
     */
    public Snake(int x, int y, SnakeGameState state) {
        this.cells = new LinkedList<>();
        this.collision = new HashSet<>();
        this.cells.add(new Point(x, y));
        this.state = state;
        this.direction = SnakeGame.Direction.NORTH;
    }

    /**
     * Сбросить состояние игры
     */
    public void reset() {
        var width = state.getWidth();
        var height = state.getHeight();
        clearCollision();
        cells.clear();
        cells.add(new Point(width /  2, height / 2));
        direction = SnakeGame.Direction.NORTH;
    }

    /**
     * Добавить сегмент змейки
     */
    public void addSegment() {
        var width = state.getWidth();
        var height = state.getHeight();
        var tail = cells.getLast();
        var newTail = new Point(tail.x, tail.y);
        if (newTail.x + 1 < width && !containsCollision(newTail.x + 1, newTail.y)) {
            newTail.x += 1;
        } else if (newTail.x - 1 >= 0  && !containsCollision(newTail.x - 1, newTail.y)) {
            newTail.x -= 1;
        } else if (newTail.y + 1 < height && !containsCollision(newTail.x, newTail.y + 1)) {
            newTail.y += 1;
        } else if (newTail.y - 1 >= 0 && !containsCollision(newTail.x, newTail.y - 1)) {
            newTail.y -= 1;
        }
        cells.add(newTail);
    }

    /**
     * Получить направление змеи
     * @return направление куда ползет змея
     */
    public SnakeGame.Direction getDirection() {
        return direction;
    }

    /**
     * Получить длинну змейки
     * @return длинна змейки
     */
    public int getLength() {
        return cells.size();
    }

    /**
     * Получить положение головы змейки
     * @return положение головы змейки
     */
    public Point getSnakeHead() {
        return cells.getFirst();
    }

    /**
     * Выполнить действие
     * @param action действие
     * @return результат действия
     */
    public SnakeGame.StepResult step(SnakeGame.Action action) {
        var iterator = cells.iterator();
        var current = iterator.next();
        var prevX = current.x;
        var prevY = current.y;
        computeDirection(action);
        computePosition(current);
        clearCollision();
        writeCollision(prevX, prevY);
        while (iterator.hasNext()) {
            var next = iterator.next();
            var nextX = next.x;
            var nextY = next.y;
            next.x = prevX;
            next.y = prevY;
            writeCollision(next.x, next.y);
            prevX = nextX;
            prevY = nextY;
        }
        return checkAccident();
    }

    @Override
    public void applyMatrix(int[] matrix) {
        int width = state.getWidth();
        var head = getSnakeHead();
        for (var point: cells) {
            if (!isInBounds(point)) {
                continue;
            }
            matrix[point.x + point.y * width] = point == head ? HEAD_OBJECT_ID : OBJECT_ID;
        }
    }

    @Override
    public void resetMatrix(int[] matrix) {
        int width = state.getWidth();
        for (var point: cells) {
            if (!isInBounds(point)) {
                continue;
            }
            matrix[point.x + point.y * width] = 0;
        }
    }

    /**
     * Очистить список пересечений
     */
    private void clearCollision() {
        collision.clear();
    }

    /**
     * Получить колизию точки
     * @param x x координата колизии
     * @param y y координата колизии
     */
    private boolean containsCollision(int x, int y) {
        return collision.contains(x + ":" + y);
    }

    /**
     * Добавить точку колизии
     * @param x x координата колизии
     * @param y y координата колизии
     */
    private void writeCollision(int x, int y) {
        collision.add(x + ":" + y);
    }

    /**
     * Обновить позицию точки
     * @param point точка
     */
    private void computePosition(Point point) {
        switch (direction) {
            case EAST -> point.x++;
            case WEST -> point.x--;
            case NORTH -> point.y++;
            case SOUTH -> point.y--;
        }
    }

    /**
     * Обновить направление движения змейки
     * @param action действие юзера
     */
    private void computeDirection(SnakeGame.Action action) {
        direction = switch (action) {
            case FORWARD -> direction;
            case LEFT -> direction.getLeft();
            case RIGHT -> direction.getRight();
        };
    }

    /**
     * Проверить змейку на столкновения
     * @return результат шага
     */
    private SnakeGame.StepResult checkAccident() {
        var head = getSnakeHead();
        if (isInBounds(head)) {
            if (containsCollision(head.x, head.y)) {
                return SnakeGame.StepResult.SELF_ACCIDENT;
            } else {
                return SnakeGame.StepResult.CONTINUE;
            }
        } else {
            return SnakeGame.StepResult.ACCIDENT;
        }
    }

    /**
     * Проверить, что точка находится на карте
     * @param point точка для проверки
     * @return true если точка находится на карте
     */
    private boolean isInBounds(Point point) {
        var width = state.getWidth();
        var height = state.getHeight();
        return point.x >= 0 && point.x < width && point.y >= 0 && point.y < height;
    }
}
