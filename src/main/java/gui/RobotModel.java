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

    private static final double MAX_VELOCITY = 100;
    private static final double MAX_ANGULAR_VELOCITY = 3;

    protected void setTargetPosition(int x, int y) {
        this.targetPositionX = x;
        this.targetPositionY = y;
        pcs.firePropertyChange(PROP_TARGET, null, null);
    }

    protected void onModelUpdateEvent(long dtMillis) {
        double dt = dtMillis / 1000.0;

        double distance = distance(targetPositionX, targetPositionY,
                robotPositionX, robotPositionY);
        if (distance < 0.5) return;
        double angleToTarget = angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);
        double angleDiff = angleToTarget - robotDirection;
        angleDiff = asNormalizedRadians(angleDiff);
        if (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
        double angularVelocity = 0;
        if (Math.abs(angleDiff) > 0.0001) {
            angularVelocity = Math.signum(angleDiff) * MAX_ANGULAR_VELOCITY;
            double maxNeeded = Math.abs(angleDiff) / dt;
            angularVelocity = Math.copySign(Math.min(Math.abs(angularVelocity), maxNeeded), angularVelocity);
        }
        double velocity = MAX_VELOCITY;

        double newDirection = robotDirection + angularVelocity * dt;
        double newX = robotPositionX + velocity * dt * Math.cos(newDirection);
        double newY = robotPositionY + velocity * dt * Math.sin(newDirection);


        double oldX = robotPositionX, oldY = robotPositionY, oldDir = robotDirection;

        robotPositionX = newX;
        robotPositionY = newY;
        robotDirection = asNormalizedRadians(newDirection);

        pcs.firePropertyChange(PROP_POSITION, null, null);
        if (Math.abs(oldDir - robotDirection) > 1e-6) {
            pcs.firePropertyChange(PROP_DIRECTION, oldDir, robotDirection);
        }
    }

    private static double asNormalizedRadians(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) angle += 2 * Math.PI;
        return angle;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
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

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
