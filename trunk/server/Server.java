
/**
 * Created by IntelliJ IDEA.
 * User: IGOR
 * Date: Nov 20, 2010
 * Time: 4:18:34 PM
 * To change this template use File | Settings | File Templates.
 */

import broker.Broker;
import broker.object.*;
import broker.service.com.protocol.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Server extends BrokerCallBack {

    Broker broker = Broker.getInstance();
    HashMap aztecsMap = new HashMap();
    HashMap coinsMap = new HashMap();
    HashMap staticMap = new HashMap();
    HashMap<Integer, HashMap> db;
    Collection<HashMap> collection;

    int numPlayers = 0;

    //static objects' positions (never changes)
    double xFort = 11.0;
    double yFort = 11.0;
    double xChest = 10.0;
    double yChest = 10.0;

    int numTrees = 5;
    {
        staticMap.put("xTree1", new Double(1.0));
        staticMap.put("yTree1", new Double(-1.0));
        staticMap.put("xTree2", new Double(2.0));
        staticMap.put("yTree2", new Double(-2.0));
        staticMap.put("xTree3", new Double(3.0));
        staticMap.put("yTree3", new Double(-3.0));
        staticMap.put("xTree4", new Double(4.0));
        staticMap.put("yTree4", new Double(-4.0));
        staticMap.put("xTree5", new Double(5.0));
        staticMap.put("yTree5", new Double(-5.0));
    }

    int numHuts = 3;
    {
        staticMap.put("xHut1", new Double(11.0));
        staticMap.put("yHut1", new Double(-11.0));
        staticMap.put("xHut2", new Double(12.0));
        staticMap.put("yHut2", new Double(-12.0));
        staticMap.put("xHut3", new Double(13.0));
        staticMap.put("yHut3", new Double(-13.0));
    }

    int numAztecs = 4;
    {
        aztecsMap.put("aztec1", new Integer(-1));
        aztecsMap.put("xAztec1", new Double(1.1));
        aztecsMap.put("yAztec1", new Double(1.1));
        aztecsMap.put("hAztec1", new Integer(180));

        aztecsMap.put("aztec2", new Integer(-2));
        aztecsMap.put("xAztec2", new Double(4.4));
        aztecsMap.put("yAztec2", new Double(4.4));
        aztecsMap.put("hAztec2", new Integer(90));

        aztecsMap.put("aztec3", new Integer(-3));
        aztecsMap.put("xAztec3", new Double(5.5));
        aztecsMap.put("yAztec3", new Double(5.5));
        aztecsMap.put("hAztec3", new Integer(45));

        aztecsMap.put("aztec4", new Integer(-4));
        aztecsMap.put("xAztec4", new Double(9.9));
        aztecsMap.put("yAztec4", new Double(9.9));
        aztecsMap.put("hAztec4", new Integer(45));

    }

    int numCoins = 5;
    {
        coinsMap.put("coin1", new Integer(-101));
        coinsMap.put("xCoin1", new Double(10.1));
        coinsMap.put("yCoin1", new Double(20.2));

        coinsMap.put("coin2", new Integer(-102));
        coinsMap.put("xCoin2", new Double(15.1));
        coinsMap.put("yCoin2", new Double(25.2));

        coinsMap.put("coin3", new Integer(-103));
        coinsMap.put("xCoin3", new Double(19.1));
        coinsMap.put("yCoin3", new Double(29.2));

        coinsMap.put("coin4", new Integer(-104));
        coinsMap.put("xCoin4", new Double(17.1));
        coinsMap.put("yCoin4", new Double(27.2));

        coinsMap.put("coin5", new Integer(-105));
        coinsMap.put("xCoin5", new Double(13.1));
        coinsMap.put("yCoin5", new Double(23.2));
    }

    public Server() {

        db = new HashMap<Integer, HashMap>();
    }

    public synchronized Message init(int playerID) {

        Message msg = new Message(playerID);

        //init
        msg.setString("mid", "init");

        //trees
        msg.setInteger("numTrees", numTrees);
        for (int i = 1; i <= numTrees; i++) {
            msg.setDouble("xTree" + i, (Double)(staticMap.get("xTree" + i)));
            msg.setDouble("yTree" + i, (Double)(staticMap.get("yTree" + i)));
        }

        //huts
        msg.setInteger("numHuts", numHuts);
        for (int i = 1; i <= numHuts; i++) {
            msg.setDouble("xHut" + i, (Double)(staticMap.get("xHut" + i)));
            msg.setDouble("yHut" + i, (Double)(staticMap.get("yHut" + i)));
        }

        //chest and fort
        msg.setDouble("xFort", xFort);
        msg.setDouble("yFort", yFort);
        msg.setDouble("xChest", xChest);
        msg.setDouble("yChest", yChest);

        //number of players which are currently active
        int player = numPlayers;
        msg.setInteger("numPlayers", numPlayers);

        collection = db.values();

        for (Iterator<HashMap> hmIterator = collection.iterator(); hmIterator.hasNext();) {
            HashMap dbValuesMap = hmIterator.next();
            if(!(dbValuesMap.get("id").equals(0))) {
                msg.setInteger("player" + player, (Integer)(dbValuesMap.get("id")));
                msg.setDouble("xPlayer" + player, (Double)(dbValuesMap.get("x")));
                msg.setDouble("yPlayer" + player, (Double)(dbValuesMap.get("y")));
                msg.setInteger("hPlayer" + player, (Integer)(dbValuesMap.get("h")));
                player--;
            }
        }

        //aztects
        msg.setInteger("numAztecs", numAztecs);
        for (int i = 1; i <= numAztecs; i++) {
            msg.setInteger("aztec" + i, i*-1);
            msg.setDouble("xAztec" + i, (Double)(aztecsMap.get("xAztec" + i)));
            msg.setDouble("yAztec" + i, (Double)(aztecsMap.get("yAztec" + i)));
            msg.setInteger("hAztec" + i, (Integer)(aztecsMap.get("hAztec" + i)));
        }

        //coins
        msg.setInteger("numCoins", numCoins);
        for (int i = 1; i <= numCoins; i++) {
            msg.setInteger("coin" + i, -100+i*-1);
            msg.setDouble("xCoin" + i, (Double)(coinsMap.get("xCoin" + i)));
            msg.setDouble("yCoin" + i, (Double)(coinsMap.get("yCoin" + i)));
        }

        return msg;
    }

    public synchronized boolean collision(int id, int heading) {

        boolean toReturn = false;
        db.get(id).put("h", heading);             //putting new h in db
        double x = (Double) db.get(id).get("x");  //getting player's x and y coordinates
        double y = (Double) db.get(id).get("y");
        int h = (Integer) db.get(id).get("h");

        switch (h) {
            case 0:
                y++;
                //todo check for collision
                //if(collision == true)
                // then set flag toReturn to true
                //else
                //put new coordinates to the DB
                db.get(id).put("y", y);
                System.out.println("y = " + y);
                break;
            case 45:
                y++;
                x++;
                db.get(id).put("x", x);
                db.get(id).put("y", y);
                System.out.println("x = " + x);
                System.out.println("y = " + y);
                break;
            case 90:
                x++;
                db.get(id).put("x", x);
                System.out.println("x = " + x);
                break;
            case 135:
                x++;
                y--;
                db.get(id).put("x", x);
                db.get(id).put("y", y);
                System.out.println("x = " + x);
                System.out.println("y = " + y);
                break;
            case 180:
                y--;
                db.get(id).put("y", y);
                System.out.println("y = " + y);
                break;
            case 225:
                x--;
                y--;
                db.get(id).put("x", x);
                db.get(id).put("y", y);
                System.out.println("x = " + x);
                System.out.println("y = " + y);
                break;
            case 270:
                x--;
                db.get(id).put("x", x);
                System.out.println("x = " + x);
                break;
            case 315:
                x--;
                y++;
                db.get(id).put("x", x);
                db.get(id).put("y", y);
                System.out.println("x = " + x);
                System.out.println("y = " + y);
                break;
            case 360:
                y++;
                db.get(id).put("y", y);
                System.out.println("y = " + y);
                break;
            default:
                System.out.println("[Server] SWITCH (heading): SOMETHING WENT TOTALLY WRONG!!! :(");
        }
        return toReturn;
    }


    public Message receive(Message request, Message response) {

        try {
            System.out.println("[Server] Request msg. with [" + request.getFieldNumber() +
                    "] fields has been received... \n" + request);

            //if player wants to move
            if(request.getString("mid").equals("moveReq")) {

                int id = request.getObjectID();
                int heading = request.getInteger("h");


                System.out.println("[Server] Moving player with id " + id + ", and h = " + heading);

                if(!collision(id, heading)) {

                    testMessage(move(id));  //todo delete debug
                    broker.sendBroadcast(move(id));


                }
                //todo when there is a collision with the coin or chest, send playerStatus() back to that client
                //playerStatus(request.getObjectID());

            }

        } catch (RuntimeException e) {
            System.out.println("[Server] Exception in receive(): " + e);
        }
        return null;
    }

    public synchronized Message move(int objID) {

        Message msg = new Message(objID);

        msg.setString("mid", "move");
        msg.setInteger("id", objID);
        msg.setDouble("x", (Double) db.get(objID).get("x"));
        msg.setDouble("y", (Double) db.get(objID).get("y"));
        msg.setInteger("h", (Integer) db.get(objID).get("h"));

        return msg;
    }

    public void objectJoined(int id) {

        HashMap players = new HashMap();
        System.out.println("[Server] objectJoined() ----> id = " + id);
        numPlayers++;

        //putting new player in a hash map, initializing its position
        if(!(db.containsKey(id))) {
            System.out.println("[Server] Player with id " + id + " is a new player, registering in DB ...");
            players.put("id", id);
            players.put("x", 0.1);
            players.put("y", 0.1);
            players.put("h", 0);
            players.put("score", 0);
            players.put("coins", 0);
            db.put(id, players);
        }
        else {
            (db.get(id)).put("id", id);
            System.out.println("[Server] Player with id " + id + " is already in DB, retrieving info...");
            System.out.println("[Server] Last saved coordinates are: " + db.get(id));
        }

        testMessage(init(id));          //todo delete debug
        testMessage(playerJoined(id));  //todo delete debug
        testHashMap("");                //todo delete debug
        testMessage(playerStatus(id));  //todo delete debug

        broker.send(init(id));   //send initialized world back to the client
        broker.send(playerStatus(id));   //send the last saved score and coins collected
        broker.sendBroadcast(playerJoined(id));  //broadcast to everyone about new player
    }

    public void objectLeft(int id) {

        System.out.println("[Server] objectLeft() ----> id = " + id);

        //Changing client's id to 0 when client disconnects. note: all [x,y,h] coordinates are still stored in the Map
        (db.get(id)).put("id", 0);

        numPlayers--;

        testMessage(playerLeft(id)); //todo delete debug
        testHashMap("");             //todo delete debug

        broker.sendBroadcast(playerLeft(id));  //tell everyone that player left
    }

    public synchronized Message playerJoined(int id) {

        Message msg = new Message(id);

        msg.setString("mid", "playerJoined");
        msg.setInteger("id", id);
        msg.setDouble("x", (Double) (db.get(id)).get("x"));
        msg.setDouble("y", (Double) (db.get(id)).get("y"));
        msg.setInteger("h", (Integer) (db.get(id)).get("h"));

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
        msg.setInteger("score", (Integer)(db.get(id)).get("score"));
        msg.setInteger("coins", (Integer)(db.get(id)).get("coins"));

        return msg;

    }

    public static void main(String args[]) {

        /* this needs to be done */
        Broker b = Broker.getInstance();
        b.registerCallBack(new Server());
        b.setAuthenticationData("AztecServer", "test");
        b.init();

        //todo delete all debug statements below
/*
        Server server = new Server();
        System.out.println("------------------------------------------");
        server.objectJoined(10);
        System.out.println("------------------------------------------");
        server.objectJoined(11);
        System.out.println("------------------------------------------");
        server.objectJoined(12);
        System.out.println("------------------------------------------");
        server.objectLeft(10);
        System.out.println("------------------------------------------");
        server.objectLeft(11);
        System.out.println("------------------------------------------");
        server.objectJoined(13);
        System.out.println("------------------------------------------");

        server.db.get(12).put("score", 12345);
        server.db.get(12).put("coins", 987);

        server.objectLeft(13);
        System.out.println("------------------------------------------");
        server.objectJoined(11);
        System.out.println("------------------------------------------");
        server.objectJoined(14);
        System.out.println("------------------------------------------");
        server.objectJoined(10);
        System.out.println("------------------------------------------");

        server.db.get(14).put("x", -1111.1111);
        server.db.get(14).put("y", -2222.2222);
        server.db.get(14).put("h", 1000);

        server.objectLeft(14);
        System.out.println("------------------------------------------");
        server.objectJoined(13);
        System.out.println("------------------------------------------");
        server.objectJoined(14);
        System.out.println("------------------------------------------");

        server.testMoveRegMsgFromClient(10, 0);
        server.testMoveRegMsgFromClient(11, 45);
        server.testMoveRegMsgFromClient(12, 90);
        server.testMoveRegMsgFromClient(13, 135);

        server.testMoveRegMsgFromClient(14, 180);
        server.testMoveRegMsgFromClient(10, 225);
        server.testMoveRegMsgFromClient(11, 270);
        server.testMoveRegMsgFromClient(12, 315);

        server.testMoveRegMsgFromClient(13, 360);


        server.objectLeft(10);
        server.objectLeft(11);
        server.objectLeft(12);
        server.objectLeft(13);
        server.objectLeft(14);

        server.objectJoined(14);
        server.objectJoined(13);
        server.objectJoined(12);
        server.objectJoined(11);
        server.objectJoined(10);
*/

    }

    //todo delete debug
    // DEBUG: FOR TESTING PURPOSES ONLY 
    public Message testMoveRegMsgFromClient(int id, int h) {

        Message m = new Message(id);
        m.setString("mid", "moveReq");
        m.setInteger("h", h);
        receive(m, null);
        return m;
    }

    public void testHashMap(String str) {

        if (str == "static") {
            System.out.println("--- Static Objects Hash Map ---");
            Iterator iterator = staticMap.keySet().iterator();

            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = staticMap.get(key).toString();

                System.out.println(key + "\t\t" + value);
            }
        }
        else if (str == "aztecs") {
            System.out.println("--- Aztecs Hash Map ---");
            Iterator iterator = aztecsMap.keySet().iterator();

            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = aztecsMap.get(key).toString();

                System.out.println(key + "\t\t" + value);
            }
        }
        else {
            System.out.println("--- DB Hash Map ---");
            Iterator iterator = db.keySet().iterator();

            while (iterator.hasNext()) {
                int key = (Integer) iterator.next();
                String value = db.get(key).toString();

                System.out.println(key + "\t\t" + value);
            }
        }
    }

    public void testMessage(Message msg) {
        System.out.println("[Server] Response msg. (" + msg.getString("mid") + ") with ["
                + msg.getFieldNumber() + "] fields has been send to client... ");
        if(msg.getString("mid").equals("move"))
            System.out.println(msg);
    }
}
