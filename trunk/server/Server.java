
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

public class Server extends BrokerCallBack {

    Broker broker = Broker.getInstance();
    HashMap hm = new HashMap();

    //static objects' positions (never changes)
    double xFort = 11.0;
    double yFort = 11.0;
    double xChest = 10.0;

    double yChest = 10.0;
    int numTrees = 5;
    {
        hm.put("xTree1", 1.0);
        hm.put("yTree1", -1.0);
        hm.put("xTree2", 2.0);
        hm.put("yTree2", -2.0);
        hm.put("xTree3", 3.0);
        hm.put("yTree3", -3.0);
        hm.put("xTree4", 4.0);
        hm.put("yTree4", -4.0);
        hm.put("xTree5", 5.0);
        hm.put("yTree5", -5.0);
    }

    int numHuts = 3;
    {
        hm.put("xHut1", 11.0);
        hm.put("yHut1", -11.0);
        hm.put("xHut2", 22.0);
        hm.put("yHut2", -22.0);
        hm.put("xHut3", 33.0);
        hm.put("yHut3", -33.0);
    }

    int numPlayers = 0;

    public Message init(int playerID) {

        //todo later needs to be --> Message msg = new Message(playerID);
        Message msg = new Message(0xFF00FF00,0x11EE11EE,0x195CC);

        //trees
        msg.setInteger("numTrees", numTrees);

        for (int i = 1; i <= numTrees; i++) {

            msg.setDouble("xTree" + i, (Double)(hm.get("xTree" + i)));
            msg.setDouble("yTree" + i, (Double)(hm.get("yTree" + i)));
        }

        //huts
        msg.setInteger("numHuts", numHuts);

        for (int i = 1; i <= numHuts; i++) {

            msg.setDouble("xHut" + i, (Double)(hm.get("xHut" + i)));
            msg.setDouble("yHut" + i, (Double)(hm.get("yHut" + i)));
        }

        //chest and fort
        msg.setDouble("xFort", xFort);
        msg.setDouble("yFort", yFort);
        msg.setDouble("xChest", xChest);
        msg.setDouble("yChest", yChest);

        //number of players which are currently logged in
        msg.setInteger("numPlayers", numPlayers);

        for (int i = 1; i <= numPlayers; i++) {

//            msg.setDouble("xPlayer" + i, (Double)(hm.get("xPlayer" + i)));
//            msg.setDouble("yPlayer" + i, (Double)(hm.get("yPlayer" + i)));
//            msg.setDouble("hPlayer" + i, (Double)(hm.get("hPlayer" + i)));
//            msg.setInteger("player" + i, (Integer)(hm.get("player" + i)));
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

            //testing....
//            objectJoined(1);
//            objectJoined(2);
//            objectJoined(3);
//            objectJoined(4);
//            objectJoined(5);



            if(!collision()) {

                //broadcast to all new position of the player
//                response.setInteger("plUpdate",123456);
//                response.setDouble("x", xPosP1);
//                response.setDouble("y", yPosP1);
//                //broker.sendBroadcast(response);
//                broker.send(response);

//                response.setBoolean("move", true);

                //testing init; init should be called from objectJoint later on
                return init(5);


//                return response;

                //            return null;
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
        //todo remove an id from hash map...-->  hm.remove("player" + id);
        //todo then numPlayers --;
    }

    public void objectJoined(int id) {

        //putting new player in a hash map
        if(!(hm.containsKey("player" + id))) {
            hm.put("xPlayer" + id, 1.123456);
            hm.put("yPlayer" + id, 0.0);
            hm.put("hPlayer" + id, 0.0);
        }

        hm.put("player" + id, id);
        numPlayers ++;
        init(id);   //send initialized world back to the client
        //broker.sendBroadcast(playerJoint(id));  //tell everyone about new player
        //broker.send(playerJoined(id));   //for testing only (won't be used)
    }

    public Message playerJoined(int id) {

        //should be in the form (new Message(ID))
        Message msg = new Message(0xFF00FF00,0x11EE11EE,0x195CC);

        msg.setString("mid", "playerJoined");
        msg.setDouble("xPlayer" + id, (Double)(hm.get("xPlayer" + id)));
        msg.setDouble("yPlayer" + id, (Double)(hm.get("yPlayer" + id)));
        msg.setDouble("hPlayer" + id, (Double)(hm.get("hPlayer" + id)));
        msg.setInteger("player" + id, (Integer)(hm.get("player" + id)));

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

