package gui;

import gui.state.StateStorage;
import gui.state.Stateful;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер локализации сообщений приложения
 */
public class Localization implements Stateful {
    /**
     * Единственный экземпляр модуля локализации
     */
    private static Localization instance;
    private static final String BUNDLE_BASE_NAME = "messages";
    /**
     * Текущий загруженный пакет ресурсов
     */
    private ResourceBundle bundle;
    /**
     * Активная локаль приложения
     */
    private Locale currentLocale;
    /**
     * Кеш скомпилированных шаблонов
     */
    private final Map<String, MessageFormat> messageFormatCache = new ConcurrentHashMap<>();

    private Localization() {
        currentLocale = Locale.forLanguageTag("ru");
        loadBundle();
    }

    /**
     * Возвращает уникальный экземпляр класса
     */
    public static Localization getInstance() {
        if (instance == null) {
            instance = new Localization();
        }
        return instance;
    }

    /**
     * Перезагружает пакет ресурсов
     */
    private void loadBundle() {
        bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);
        messageFormatCache.clear();
    }

    /**
     * Устанавливает новую локаль на основе языкового тега
     */
    public void setLocaleFromTag(String tag) {
        if (tag != null && !tag.isEmpty()) {
            Locale locale = Locale.forLanguageTag(tag);
            if (!currentLocale.equals(locale)) {
                currentLocale = locale;
                loadBundle();
            }
        }
    }

    /**
     * Получает локализованную строку по ключу из текущего пакета ресурсов
     */
    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    /**
     * Форматирует шаблон сообщения
     */
    public String format(String key, Object... args) {
        String pattern = getString(key);
        MessageFormat mf = messageFormatCache.computeIfAbsent(
                pattern, p -> new MessageFormat(p, currentLocale));
        return mf.format(args);
    }

    @Override
    public void saveState(StateStorage storage) {
        storage.put("tag", currentLocale.toLanguageTag());
    }

    @Override
    public void restoreState(StateStorage storage) {
        String tag = storage.get("tag");
        setLocaleFromTag(tag);
    }
}
