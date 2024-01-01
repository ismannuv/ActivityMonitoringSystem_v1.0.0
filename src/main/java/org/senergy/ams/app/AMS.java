package org.senergy.ams.app;

import SIPLlib.DBaccess;
import SIPLlib.DBaccess2;
import SIPLlib.SIPLlibException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.jsonwebtoken.Jwts;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.DBconnection;
import org.senergy.ams.model.LiveData;
import org.senergy.ams.model.entity.User;
import org.senergy.ams.server.ServerSync;

import javax.crypto.SecretKey;
import java.util.Date;

public class AMS {
    public static LiveData liveData=new LiveData();

    public static long pktRx=0,pktTx=0,secCount=0;
    private static SerialConnection serialComm=null;
    public static String portAvailable="Unavailable";
    public static String processingPktFrom="",error="";

    public static boolean IAPmode=false;
    public static int IAPtimeout=0;


    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");
        Config config = new Config();
        config.init();
        ServerSync.start(Config.serverIp,Config.serverPort);
        SecretKey key = Jwts.SIG.HS256.key().build();

        User user =new User();
        user.id="1136";
        user.getUserBy(2);
        while (true) {
            try {
                serialListen();

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
        }



    }
    public static void serialListen()
    {
        if(serialComm==null)
        {
            serialComm = new SerialConnection(Config.serialPort, Config.serialBaud);
        }
        serialComm.stateMachine();
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
}