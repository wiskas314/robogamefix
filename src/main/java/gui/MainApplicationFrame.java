package gui;

import gui.state.ApplicationStateManager;
import gui.state.StateStorage;
import gui.state.Stateful;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TimerTask;
import java.util.Timer;


public class MainApplicationFrame extends JFrame implements Stateful
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    RobotModel robotModel = new RobotModel();
    private RobotController robotController;
    private Timer timer;
    private final ApplicationStateManager stateManager;
    public MainApplicationFrame(ApplicationStateManager stateManager) {
        this.stateManager = stateManager;
        robotController = new RobotController(robotModel);
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        GameVisualizer visualizer = new GameVisualizer(robotModel);
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow(visualizer);
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        RobotCoordinatesWindow coordsWindow = new RobotCoordinatesWindow(robotModel);
        addWindow(coordsWindow);

        stateManager.register("main",this);
        stateManager.register("log",logWindow);
        stateManager.register("game",gameWindow);
        stateManager.register("coords", coordsWindow);

        startModelUpdateTimer();

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                confirmAndExit();
            }
        });
    }


    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создаёт основную панель меню приложения
     */
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createExitMenu());
        return menuBar;
    }

    /**
     * Создание кнопки отвечающие за схему
     */
    private JMenu createLookAndFeelMenu(){
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);
        return lookAndFeelMenu;
    }

    /**
     * Создает меню отвечающие за тесты и логи
     */
    private JMenu createTestMenu(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);
        return testMenu;
    }

    /**
     * Создает меню отвечающее за выход из игры
     */
    private JMenu createExitMenu(){
        JMenu fileMenu = new JMenu("Выход");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exitItem = new JMenuItem("Выход",KeyEvent.VK_X);
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener((event)->{
            Toolkit.getDefaultToolkit().getSystemEventQueue().
                    postEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
        });
        fileMenu.add(exitItem);
        return fileMenu;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    private void confirmAndExit(){
        String[] options = {"Да","Нет"};
        int confirm = JOptionPane.showOptionDialog(MainApplicationFrame.this,
                "Действительно выйти?", "Подтверждение выхода",
                JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,
                null,options,options[1]);
        if(confirm==0){
            timer.cancel();
            stateManager.save();
            for(JInternalFrame frame : desktopPane.getAllFrames()){
                if(frame!=null) frame.dispose();
            }
            MainApplicationFrame.this.setVisible(false);
            MainApplicationFrame.this.dispose();
            System.exit(0);
        }
    }

    /**
     * Запускает таймер для периодического обновления модели робота
     */
    private void startModelUpdateTimer()
    {
        timer = new Timer("model-update", true);

        timer.schedule(new TimerTask()
        {
            private long lastTime = System.currentTimeMillis();

            @Override
            public void run()
            {
                long now = System.currentTimeMillis();
                long dtMillis = now - lastTime;

                robotController.update(dtMillis);

                lastTime = now;
            }
        }, 0, 10);
    }

    @Override
    public void saveState(StateStorage storage){
        storage.put("x",getX());
        storage.put("y",getY());
        storage.put("width",getWidth());
        storage.put("height",getHeight());
        storage.put("extendedState", getExtendedState());
    }

    @Override
    public void restoreState(StateStorage storage)
    {
        int x = storage.getInt("x", 50);
        int y = storage.getInt("y", 50);
        int w = storage.getInt("width", Toolkit.getDefaultToolkit().getScreenSize().width - 100);
        int h = storage.getInt("height", Toolkit.getDefaultToolkit().getScreenSize().height - 100);
        setBounds(x, y, w, h);

        int state = storage.getInt("extendedState", Frame.MAXIMIZED_BOTH);
        setExtendedState(state);
    }

    public void restoreAllStates()
    {
        stateManager.restore();
    }
}
