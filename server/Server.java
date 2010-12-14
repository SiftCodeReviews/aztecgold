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
    private static final double CHASE_DISTANCE = 8;

    public static Server getInstance() {
        return instance;
    }

    Broker broker = Broker.getInstance();

    List<AztecObject> staticObjects = new ArrayList<AztecObject>();
    List<Aztec> aztecs = new ArrayList<Aztec>();
    Map<Integer, Player> players = new HashMap<Integer, Player>();

    int numPlayers = 0;
    int numTrees = 5;
    int numHuts = 5;
    int numAztecs = 4;

    int numCoins = 10;

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
        staticObjects.add(new Tree(3, 5, -5));
        staticObjects.add(new Tree(4, 8, -8));
        staticObjects.add(new Tree(5, 15, -15));

        staticObjects.add(new Hut(1, -40, -40));
        staticObjects.add(new Hut(2, -15, -15));
        staticObjects.add(new Hut(3, -30, -30));
        staticObjects.add(new Hut(4, 0, -35));
        staticObjects.add(new Hut(5, 0, -20));

        staticObjects.add(new Fort(11, 11));
        staticObjects.add(new Chest(-6, 3));

        staticObjects.add(new Coin(-101, 1, 10, 20));
        staticObjects.add(new Coin(-102, 2, 10, 25));
        staticObjects.add(new Coin(-103, 3, 10, 30));
        staticObjects.add(new Coin(-104, 4, 10, 35));
        staticObjects.add(new Coin(-105, 5, 10, 40));
        staticObjects.add(new Coin(-106, 6, 20, 20));
        staticObjects.add(new Coin(-107, 7, 20, 25));
        staticObjects.add(new Coin(-108, 8, 20, 30));
        staticObjects.add(new Coin(-109, 9, 20, 35));
        staticObjects.add(new Coin(-110, 10, 20, 40));
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
                System.out.println("[Server] Collision with " + staticObject.getType() + " detected");
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
                    double randX = Math.random() * 40.0;
                    double randY = Math.random() * 40.0;
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
                player.move(2, 2);
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
            player = new Player(id, 2, 2);
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
