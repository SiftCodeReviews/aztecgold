import aztec.MovableObject;
import aztec.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Dec 14, 2010
 * Time: 1:28:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class ObjectMover extends Thread {
    private static ObjectMover instance;

    private final List<MovableObject> objectsToMove;
    private static final int DELAY = 50;

    public static ObjectMover getInstance() {
        if (instance == null) {
            instance = new ObjectMover();
            instance.start();
        }

        return instance;
    }

    private ObjectMover() {
        objectsToMove = new ArrayList<MovableObject>();
    }

    public void run() {
        while (true) {
            synchronized (objectsToMove) {
                while (objectsToMove.size() == 0) {
                    try {
                        objectsToMove.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            synchronized (objectsToMove) {
                for (int i = 0; i < objectsToMove.size(); i++) {
                    MovableObject movableObject = objectsToMove.get(i);
                    if (moveObject(movableObject)) {
                        i--;
                    }
                }
            }
            Server.getInstance().updateAztecs();
            Server.getInstance().processAztecPlayerCollisions();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void addObject(MovableObject object) {
        synchronized (objectsToMove) {
            if (!objectsToMove.contains(object)) {
                objectsToMove.add(object);
                objectsToMove.notify();
            }
        }
    }

    public void removeObject(MovableObject object) {
        synchronized (objectsToMove) {
            objectsToMove.remove(object);
        }
    }

    private boolean moveObject(MovableObject object) {
        if (object.getVelocity() == 0) {
            return false;
        }
        double alpha = (double) object.getHeading() / 360 * 2 * Math.PI;
        double newX = object.getX() + object.getVelocity() * Math.sin(alpha);
        double newY = object.getY() + object.getVelocity() * Math.cos(alpha);

        if (object instanceof Player) {
            if (Server.getInstance().processCollision(newX, newY, object.getId())) {
                removeObject(object);
                return true;
            } else {
                object.move(newX, newY);
                Server.getInstance().doMovePlayer(object.getId());
                return false;
            }
        } else {
            object.move(newX, newY);
            Server.getInstance().doMoveAztec(object);
            return false;
        }
     
    }
}
