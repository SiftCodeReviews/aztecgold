package aztec;

import broker.service.com.protocol.Message;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Dec 13, 2010
 * Time: 11:08:56 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AztecObject {
    private double x;
    private double y;
    private final int id;
    private final String type;
    private final double width;
    private final double height;

    protected AztecObject(int id, String type, double x, double y, double width, double height) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Rectangle getRectangle() {
        return new Rectangle((int)(x - width / 2), (int)(y - height / 2), (int)width, (int)height);
    }

    public abstract void appendToMessage(Message message);
}
