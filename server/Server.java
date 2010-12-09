
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

import java.util.HashMap;
import java.util.Iterator;

public class Server extends BrokerCallBack {

    Broker broker = Broker.getInstance();

    HashMap staticMap = new HashMap();

    //HashMap playersMap = new HashMap();
    //HashMap aztecsMap = new HashMap();

    HashMap<Integer, HashMap> db;

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

//    int numAztecs = 15;
//    {
//        for (int i = 1; i <= numAztecs; i++) {
//            aztecsMap.put("aztec" + numAztecs, -1 * numAztecs);
//        }
//    }

    public Server() {

        db = new HashMap<Integer, HashMap>();
    }

    public Message init(int playerID) {

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

        //aztects


        //number of players which are currently active
        msg.setInteger("numPlayers", numPlayers);



//
//
////todo ........ change, add aztecs
//        Iterator iterator = playersMap.keySet().iterator();
//
//        while (iterator.hasNext()) {
//            String key = iterator.next().toString();
//
//            if(key.charAt(0) == 'p')
//                msg.setInteger(key, (Integer)playersMap.get(key));
//            else
//                msg.setDouble(key, (Double)playersMap.get(key));
//        }
////todo....





        return msg;
    }

    public boolean collision() {

        return false;
    }


    public Message receive(Message request, Message response) {

        System.out.println("------------------------------------------");
        System.out.println("[Server] receive()");
        System.out.println("----> Number of fields = " + request.getFieldNumber());
        System.out.println("----> SessionID = "        + request.getSessionID());
        System.out.println("----> RequestID = "        + request.getRequestID());
        System.out.println("----> ObjectID = "         + request.getObjectID());
        System.out.println("----> Request msg = "      + request);
        System.out.println("------------------------------------------");

        if(request.getString("mid").equals("moveReq")) {

            System.out.println("[Server] Moving player " + request.getObjectID() +
                    " to h = " + request.getDouble("h"));

            if(!collision()) {

                //broadcast to all new position of the player
//                response.setInteger("plUpdate",123456);
//                response.setDouble("x", xPosP1);
//                response.setDouble("y", yPosP1);
//                //broker.sendBroadcast(response);
//                broker.send(response);

//                response.setBoolean("move", true);

                //testing init; init should be called from objectJoint later on
                //return init(5);


//                return response;

                return null;
            }
            else {
                System.out.println("[Server] Player with id " + request.getObjectID() + " CAN NOT longer move...");
                //todo notifyAll() about the change....NO move!!!
                return null;
            }
        }
        else
            return null;
    }

    public void objectJoined(int id) {

        HashMap players = new HashMap();
        System.out.println("\n\n[Server] objectJoined() ----> id = " + id);
        numPlayers++;

        //putting new player in a hash map, initializing its position
        if(!(db.containsKey(id))) {
            System.out.println("[Server] Player with id " + id + " is a new player, registering in DB ...");
            players.put("id", id);
            players.put("x", 0.1);
            players.put("y", 0.1);
            players.put("h", 0.1);
            db.put(id, players);
        }
        else {
            (db.get(id)).put("id", id);
            System.out.println("[Server] Player with id " + id + " is already in DB, retrieving info...");
            System.out.println("[Server] Last saved coordinates are: " + db.get(id));
        }

        testMessage(init(id));
        testMessage(playerJoined(id));
        testHashMap("");

        broker.send(init(id));   //send initialized world back to the client
//        //todo doesn't work yet wait for Peter --->
        broker.sendBroadcast(playerJoined(id));  //broadcast to everyone about new player
//        broker.send(playerJoined(id)); //for testing only (sendBroadcast() will be used instead...)


//-------------------------------------------------------------

//        System.out.println("[Server] objectJoined() ----> id = " + id);
//        numPlayers++;
//
//        //putting new player in a hash map if player is new
//        if(!(playersMap.containsValue(id))) {
//            System.out.println("[Server] Player with id " + id + " is a new player, registering in DB...");
//
//            playersMap.put("player" + numPlayers, id);
//            playersMap.put("xPlayer" + numPlayers, new Double(0.1));
//            playersMap.put("yPlayer" + numPlayers, new Double(0.1));
//            playersMap.put("hPlayer" + numPlayers, new Double(0.1));
//
//            testHashMap("aztec");
//        }
//        else {
//
//        }
//
////        if(!(playersMap.containsKey("xPlayer" + id))) {
////            playersMap.put("xPlayer" + id, new Double(0.1));
////            playersMap.put("yPlayer" + id, new Double(0.1));
////            playersMap.put("hPlayer" + id, new Double(0.1));
////        }
////
////        playersMap.put("player" + id, new Integer(id));
////        numPlayers ++;
//
//
//        broker.send(init(id));   //send initialized world back to the client
//        //todo doesn't work yet wait for Peter --->
//        //broker.sendBroadcast(playerJoined(id));  //broadcast to everyone about new player
//        broker.send(playerJoined(id)); //for testing only (sendBroadcast() will be used instead...)
    }

