package org.senergy.ams.app;

import SIPLlib.DBaccess2;
import SIPLlib.DataTable;
import SIPLlib.SIPLlibException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.senergy.ams.hw.CommandSyncService;
import org.senergy.ams.hw.KeySlot;
import org.senergy.ams.hw.SerialConnectionService;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.DBconnection;
import org.senergy.ams.model.DBoperationException;
import org.senergy.ams.model.LiveData;
import org.senergy.ams.model.entity.Key;
import org.senergy.ams.server.ServerSync;

import java.util.Date;

public class AMS {
    public static LiveData liveData=new LiveData();
    public static KeySlot[] keySlots;

    public static long pktRx=0,pktTx=0,secCount=0;
    public static SerialConnectionService serialComm=null;
    public static String portAvailable="Unavailable";
    public static String processingPktFrom="",error="";

    public static boolean IAPmode=false;
    public static int IAPtimeout=0;
    public static CommandSyncService commandSync;


    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.init();
        ServerSync.start(Config.serverIp,Config.serverPort);
//        SecretKey key = Jwts.SIG.HS256.key().build();
        commandSync=new CommandSyncService();
        Thread t = new Thread(commandSync);
        t.start();
        serialListen();

        /*while (true) {
            try {


                secCount++;
                if(secCount>=10)
                {
                    secCount=0;
//                    if(IAPtimeout>0)
//                        IAPtimeout--;

//                    updateProcessStatus();
                }
                Thread.sleep(10);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/



    }
    public static void serialListen()
    {
        if(serialComm==null)
        {
            serialComm = new SerialConnectionService(Config.serialPort, Config.serialBaud);
        }
        serialComm.run();
    }

    private static void updateProcessStatus()
    {
        DBaccess2 DBcon= DBconnection.newInstance();
        JsonObject obj=new JsonObject();
        obj.add("version", new JsonPrimitive(Config.version));
        obj.add("listeningAt", new JsonPrimitive(portAvailable));
        obj.add("pktRx", new JsonPrimitive(pktRx));
        obj.add("pktTx", new JsonPrimitive(pktTx));
        obj.add("processingPktFrom", new JsonPrimitive(processingPktFrom));
        obj.add("error", new JsonPrimitive(error));
        try {
            DBcon.preparedQuery("update processStatus set runningAt='"+Config.dateFormat.format(new Date())+"',status=? where id=1",obj.toString());
        } catch (SIPLlibException e) {
            throw new RuntimeException(e);
        }
    }
    private static void initKeySlot(){
        //initializing key slots
        AMS.keySlots=new KeySlot[32];
        for (int i = 0; i < keySlots.length; i++) {
            AMS.keySlots[i]=new KeySlot();
        }
        //getting all configured keys
        try {
            DBaccess2 DBcon=DBconnection.newInstance();
            String qry = "Select k.tagUid from key k order by k.position";
            if (DBcon.dqlQuery(qry)) {
                DataTable dt = DBcon.getResultSet();
                Key key = null;
                while (dt.next()) {
                    AMS.keySlots[dt.getInt("position")-1].setConfiguredTagUid(dt.getBigInteger("tagUid"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}