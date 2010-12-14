package aztec;

import broker.service.com.protocol.Message;

/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Dec 14, 2010
 * Time: 12:28:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class Player extends MovingObject {
    public static final double VELOCITY = 1.0;
    
    private boolean isOnline;
    private int score;
    private int coins;

    public Player(int id, double x, double y) {
        super(id, 0, "Player", x, y, 1, 1, 0);
        this.isOnline = true;
        this.score = 0;
        this.coins = 0;
    }

    public void appendToMessage(Message message, int counter) {
        this.setCounter(counter);
        super.appendToMessage(message);
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getScore() {
        return score;
    }

    public int getCoins() {
        return coins;
    }
}
