package org.senergy.ams.app;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.LiveData;
import org.senergy.ams.model.entity.User;
import org.senergy.ams.server.ServerSync;

import javax.crypto.SecretKey;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static LiveData liveData=new LiveData();

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Config config = new Config();
        config.init();
        ServerSync.start(Config.serverIp,Config.serverPort);
        SecretKey key = Jwts.SIG.HS256.key().build();
        Timer timer = new Timer();

        /// Create a separate thread for garbage collection
        Thread gcThread = new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                System.out.println("Garbage Collection Thread started.");
                System.gc(); // Trigger garbage collection
//                System.out.println("Garbage Collection Thread completed.");
            }

        });

        // Start the garbage collection thread
//        gcThread.start();
//        String secretString = Encoders.BASE64.encode(key.getEncoded());
//        System.out.println(secretString);
//
//        SecretKey key2 = Jwts.SIG.HS256.key().build();
//        String secretString2 = Encoders.BASE64.encode(key2.getEncoded());
//        System.out.println(secretString2);
//
//        User u = new User();
//        System.out.println(u.generateMD5("senergy1136"));

    }
}