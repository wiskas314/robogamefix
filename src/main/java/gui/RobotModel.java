package gui;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RobotModel {

    public static final String PROP_POSITION = "position";
    public static final String PROP_TARGET = "target";
    public static final String PROP_DIRECTION = "direction";

    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;

    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    protected void setTargetPosition(int x, int y) {
        this.targetPositionX = x;
        this.targetPositionY = y;
        pcs.firePropertyChange(PROP_TARGET, null, null);
    }

    protected void setPosition(double x, double y) {
        robotPositionX = x;
        robotPositionY = y;
        pcs.firePropertyChange(PROP_POSITION, null, null);
    }
    protected void setDirection(double direction) {
        double oldDir = robotDirection;
        robotDirection = asNormalizedRadians(direction);
        if (Math.abs(oldDir - robotDirection) > 1e-6) {
            pcs.firePropertyChange(PROP_DIRECTION, oldDir, robotDirection);
        }
    }
    protected static double asNormalizedRadians(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) angle += 2 * Math.PI;
        return angle;
    }

    protected static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    protected static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    public double getX() {
        return robotPositionX;
    }

    public double getY() {
        return robotPositionY;
    }

    public double getDirection() {
        return robotDirection;
    }

    public int getTargetX() {
        return targetPositionX;
    }

    public int getTargetY() {
        return targetPositionY;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }
}
