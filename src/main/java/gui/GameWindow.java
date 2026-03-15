package gui;

import gui.state.StateStorage;
import gui.state.Stateful;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame implements Stateful
{
    public GameWindow(JComponent component)
    {
        super("Игровое поле", true, true, true, true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void saveState(StateStorage storage)
    {
        storage.put("x", getX());
        storage.put("y", getY());
        storage.put("width", getWidth());
        storage.put("height", getHeight());
        storage.put("maximized", isMaximum());
        storage.put("iconified", isIcon());
    }

    @Override
    public void restoreState(StateStorage storage)
    {
        int x = storage.getInt("x", 100);
        int y = storage.getInt("y", 100);
        int width = storage.getInt("width", 400);
        int height = storage.getInt("height", 400);
        setBounds(x, y, width, height);

        if (storage.getBoolean("maximized", false))
            try { setMaximum(true); } catch (Exception ignored) {}
        else if (storage.getBoolean("iconified", false))
            try { setIcon(true); } catch (Exception ignored) {}
    }
}
