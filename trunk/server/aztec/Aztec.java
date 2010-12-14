package aztec;

/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Dec 14, 2010
 * Time: 12:22:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class Aztec extends MovableObject {
    public final static double VELOCITY = 0.2;

    private double homeX;
    private double homeY;

    public Aztec(int id, double x, double y, int heading) {
        super(-id, id, "Aztec", x, y, 1, 1, heading);
        this.homeX = x;
        this.homeY = y;
    }

    public double getHomeX() {
        return homeX;
    }

    public double getHomeY() {
        return homeY;
    }

    public boolean isHome() {
        double dx = getX() - homeX;
        double dy = getY() - homeY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < VELOCITY) {
            return true;
        }
        return false;
    }

    @Override
    public void move(double x, double y) {
        super.move(x, y);
    }
}
