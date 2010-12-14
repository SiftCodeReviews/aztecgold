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

public class Server extends BrokerCallBack implements Runnable {

    Broker broker = Broker.getInstance();
//    HashMap<Integer, HashMap> db;

    List<AztecObject> staticObjects = new ArrayList<AztecObject>();
    List<Aztec> aztecs = new ArrayList<Aztec>();
    Map<Integer, Player> players = new HashMap<Integer, Player>();

    int numPlayers = 0;
    int numTrees = 5;
    int numHuts = 5;
    int numAztecs = 4;

    int numCoins = 10;

    public Server() {

//        db = new HashMap<Integer, HashMap>();

        initializeStaticObjects();
        initializeAztecs();
    }

    private void initializeAztecs() {
        aztecs.add(new Aztec(1, -10, 10, 180));
        aztecs.add(new Aztec(2, -15, 15, 90));
        aztecs.add(new Aztec(3, -20, 20, 45));
        aztecs.add(new Aztec(4, -30, 30, 45));
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

    public synchronized Message init(int playerID) {

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
//        int player = numPlayers;
        msg.setInteger("numPlayers", numPlayers);

        int counter = 1;
        for (Player p : players.values()) {
            if (p.isOnline()) {
                p.appendToMessage(msg, counter);
                counter++;
            }
        }
//
//        collection = db.values();
//        for (Iterator<HashMap> hmIterator = collection.iterator(); hmIterator.hasNext();) {
//            HashMap dbValuesMap = hmIterator.next();
//            if(!(dbValuesMap.get("id").equals(0))) {
//                msg.setInteger("player" + player, (Integer)(dbValuesMap.get("id")));
//                msg.setDouble("xPlayer" + player, (Double)(dbValuesMap.get("x")));
//                msg.setDouble("yPlayer" + player, (Double)(dbValuesMap.get("y")));
//                msg.setInteger("hPlayer" + player, (Integer)(dbValuesMap.get("h")));
//                player--;
//            }
//        }

        //aztecs
        msg.setInteger("numAztecs", numAztecs);

        for (Aztec aztec : aztecs) {
            aztec.appendToMessage(msg);
        }

        return msg;
    }

    public synchronized boolean processCollision(double x, double y, int id) {

        double X, Y, randX, randY;
        Rectangle playerRectangle = new Rectangle((int) x - 1, (int) y - 1, 2, 2);

        for (AztecObject staticObject : staticObjects) {
            if (playerRectangle.intersects(staticObject.getRectangle())) {
                System.out.println("[Server] Collision with " + staticObject.getType() + " detected");
                String type = staticObject.getType();
                if ("Tree".equals(type) || "Hut".equals(type) || "Fort".equals(type)) {
                    return true;
                } else if ("Chest".equals(type)) {
                    System.out.println("CHEST!!!!!!!");
                    /*
                      int score = (Integer) db.get(id).get("score");
            int coins = (Integer) db.get(id).get("coins");
            db.get(id).put("score", score + coins);
            db.get(id).put("coins", 0);

            testMessage(playerStatus(id));  //todo delete debug

            broker.send(playerStatus(id));
                     */
                    return true;
                } else if ("Coin".equals(type)) {
                    /*
                int coins = (Integer) db.get(id).get("coins");   //retrieving last known number of coins
                int coinID = (Integer) coinsMap.get("coin" + i); //getting an ID of the coin that has been collected
                db.get(id).put("coins", ++coins);                //putting new number of coins in DB
                broker.send(playerStatus(id));                   //sending playerStatus()

                //randomizing coin's new position [0.0 - 1000.0]
                randX =  Math.random() * 1000.0;
                randY =  Math.random() * 1000.0;

                coinsMap.put("xCoin" + i, randX);
                coinsMap.put("yCoin" + i, randY);

                testMessage(playerStatus(id));         //todo delete debug
                testMessage(moveCoin(id, coinID, i));  //todo delete debug
//                testHashMap("coins");                  //todo delete debug

                broker.sendBroadcast(moveCoin(id, coinID, i));   //moving coin to a new position and broadcasting...
                System.out.println("[Server] Collision with the COIN detected!!!");

                     */
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
                System.out.println("OUCH!!!!!!!!!!!!!!!!!!!!!!!!!1");
                /*
                   db.get(id).put("coins", 0);      //resetting number of coins and coordinates for the player
                db.get(id).put("x", 0.0);
                db.get(id).put("y", 0.0);
                db.get(id).put("h", 0);

                testMessage(playerStatus(id));         //todo delete debug
                testHashMap("");                       //todo delete debug

                broker.send(playerStatus(id));
                System.out.println("[Server] Collision with the AZTEC detected!!!");
                return true;
                 */
                return true;
            }
        }

        return false;
    }

    public synchronized boolean detectCollision(int id, int heading) {
        heading = (heading + 360) % 360;

        boolean flag = false;
        Player player = players.get(id);
        player.setHeading(heading);
//
//        db.get(id).put("h", heading);             //putting new h in DB
//        double x = (Double) db.get(id).get("x");  //getting player's x and y coordinates
//        double y = (Double) db.get(id).get("y");
//        int h = (Integer) db.get(id).get("h");

        double x = player.getX();
        double y = player.getY();

        switch (heading) {
            case 0:
                y += Player.VELOCITY;
                if (processCollision(x, y, id))
                    flag = true;
                else
                    player.move(x, y);
                break;
            case 45:
                y += Player.VELOCITY;
                x += Player.VELOCITY;

                if (processCollision(x, y, id))
                    flag = true;
                else {
                    player.move(x, y);
                }
                break;
            case 90:
                x += Player.VELOCITY;

                if (processCollision(x, y, id))
                    flag = true;
                else
                    player.move(x, y);
                break;
            case 135:
                x += Player.VELOCITY;

                y -= Player.VELOCITY;
                if (processCollision(x, y, id))
                    flag = true;
                else {
                    player.move(x, y);
                }
                break;
            case 180:
                y -= Player.VELOCITY;
                if (processCollision(x, y, id))
                    flag = true;
                else
                    player.move(x, y);
                break;
            case 225:
                x -= Player.VELOCITY;

                y -= Player.VELOCITY;
                if (processCollision(x, y, id))
                    flag = true;
                else {
                    player.move(x, y);
                }
                break;
            case 270:
                x -= Player.VELOCITY;

                if (processCollision(x, y, id))
                    flag = true;
                else
                    player.move(x, y);
                break;
            case 315:
                x -= Player.VELOCITY;

                y += Player.VELOCITY;
                if (processCollision(x, y, id))
                    flag = true;
                else {
                    player.move(x, y);
                }
                break;
            default:
                System.out.println("[Server] SWITCH (heading): SOMETHING WENT TOTALLY WRONG!!! :(");
        }
        return flag;
    }

    public void run() {

    }

    public Message receive(Message request, Message response) {

        try {
            System.out.println("[Server] Request msg. with [" + request.getFieldNumber() +
                    "] fields has been received... \n" + request);

            //if player wants to movePlayer
            if (request.getString("mid").equals("moveReq")) {

                int id = request.getObjectID();
                int heading = request.getInteger("h");

                System.out.println("[Server] Moving player with id " + id + ", and h = " + heading);

                if (!detectCollision(id, heading)) {  //if there is NO collision detected

                    //testMessage(movePlayer(id));    //todo delete debug

                    broker.sendBroadcast(movePlayer(id));
                } else {   //collision...
                    System.out.println("\n\n\n\n\t\t\t...COLLISION...\n");              //todo delete debug
                    System.out.println("[Server] Player " + id + " collided with an object...");
                    System.out.println("\n\n\n\n\n\n\n\n\n");                           //todo delete debug
                }

//-----------------------------------------------------------
                //if player within the range of the aztec, aztec starts moving towards the player
                int objID = isAztecActivated(id);

                if (objID != 0) {
                    System.out.println("[Server] Aztec" + objID + " is moving towards the player " + id);

                    for (int i = 1; i < 30; i++) {
                        broker.sendBroadcast(moveAztec(id, objID));
                        Thread.sleep(3000);
                    }

                    testMessage(moveAztec(id, objID));    //todo delete debug
                }

//-----------------------------------------------------------

            }


        } catch (RuntimeException e) {
            System.out.println("*\n*\n*\n*\n*\n*\n*\n*\n*\n*\n");
            e.printStackTrace();
            System.out.println("*\n*\n*\n*\n*\n*\n*\n*\n*\n*\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized int isAztecActivated(int id) {

//        double X, Y, pX, pY;
//        Rectangle aztecRectangle, playerRectangle;
//
//        //checking if player is in the right range from the aztec
//        for (int objID = 1; objID <= numAztecs; objID++) {
//
//            X = (Double)(aztecsMap.get("xAztec" + objID));    //positions of every Aztec
//            Y = (Double)(aztecsMap.get("yAztec" + objID));
//
//            aztecRectangle = new Rectangle((int)X-5, (int)Y-5, 10, 10);
//
//            pX = (Double) db.get(id).get("x");
//            pY = (Double) db.get(id).get("y");
//
//            playerRectangle = new Rectangle((int)pX-1, (int)pY-1, 2, 2);
//
//            if(aztecRectangle.intersects(playerRectangle)) {
//                return objID;
//            }
//        }
        return 0;
    }

    public synchronized Message movePlayer(int id) {

        Message msg = new Message(id);
        msg.setString("mid", "move");
        Player player = players.get(id);
        player.appendToStatusMessage(msg);

        return msg;
    }

//    public synchronized Message moveCoin(int id, int objID, int localID) {
//
//        Message msg = new Message(id);
//        msg.setString("mid", "move");
//        msg.setInteger("id", objID);
//        msg.setDouble("x", (Double) coinsMap.get("xCoin" + localID));
//        msg.setDouble("y", (Double) coinsMap.get("yCoin" + localID));
//        return msg;
//    }

    public synchronized Message moveAztec(int id, int objID) {

        int ID = objID * -1;

        Message msg = new Message(id);

        msg.setString("mid", "move");
        msg.setInteger("id", ID);

//        double x = (Double) aztecsMap.get("xAztec" + objID);
//        double y = (Double) aztecsMap.get("yAztec" + objID);
//        //int h = (Integer) aztecsMap.get("hAztec" + localID);
//
////        x++;
////        y++;
//        //  h+=45;
//
//        aztecsMap.put("xAztec" + objID, ++x);
//        aztecsMap.put("yAztec" + objID, ++y);
//        //aztecsMap.put("hAztec" + localID, h);
//
//        msg.setDouble("x", (Double) aztecsMap.get("xAztec" + objID));
//        msg.setDouble("y", (Double) aztecsMap.get("yAztec" + objID));
//        msg.setInteger("h", (Integer) aztecsMap.get("hAztec" + objID));

        return msg;
    }

    public synchronized void objectJoined(int id) {

//        HashMap players = new HashMap();
        System.out.println("[Server] objectJoined() ----> id = " + id);
        numPlayers++;

        Player player = players.get(id);
        if (player == null) {
            player = new Player(id, 2, 2);
            players.put(id, player);
        } else {
            player.setOnline(true);
        }

        //putting new player in a hash map, initializing its position
//        if(!(db.containsKey(id))) {
//            System.out.println("[Server] Player with id " + id + " is a new player, registering in DB ...");
//            players.put("id", id);
//            players.put("x", 2.0);
//            players.put("y", 2.0);
//            players.put("h", 0);
//            players.put("score", 0);
//            players.put("coins", 0);
//            db.put(id, players);
//        }
//        else {
//            (db.get(id)).put("id", id);
//            System.out.println("[Server] Player with id " + id + " is already in DB, retrieving info...");
//            System.out.println("[Server] Last saved coordinates are: " + db.get(id));
//        }

//        testMessage(init(id));          //todo delete debug
//        testMessage(playerJoined(id));  //todo delete debug
//        testMessage(playerStatus(id));  //todo delete debug

        broker.send(init(id));   //send initialized world back to the client
        broker.send(playerStatus(id));   //send the last saved score and coins collected
        broker.sendBroadcast(playerJoined(id));  //broadcast to everyone about new player
    }

    public void objectLeft(int id) {

        System.out.println("[Server] objectLeft() ----> id = " + id);
        players.get(id).setOnline(false);

        //Changing client's id to 0 when client disconnects. note: all [x,y,h] coordinates are still stored in the Map
//        (db.get(id)).put("id", 0);
        numPlayers--;

//        testMessage(playerLeft(id)); //todo delete debug

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

        Message msg = new Message(id);
        msg.setString("mid", "playerStatus");
        Player player = players.get(id);

        msg.setInteger("score", player.getScore());
        msg.setInteger("coins", player.getCoins());
        return msg;
    }

    public static void main(String args[]) {

        /* this needs to be done */
        Broker b = Broker.getInstance();
        b.registerCallBack(new Server());
        b.setAuthenticationData("AztecServer", "test");
        b.init();
    }

    public void testMessage(Message msg) {
        System.out.println("[Server -DEBUG] Response msg. (" + msg.getString("mid") + ") with ["
                + msg.getFieldNumber() + "] fields has been send to client... ");
//        if(msg.getString("mid").equals("move") || msg.getString("mid").equals("playerStatus"))
//            System.out.println(msg);
    }
}
