package aztec;

import broker.service.com.protocol.Message;

/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Dec 14, 2010
 * Time: 12:04:57 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class MovableObject extends AztecObject {
    private int heading;
    private double velocity;
    private int counter;

    protected MovableObject(int id, int counter, String type, double x, double y, double width, double height, int heading) {
        super(id, type, x, y, width, height);
        this.heading = heading;
        this.velocity = 0;
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    protected void setCounter(int counter) {
        this.counter = counter;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getVelocity() {
        return velocity;
    }

    @Override
    public void appendToMessage(Message message) {
        message.setInteger(getType().toLowerCase() + counter, getId());
        message.setDouble("x" + getType() + counter, getX());
        message.setDouble("y" + getType() + counter, getY());
        message.setInteger("h" + getType() + counter, getHeading());
    }

    public void appendToStatusMessage(Message message) {
        message.setInteger("id", getId());
        message.setDouble("x", getX());
        message.setDouble("y", getY());
        message.setInteger("h", getHeading());
    }
}
