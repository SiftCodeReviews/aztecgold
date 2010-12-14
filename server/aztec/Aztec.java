package aztec;

/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Dec 14, 2010
 * Time: 12:22:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class Aztec extends MovingObject {
    public Aztec(int id, double x, double y, int heading) {
        super(-id, id, "Aztec", x, y, 1, 1, heading);
    }
}
