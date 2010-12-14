package aztec;

import broker.service.com.protocol.Message;

/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Dec 13, 2010
 * Time: 11:17:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StaticAztecObject extends AztecObject {

    protected StaticAztecObject(int id, String type, double x, double y, double width, double height) {
        super(id, type, x, y, width, height);
    }

    @Override
    public void appendToMessage(Message msg) {
        if (getId() != 0) {
            msg.setDouble("x" + getType() + getId(), getX());
            msg.setDouble("y" + getType() + getId(), getY());
        } else {
            msg.setDouble("x" + getType(), getX());
            msg.setDouble("y" + getType(), getY());
        }
    }
}
