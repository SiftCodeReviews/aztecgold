
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
import broker.service.index.DistObject;
import broker.service.index.IndexService;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;

public class Server extends BrokerCallBack {

    Broker broker = Broker.getInstance();
    HashMap staticMap = new HashMap();
    HashMap playersMap = new HashMap();

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

    int numPlayers = 0;

    public Message init(int playerID) {

        //todo later needs to be --> Message msg = new Message(playerID);
        Message msg = new Message(0xFF00FF00,0x11EE11EE,0x195CC);

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

        //number of players which are currently logged in
        msg.setInteger("numPlayers", numPlayers);

        Iterator iterator = playersMap.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next().toString();

            if(key.charAt(0) == 'p')
                msg.setInteger(key, (Integer)playersMap.get(key));
            else
                msg.setDouble(key, (Double)playersMap.get(key));
        }
        return msg;
    }

    public boolean collision() {

        return false;
    }

    public Message receive(Message request, Message response) {

        System.out.println("------------------------------------------");
        System.out.println("Number of fields = " + request.getFieldNumber());
        System.out.println("SessionID = "        + request.getSessionID());
        System.out.println("RequestID = "        + request.getRequestID());
        System.out.println("ObjectID = "         + request.getObjectID());
        System.out.println("Request = "          + request);
        System.out.println("------------------------------------------");

        if(request.getString("mid").equals("moveReq")) {

            System.out.println("Preparing to move the player...");

//##########################TESTING############################################

//testing..........................static hash map
//            Iterator iterator = staticMap.keySet().iterator();
//
//            while (iterator.hasNext()) {
//                String key = iterator.next().toString();
//                String value = staticMap.get(key).toString();
//
//                System.out.println(key + "\t\t" + value);
//            }
//testing..........................static hash map
            objectJoined(10);

            playersMap.put("xPlayer10", new Double(2222.22222));
            playersMap.put("yPlayer10", new Double(2222.22222));
            playersMap.put("hPlayer10", new Double(2222.22222));

            objectJoined(44);
            objectLeft(10);
            objectJoined(55);
            objectJoined(66);
            objectJoined(77);

//#############################END OF TESTING#########################################

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
                System.out.println("Player CANNOT move, COLLISION!!!");
                //todo notifyAll() about the change....NO move!!!
                return null;
            }
        }
        else
            return null;
    }

    public void objectLeft(int id) {

        //removing clients that left, note: their coordinates are still stored in the map
        playersMap.remove("player" + id);
        numPlayers--;
    }

    public void objectJoined(int id) {

        //putting new player in a hash map if player doesn't exist
        if(!(playersMap.containsKey("xPlayer" + id))) {
            playersMap.put("xPlayer" + id, 0.1);
            playersMap.put("yPlayer" + id, 0.1);
            playersMap.put("hPlayer" + id, 0.1);
        }

        playersMap.put("player" + id, new Integer(id));
        numPlayers ++;
        broker.send(init(id));   //send initialized world back to the client

        //broker.sendBroadcast(playerJoint(id));  //tell everyone about new player
        //broker.send(playerJoined(id));   //for testing only (won't be used)
    }

    public Message playerJoined(int id) {

        //should be in the form (new Message(ID))
        Message msg = new Message(0xFF00FF00,0x11EE11EE,0x195CC);

        msg.setString("mid", "playerJoined");
        msg.setDouble("xPlayer" + id, (Double)(playersMap.get("xPlayer" + id)));
        msg.setDouble("yPlayer" + id, (Double)(playersMap.get("yPlayer" + id)));
        msg.setDouble("hPlayer" + id, (Double)(playersMap.get("hPlayer" + id)));
        msg.setInteger("player" + id, (Integer)(playersMap.get("player" + id)));

        return msg;
    }

    public static void start(Server server) {

        /* this needs to be done */
        Broker b = Broker.getInstance();
        b.registerCallBack(server);
        b.init();

        /* demo for sending a message back to client */
        Message m = new Message(0xFF00FF00,0x11EE11EE,0x195CC);
        m.setString("start", "communication");
        b.send(m);
    }

    //main function for testing purposes
    public static void main(String args[]) {

        /* hard coded binding of the client, this will be done later automatically */
        IndexService s = IndexService.getInstance();
        try {
            s.registerDistObject( new DistObject(0xFF00FF00, 103884, new InetSocketAddress(InetAddress.getByName((args.length>0)?args[0]:"127.0.0.1"), 2040)) );
        } catch(Exception e) {
            e.printStackTrace();
        }

        start(new Server());
    }
}

