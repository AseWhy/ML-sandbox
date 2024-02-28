package ru.astecom.support;

import com.twelvemonkeys.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astecom.snake.SnakeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Статистика агента
 * @param <T> тип статистики
 */
public class AgentStats<T> {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(SnakeUtils.class);

    /** Карта, где ключ это наименование а значение это значение статистики */
    private final Map<String, AgentStat<T>> stats;

    /**
     * Конструктор
     */
    public AgentStats() {
        this.stats = new HashMap<>();
    }

    /**
     * Получить значение статистики агента
     * @param name наименование статистики
     * @return значение статистики
     */
    public AgentStat<T> get(String name) {
        return stats.computeIfAbsent(name, key -> new AgentStat<>(name));
    }

    /**
     * Выводит статистику в лог
     */
    public void print() {
        log.info(String.format("\n%s", getPrintString()));
    }

    /**
     * Получить строку для печати
     * @return строка для печати
     */
    public String getPrintString() {
        int maxRowLength = 0;
        var temp = new HashMap<String, String>();
        for (var current : stats.values()) {
            var result = current.getDescription() != null ? String.format("%s [%s]: ", current.getName(), current.getDescription())
                    : current.getName();
            var rowLength = (result + current.getStat()).length();
            if (rowLength > maxRowLength) {
                maxRowLength = rowLength;
            }
            temp.put(result, current.toString());
        }
        var builder = new StringBuilder();
        var iterator = temp.entrySet().iterator();
        while (iterator.hasNext()) {
            var current = iterator.next();
            var label = current.getKey();
            var value = current.getValue();
            builder.append(label);
            builder.append(StringUtil.pad("", maxRowLength - label.length() - value.length(), " ", false));
            builder.append(value);
            if (iterator.hasNext()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * Значение статистики агента
     * @param <T> тип статистики
     */
    public static class AgentStat<T> {

        /**
         * Наименование статистики агента
         */
        private final String name;

        /** Пре обработчики перед печатью */
        private final List<Function<T, T>> printPreProcessors;

        /** Описание статистики */
        private String description;

        /** Значение статистики агента */
        private T stat;

        /**
         * Создать новый объект стаистики
         * @param name наименование
         */
        public AgentStat(String name) {
            this.name = name;
            this.stat = null;
            this.description = null;
            this.printPreProcessors = new ArrayList<>();
        }

        /**
         * Добавить препроцессор для печатаемого значения
         * @param preprocessor препроцессор
         * @return состояние статистики
         */
        public AgentStat<T> addPrintPreProcessor(Function<T, T> preprocessor) {
            this.printPreProcessors.add(preprocessor);
            return this;
        }

        /**
         * Установить описание статистики
         * @param description описание статистики
         * @return состояние статистики
         */
        public AgentStat<T> setDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Установить новое значение стистики
         * @param stat значение статистики
         * @return состояние статистики
         */
        public AgentStat<T> setStat(T stat) {
            this.stat = stat;
            return this;
        }

        /**
         * Обновить состояние статистики
         * @param updater функция обновления
         * @return состояние статистики
         */
        public AgentStat<T> reduceState(Function<T, T> updater) {
            stat = updater.apply(stat);
            return this;
        }

        /**
         * Получить описание статистики
         * @return описание статистики
         */
        public String getDescription() {
            return description;
        }

        /**
         * Получить значение статистики
         * @return значение статистики
         */
        public T getStat() {
            return stat;
        }

        /**
         * Получить наименование статистики
         * @return наименование статистики
         */
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            if (stat == null) {
                return "-";
            }
            T local = stat;
            for (var current : printPreProcessors) {
                local = current.apply(local);
            }
            return String.valueOf(local);
        }
    }
}
