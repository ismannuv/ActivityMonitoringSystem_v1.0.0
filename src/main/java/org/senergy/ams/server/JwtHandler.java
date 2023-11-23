package org.senergy.ams.server;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Calendar;

public class JwtHandler {
    public String createJwtToken()
    {
        String jwt=null;
        SecretKey key = Jwts.SIG.HS256.key().build();
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE,1);
        // Build the JWT token
         jwt = Jwts.builder()
                .subject("mannu")
                .signWith(key)
                .expiration(calendar.getTime())
                .compact();
        return jwt;
    }
}
