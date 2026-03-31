package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Контроллер игры
 */
public class GameController {

    private final RobotModel model;
    private final RobotController robotController;
    private final GameVisualizer visualizer;
    private Timer updateTimer;

    /**
     * Конструктор инициализирует компоненты игры
     */
    public GameController(RobotModel model, GameVisualizer visualizer) {
        this.model = model;
        this.visualizer = visualizer;
        this.robotController = new RobotController(model);

        setupMouseInput();
        startUpdateTimer();
    }

    /**
     * Обработчик событий мыши
     */
    private void setupMouseInput() {
        visualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                model.setTargetPosition(e.getPoint().x, e.getPoint().y);
            }
        });
    }

    /**
     * Запускает таймер для периодического обновления модели робота
     */
    private void startUpdateTimer() {
        updateTimer = new Timer("robot-update", true);
        updateTimer.schedule(new TimerTask() {
            private long lastTime = System.currentTimeMillis();

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long dt = now - lastTime;
                robotController.update(dt);
                lastTime = now;
            }
        }, 0, 10);
    }

    /**
     * Возвращает экземпляр таймера
     */
    public Timer getUpdateTimer() {
        return updateTimer;
    }
}