    public void objectLeft(int id) {

        System.out.println("\n\n[Server] objectLeft() ----> id = " + id);

        //Changing client's id to ZERO,
        //meaning client left but all the coordinates are still stored in HashMap
        (db.get(id)).put("id", 0);

        numPlayers--;

        testMessage(playerLeft(id));
        testHashMap("");

        broker.sendBroadcast(playerLeft(id));  //tell everyone that player left
        //broker.send(playerLeft(id));   //for testing only (sendBroadcast() will be used instead...)
//--------------------------------------------------------------------------

//        //todo doesn't work yet wait for Peter --->
            //broker.sendBroadcast(playerLeft(id));  //tell everyone that player left
//        broker.send(playerLeft(id));   //for testing only (sendBroadcast() will be used instead...)
//
//        //removing clients that left, note: their coordinates are still stored in the map
//        playersMap.remove("player" + id);
//        numPlayers--;
    }

    public Message playerJoined(int id) {

        Message msg = new Message(id);

        msg.setString("mid", "playerJoined");
        msg.setInteger("id", id);
        msg.setDouble("x", (Double) (db.get(id)).get("x"));
        msg.setDouble("y", (Double) (db.get(id)).get("y"));
        msg.setDouble("h", (Double) (db.get(id)).get("h"));

//        msg.setDouble("xPlayer" + id, (Double)(playersMap.get("xPlayer" + id)));
//        msg.setDouble("yPlayer" + id, (Double)(playersMap.get("yPlayer" + id)));
//        msg.setDouble("hPlayer" + id, (Double)(playersMap.get("hPlayer" + id)));

        return msg;
    }

    public Message playerLeft(int id) {

        Message msg = new Message(id);

        msg.setString("mid", "playerLeft");
        msg.setInteger("id", id);

        return msg;
    }

    public static void main(String args[]) {

        /* this needs to be done */
        Broker b = Broker.getInstance();
        b.registerCallBack(new Server());
        b.setAuthenticationData("AztecServer", "test");
        b.init();
/*
        Server server = new Server();
        server.objectJoined(10);

        server.db.get(10).put("x", 777.7);
        server.db.get(10).put("y", 888.8);
        //server.db.get(10).put("h", 999.9);

        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectJoined(11);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectJoined(12);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectLeft(10);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectLeft(11);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectJoined(13);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectLeft(13);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectJoined(11);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectJoined(14);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectJoined(13);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectJoined(10);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
        server.objectLeft(14);
        System.out.println("TOTAL # OF PLAYERS LOGGED IN-------> [" + server.numPlayers + "]");
*/
    }

    // DEBUG: FOR TESTING PURPOSES ONLY
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
//        else if (str == "players") {
//            System.out.println("--- Players Hash Map ---");
//            Iterator iterator = playersMap.keySet().iterator();
//
//            while (iterator.hasNext()) {
//                String key = iterator.next().toString();
//                String value = playersMap.get(key).toString();
//
//                System.out.println(key + "\t\t" + value);
//            }
//        }
//        else if (str == "aztecs") {
//            System.out.println("--- Aztecs Hash Map ---");
//            Iterator iterator = aztecsMap.keySet().iterator();
//
//            while (iterator.hasNext()) {
//                String key = iterator.next().toString();
//                String value = aztecsMap.get(key).toString();
//
//                System.out.println(key + "\t\t" + value);
//            }
//        }

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

        System.out.println("------ MESSAGE RECEIVED ------");
        System.out.println("Number of fields = " + msg.getFieldNumber());
        System.out.println(msg);
        System.out.println("------ END OF MESSAGE  ------");

    }
}
