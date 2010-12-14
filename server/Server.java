/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Nov 20, 2010
 * Time: 4:18:34 PM
 * To change this template use File | Settings | File Templates.
 */

import aztec.*;
import broker.Broker;
import broker.object.*;
import broker.service.com.protocol.*;

import java.awt.Rectangle;
import java.util.*;

public class Server extends BrokerCallBack {
    private static Server instance = new Server();
    private static final double CHASE_DISTANCE = 11;

    public static Server getInstance() {
        return instance;
    }

    Broker broker = Broker.getInstance();

    List<AztecObject> staticObjects = new ArrayList<AztecObject>();
    List<Aztec> aztecs = new ArrayList<Aztec>();
    Map<Integer, Player> players = new HashMap<Integer, Player>();

    int numPlayers = 0;
    int numTrees = 25;
    int numHuts = 5;
    int numAztecs = 4;
    int numCoins = 50;

    private Server() {
        initializeStaticObjects();
        initializeAztecs();
    }

    private void initializeAztecs() {
        aztecs.add(new Aztec(1, -10, 10, 180));
        aztecs.add(new Aztec(2, -15, 15, 90));
        aztecs.add(new Aztec(3, -20, 20, 45));
        aztecs.add(new Aztec(4, -30, 30, 45));

        for (Aztec aztec : aztecs) {
            ObjectMover.getInstance().addObject(aztec);
        }
    }

    private void initializeStaticObjects() {
        staticObjects.add(new Tree(1, 1, -1));
        staticObjects.add(new Tree(2, 3, -3));
        staticObjects.add(new Tree(3, 5, -35));
        staticObjects.add(new Tree(4, -8, -28));
        staticObjects.add(new Tree(5, -15, -83));
        staticObjects.add(new Tree(6, -33, -14));
        staticObjects.add(new Tree(7, -33, -41));
        staticObjects.add(new Tree(8, 25, -5));
        staticObjects.add(new Tree(9, 38, -8));
        staticObjects.add(new Tree(10, 55, -15));
        staticObjects.add(new Tree(11, 41, -18));
        staticObjects.add(new Tree(12, 43, -31));
        staticObjects.add(new Tree(13, 35, -59));
        staticObjects.add(new Tree(14, 38, -84));
        staticObjects.add(new Tree(15, 15, -15));
        staticObjects.add(new Tree(16, 52, -19));
        staticObjects.add(new Tree(17, 37, -24));
        staticObjects.add(new Tree(18, 100, -51));
        staticObjects.add(new Tree(19, 85, -8));
        staticObjects.add(new Tree(20, 115, -15));
        staticObjects.add(new Tree(21, 120, -13));
        staticObjects.add(new Tree(22, 34, -3));
        staticObjects.add(new Tree(23, 52, -5));
        staticObjects.add(new Tree(24, 89, -74));
        staticObjects.add(new Tree(25, 64, -30));
        
        staticObjects.add(new Hut(1, -40, -40));
        staticObjects.add(new Hut(2, -15, -15));
        staticObjects.add(new Hut(3, -30, -30));
        staticObjects.add(new Hut(4, 0, -35));
        staticObjects.add(new Hut(5, 0, -20));

        staticObjects.add(new Fort(0, 70));
        staticObjects.add(new Chest(0, 50));

        staticObjects.add(new Coin(-101, 1, Math.random() * 10, Math.random() * 10));
        staticObjects.add(new Coin(-102, 2, Math.random() * 10, Math.random() * 10));
        staticObjects.add(new Coin(-103, 3, Math.random() * 10, Math.random() * 10));
        staticObjects.add(new Coin(-104, 4, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-105, 5, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-106, 6, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-107, 7, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-108, 8, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-109, 9, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-110, 10, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-111, 11, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-112, 12, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-113, 13, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-114, 14, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-115, 15, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-116, 16, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-117, 17, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-118, 18, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-119, 19, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-120, 20, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-121, 21, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-122, 22, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-123, 23, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-124, 24, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-125, 25, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-126, 26, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-127, 27, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-128, 28, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-129, 29, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-130, 30, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-131, 31, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-132, 32, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-133, 33, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-134, 34, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-135, 35, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-136, 36, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-137, 37, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-138, 38, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-139, 39, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-140, 40, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-141, 41, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-142, 42, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-143, 43, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-144, 44, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-145, 45, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-146, 46, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-147, 47, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-148, 48, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-149, 49, Math.random() * 100, Math.random() * 100));
        staticObjects.add(new Coin(-150, 50, Math.random() * 100, Math.random() * 100));
    }

