package gui;

/**
 * Контроллер движения робота отвечающий за вычисление новой позиции и направления
 */
public class RobotUpdater {
    private final RobotModel model;
    /**
     * Максимальная линейная скорость робота
     */
    private static final double MAX_VELOCITY = 100;
    /**
     * Максимальная угловая скорость поворота робота
     */
    private static final double MAX_ANGULAR_VELOCITY = 3;
    /**
     * Максимально допустимый шаг времени для одного кадра
     */
    private static final long MAX_DT_MILLIS = 50;

    public RobotUpdater(RobotModel model) {
        this.model = model;
    }

    /**
     * Обновляет состояние робота
     */
    public void update(long dtMillis) {
        dtMillis = Math.min(dtMillis, MAX_DT_MILLIS);
        double dt = dtMillis / 1000.0;

        double distance = RobotModel.distance(model.getTargetX(), model.getTargetY(),
                model.getX(), model.getY());
        if (distance < 0.5) return;

        double angleToTarget = RobotModel.angleTo(model.getX(), model.getY(),
                model.getTargetX(), model.getTargetY());
        double angleDiff = angleToTarget - model.getDirection();
        angleDiff = RobotModel.asNormalizedRadians(angleDiff);
        if (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;

        double angularVelocity = 0;
        if (Math.abs(angleDiff) > 0.0001) {
            angularVelocity = Math.signum(angleDiff) * MAX_ANGULAR_VELOCITY;
            double maxNeeded = Math.abs(angleDiff) / dt;
            angularVelocity = Math.copySign(
                    Math.min(Math.abs(angularVelocity), maxNeeded),
                    angularVelocity);
        }
        double velocity = MAX_VELOCITY;

        double newDirection = model.getDirection() + angularVelocity * dt;
        double newX = model.getX() + velocity * dt * Math.cos(newDirection);
        double newY = model.getY() + velocity * dt * Math.sin(newDirection);

        model.setPosition(newX, newY);
        model.setDirection(newDirection);
    }
}
