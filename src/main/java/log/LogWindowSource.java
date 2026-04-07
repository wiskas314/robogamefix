package log;

import java.util.*;

public class LogWindowSource {
    private int queueLength;

    private CircularBuffer messages;
    private final Set<LogChangeListener> listeners = Collections.newSetFromMap(new WeakHashMap<>());


    public LogWindowSource(int iQueueLength) {
        queueLength = iQueueLength;
        messages = new CircularBuffer(iQueueLength);
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        messages.add(entry);
        List<LogChangeListener> activeListeners;
        synchronized (listeners) {
            activeListeners = new ArrayList<>(listeners);
        }
        for (LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        return messages.getRange(startFrom, count);
    }

    public Iterable<LogEntry> all() {
        return messages.getAll();
    }

    /**
     * Потокобезопасный кольцевой буфер для хранения записей лога
     */
    private static class CircularBuffer {
        private final LogEntry[] buffer;
        /**
         * Указатель на следующую позицию для записи
         */
        private int head = 0;
        /**
         * Количество элементов
         */
        private int size = 0;

        public CircularBuffer(int capacity) {
            this.buffer = new LogEntry[capacity];
        }

        /**
         * Добавляет новую запись в буфер
         */
        public synchronized void add(LogEntry element) {
            buffer[head] = element;
            head = (head + 1) % buffer.length;
            if (size < buffer.length) {
                size++;
            }
        }

        /**
         * Возвращает текущее количество записей в буфере
         */
        public synchronized int size() {
            return size;
        }

        /**
         * Возвращает диапазон записей из буфера, начиная с заданного логического индекса
         */
        public synchronized List<LogEntry> getRange(int start, int count) {
            if (start < 0 || start >= size) return Collections.emptyList();

            int actualCount = Math.min(count, size - start);
            List<LogEntry> result = new ArrayList<>(actualCount);

            int firstElemIndex = (head - size + buffer.length) % buffer.length;

            for (int i = 0; i < actualCount; i++) {
                int index = (firstElemIndex + start + i) % buffer.length;
                result.add(buffer[index]);
            }
            return result;
        }

        /**
         * Возвращает все записи, хранящиеся в буфере
         */
        public synchronized List<LogEntry> getAll() {
            return getRange(0, size);
        }
    }
}
