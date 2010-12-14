package aztec;

import broker.service.com.protocol.Message;

/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Dec 13, 2010
 * Time: 11:53:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Coin extends AztecObject {
    private final int counter;

    public Coin(int id, int counter, double x, double y) {
        super(id, "Coin", x, y, 1, 1);
        this.counter = counter;
    }

    @Override
    public void appendToMessage(Message message) {
        message.setInteger("coin" + counter, getId());
        message.setDouble("xCoin" + counter, getX());
        message.setDouble("yCoin" + counter, getY());
    }
}