    public synchronized Message initializeStage(int playerID) {

        Message msg = new Message(playerID);
        //init
        msg.setString("mid", "init");

        //trees
        msg.setInteger("numTrees", numTrees);
        //huts
        msg.setInteger("numHuts", numHuts);
        //coins
        msg.setInteger("numCoins", numCoins);
        //chest and fort
        for (AztecObject staticObject : staticObjects) {
            staticObject.appendToMessage(msg);
        }
        //number of players which are currently active
        msg.setInteger("numPlayers", numPlayers);

        int counter = 1;
        for (Player p : players.values()) {
            if (p.isOnline()) {
                p.appendToMessage(msg, counter++);
            }
        }
        //aztecs
        msg.setInteger("numAztecs", numAztecs);

        for (Aztec aztec : aztecs) {
            aztec.appendToMessage(msg);
        }
        return msg;
    }

    public synchronized boolean processCollision(double x, double y, int id) {

        Player player = players.get(id);

        Rectangle playerRectangle = new Rectangle((int) x - 1, (int) y - 1, 2, 2);

        for (AztecObject staticObject : staticObjects) {
            if (playerRectangle.intersects(staticObject.getRectangle())) {
                //System.out.println("[Server] Collision with " + staticObject.getType() + " detected");
                String type = staticObject.getType();
                if ("Tree".equals(type) || "Hut".equals(type) || "Fort".equals(type)) {
                    return true;
                } else if ("Chest".equals(type)) {
                    player.convertCoinsIntoScore();
                    broker.send(playerStatus(player.getId()));
                    return true;
                } else if ("Coin".equals(type)) {
                    player.addCoin();
                    broker.send(playerStatus(id));
                    double randX = Math.random() * 100.0;
                    double randY = Math.random() * 100.0;
                    staticObject.move(randX, randY);
                    broker.sendBroadcast(moveCoin(player.getId(), staticObject));
                    return false;
                } else {
                    System.out.println("WEIRD TYPE OF OBJECT: " + type);
                    return true;
                }
            }
        }

        //checking the collision with aztects
        for (Aztec aztec : aztecs) {
            if (playerRectangle.intersects(aztec.getRectangle())) {
                player.move(5, 50);
                player.setHeading(0);
                player.removeAllCoins();
                broker.send(playerStatus(id));
                broker.sendBroadcast(movePlayer(id));

                return true;
            }
        }

        return false;
    }

