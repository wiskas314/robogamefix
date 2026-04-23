package gui.state;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Абстракция данных для хранения и восстановления состояния окон приложения.
 */
public class StateStorage {
    /**
     * Общий словарь всех состояний приложения
     */
    private final Map<String, String> targetMap;
    /**
     * Префикс ключей для текущего компонента
     */
    private final String prefix;

    /**
     * Создаёт отфильтрованное хранилище с указанным префиксом.
     */
    public StateStorage(Map<String, String> targetMap, String prefix) {
        this.targetMap = targetMap;
        this.prefix = (prefix == null || prefix.isEmpty()) ? "" :
                prefix.endsWith(".") ? prefix : prefix + ".";
    }

    /**
     * Сохраняет значение в хранилище.
     */
    public void put(String key, Object value) {
        if (value != null)
            targetMap.put(prefix + key, value.toString());
    }

    /**
     * Возвращает строковое значение по ключу.
     */
    public String get(String key) {
        return targetMap.get(prefix + key);
    }

    /**
     * Возвращает целочисленное значение или значение по умолчанию.
     */
    public int getInt(String key, int defaultValue) {
        String val = get(key);
        if (val == null)
            return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Возвращает булево значение или значение по умолчанию.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        if (val == null) return defaultValue;
        return Boolean.parseBoolean(val);
    }

}
