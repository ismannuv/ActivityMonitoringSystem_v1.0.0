package org.senergy.ams.app;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.senergy.ams.model.Config;
import org.senergy.ams.server.ServerSync;

import javax.crypto.SecretKey;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Config config = new Config();
        config.init();
        ServerSync.start(Config.serverPort);
        SecretKey key = Jwts.SIG.HS256.key().build();
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        System.out.println(secretString);

        SecretKey key2 = Jwts.SIG.HS256.key().build();
        String secretString2 = Encoders.BASE64.encode(key2.getEncoded());
        System.out.println(secretString2);




    }
}