package gui;

import gui.state.StateStorage;
import gui.state.Stateful;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import javax.swing.*;
import java.awt.*;

public class LogWindow extends JInternalFrame implements LogChangeListener, Stateful
{
    private LogWindowSource logSource;
    private TextArea logContent;

    public LogWindow(LogWindowSource logSource)
    {
        super(Localization.getInstance().getString("window.log"), true, true, true, true);
        this.logSource = logSource;
        this.logSource.registerListener(this);
        this.logContent = new TextArea("");
        this.logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        logContent.setText(content.toString());
        logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
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
        int x = storage.getInt("x", 10);
        int y = storage.getInt("y", 10);
        int width = storage.getInt("width", 300);
        int height = storage.getInt("height", 800);
        setBounds(x, y, width, height);

        if (storage.getBoolean("maximized", false))
            try { setMaximum(true); } catch (Exception ignored) {}
        else if (storage.getBoolean("iconified", false))
            try { setIcon(true); } catch (Exception ignored) {}
    }
}
