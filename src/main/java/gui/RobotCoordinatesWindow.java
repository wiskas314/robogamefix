package gui;

import gui.state.StateStorage;
import gui.state.Stateful;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Окно для отображения текущих координат
 */
public class RobotCoordinatesWindow extends JInternalFrame
        implements PropertyChangeListener, Stateful {
    private final RobotModel model;
    /**
     * Метка для отображения координаты X робота.
     */
    private final JLabel lblRobotX  = new JLabel("X: —");
    /**
     * Метка для отображения координаты Y робота.
     */
    private final JLabel lblRobotY  = new JLabel("Y: —");
    /**
     * Метка для отображения направления движения робота в градусах.
     */
    private final JLabel lblDir     = new JLabel("Направление: —°");
    /**
     * Метка для отображения координат целевой точки.
     */
    private final JLabel lblTarget  = new JLabel("Цель: (—, —)");

    /**
     * Создаёт новое окно координат робота
     */
    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.model = model;

        model.addPropertyChangeListener(RobotModel.PROP_POSITION, this);
        model.addPropertyChangeListener(RobotModel.PROP_TARGET,   this);
        model.addPropertyChangeListener(RobotModel.PROP_DIRECTION,this);

        initUI();
        updateDisplay();

        setSize(240, 160);
        setLocation(450, 20);
    }

    /**
     * Создает пользовательский интерфейс
     */
    private void initUI(){
        JPanel p =new JPanel(new GridLayout(0,1,4,4));
        p.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));

        p.add(lblRobotX);
        p.add(lblRobotY);
        p.add(lblDir);
        p.add(lblTarget);

        getContentPane().add(p);
    }

    /**
     * Обновляет отображаемые данные
     */
    private void updateDisplay() {
        lblRobotX.setText(String.format("X: %.1f", model.getX()));
        lblRobotY.setText(String.format("Y: %.1f", model.getY()));
        double deg = Math.toDegrees(model.getDirection());
        lblDir.setText(String.format("Направление: %.1f°", deg));
        lblTarget.setText(String.format("Цель: (%d, %d)", model.getTargetX(), model.getTargetY()));
    }

    @Override
    public void saveState(StateStorage st) {
        st.put("x",      getX());
        st.put("y",      getY());
        st.put("width",  getWidth());
        st.put("height", getHeight());
        st.put("maximized",    isMaximum());
        st.put("iconified",   isIcon());
    }

    @Override
    public void restoreState(StateStorage st) {
        int x  = st.getInt("x", 450);
        int y  = st.getInt("y", 20);
        int w  = st.getInt("width", 240);
        int h  = st.getInt("height", 160);
        setBounds(x, y, w, h);

        if (st.getBoolean("maximized", false))
            try { setMaximum(true); } catch (Exception ignored) {}
        else if (st.getBoolean("iconified", false))
            try { setIcon(true); } catch (Exception ignored) {}
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        EventQueue.invokeLater(this::updateDisplay);
    }
}
