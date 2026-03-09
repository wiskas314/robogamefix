package gui.state;

import java.util.Map;

/**
 * Абстракция данных для хранения и восстановления состояния окон приложения.
 */
public class StateStorage {
    /**
     * Общий словарь всех состояний приложения
     */
    private final Map<String, String> map;
    /**
     * Префикс ключей для текущего компонента
     */
    private final String prefix;

    /**
     * Создаёт отфильтрованное хранилище с указанным префиксом.
     */
    public StateStorage(Map<String, String> map, String prefix) {
        this.map = map;
        this.prefix = (prefix == null || prefix.isEmpty()) ? "" :
                prefix.endsWith(".") ? prefix : prefix + ".";
    }

    /**
     * Сохраняет строковое значение в хранилище.
     */
    public void put(String key, String value) {
        if (value != null)
            map.put(prefix + key, value);
    }

    /**
     * Сохраняет целочисленное значение.
     */
    public void put(String key, int value) {
        put(key, Integer.toString(value));
    }

    /**
     * Сохраняет булево значение.
     */
    public void put(String key, boolean value) {
        put(key, Boolean.toString(value));
    }

    /**
     * Возвращает строковое значение по ключу.
     */
    public String get(String key) {
        return map.get(prefix + key);
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
