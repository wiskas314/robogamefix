package gui.state;

/**
 * Интерфейс определяющий возможность объекта сохранять и восстанавливать состояние
 */
public interface Stateful {
    /**
     * Сохраняет текущее состояние объекта в хранилище
     */
    void  saveState(StateStorage storage);

    /**
     * Восстанавливает состояние из хранилища
     */
    void restoreState(StateStorage storage);
}