    public synchronized Message receive(Message request, Message response) {
        try {
            //if player wants to movePlayer
            if (request.getString("mid").equals("moveReq")) {
                int id = request.getObjectID();
                int heading = request.getInteger("h");

                Player player = players.get(id);

                if (heading == 1000) {
                    ObjectMover.getInstance().removeObject(player);
                    player.setVelocity(0);
                } else {
                    player.setHeading(heading);
                    player.setVelocity(Player.VELOCITY);
                    ObjectMover.getInstance().addObject(player);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void doMovePlayer(int playerId) {
        broker.sendBroadcast(movePlayer(playerId));
    }

    public Message movePlayer(int id) {

        Message msg = new Message(id);
        msg.setString("mid", "move");
        Player player = players.get(id);
        player.appendToStatusMessage(msg);

        return msg;
    }

    public Message moveCoin(int playerId, AztecObject coin) {
        Message msg = new Message(playerId);
        msg.setString("mid", "move");
        msg.setInteger("id", coin.getId());
        msg.setDouble("x", coin.getX());
        msg.setDouble("y", coin.getY());

        return msg;
    }

    public void doMoveAztec(MovableObject aztec) {
        Message msg = new Message(aztec.getCounter());

        msg.setString("mid", "move");
        msg.setInteger("id", aztec.getId());
        msg.setDouble("x", aztec.getX());
        msg.setDouble("y", aztec.getY());
        msg.setInteger("h", aztec.getHeading());
        broker.sendBroadcast(msg);
    }

    public synchronized void objectJoined(int id) {
        System.out.println("[Server] objectJoined() ----> id = " + id);
        numPlayers++;

        Player player = players.get(id);
        if (player == null) {
            player = new Player(id, 5, 50);
            players.put(id, player);
        } else {
            player.setOnline(true);
        }

        broker.send(initializeStage(id));   //send initialized world back to the client
        broker.send(playerStatus(id));   //send the last saved score and coins collected
        broker.sendBroadcast(playerJoined(id));  //broadcast to everyone about new player
    }

    public void objectLeft(int id) {
        System.out.println("[Server] objectLeft() ----> id = " + id);
        players.get(id).setOnline(false);

        numPlayers--;
        broker.sendBroadcast(playerLeft(id));  //tell everyone that player left
    }

    public synchronized Message playerJoined(int id) {
        Message msg = new Message(id);
        msg.setString("mid", "playerJoined");
        players.get(id).appendToStatusMessage(msg);

        return msg;
    }

    public Message playerLeft(int id) {

        Message msg = new Message(id);
        msg.setString("mid", "playerLeft");
        msg.setInteger("id", id);
        return msg;
    }

    public synchronized Message playerStatus(int id) {
        Player player = players.get(id);
        Message msg = new Message(id);

        msg.setString("mid", "playerStatus");
        msg.setInteger("score", player.getScore());
        msg.setInteger("coins", player.getCoins());

        return msg;
    }

    public static void main(String args[]) {

        /* this needs to be done */
        Broker b = Broker.getInstance();
        b.registerCallBack(Server.getInstance());
        b.setAuthenticationData("AztecServer", "test");
        b.init();
    }

    public void updateAztecs() {
        for (Aztec aztec : aztecs) {
            Player closestPlayer = null;
            double distance = 10000000;
            for (Player player : players.values()) {
                double x = player.getX() - aztec.getX();
                double y = player.getY() - aztec.getY();
                double value = Math.sqrt(x * x + y * y);
                if (value < distance) {
                    distance = value;
                    closestPlayer = player;
                }
            }
            if (distance < CHASE_DISTANCE && closestPlayer != null) {
                int heading = getHeadingToCoordinates(aztec, closestPlayer.getX(), closestPlayer.getY());
                aztec.setHeading(heading);
                aztec.setVelocity(Aztec.VELOCITY);
            } else if (!aztec.isHome()) {
                int heading = getHeadingToCoordinates(aztec, aztec.getHomeX(), aztec.getHomeY());
                aztec.setHeading(heading);
                aztec.setVelocity(Aztec.VELOCITY);
            } else if (aztec.isHome()) {
                aztec.setVelocity(0);
            }
        }
    }

    private int getHeadingToCoordinates(Aztec aztec, double cx, double cy) {
        double dx = cx - aztec.getX();
        double dy = cy - aztec.getY();
        double alpha;
        if (dy == 0) {
            if (dx > 0) {
                alpha = Math.PI / 2;
            } else {
                alpha = 3 * Math.PI / 2;
            }
        } else {
            alpha = Math.atan(dx / dy);
            if (dy < 0) {
                alpha += Math.PI;
            }
        }

        return (int) (alpha / Math.PI / 2 * 360 + 720) % 360;
    }

    public void processAztecPlayerCollisions() {
        for (Player player : players.values()) {
            processCollision(player.getX(), player.getY(), player.getId());
        }
    }
}
