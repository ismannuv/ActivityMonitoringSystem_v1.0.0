package org.senergy.ams.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.DBoperationException;
import org.senergy.ams.model.Json;
import org.senergy.ams.model.JsonJackson;
import org.senergy.ams.model.entity.User;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JwtHandler {

    public User webOperator=null;
    public void JwtHandler(){
        this.webOperator=null;
    }
    public String createJwtToken(boolean isDummyUser,String userObj, Key key)
    {
        String jwt=null;

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE,15);
        // Build the JWT token
        if(isDummyUser){//no expiry
            jwt = Jwts.builder()
                    .signWith(key)
                    .claim("user",userObj)
                    .compact();
        }else {
            jwt = Jwts.builder()
                    .signWith(key)
                    .expiration(calendar.getTime())
                    .claim("user",userObj)
                    .compact();
        }

        return jwt;
    }
    private boolean validateJwtToken(String jwt,SecretKey key) throws DBoperationException, JsonProcessingException {
        boolean status=false;
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
        if(claims.getExpiration()!=null && claims.getExpiration().before(new Date())){
            System.out.println("jwt expired :"+jwt);
            System.out.println("jwt expired date :"+claims.getExpiration());
            //jwt expired

        }else {

                String user= (String) claims.get("user");
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode userObj = null;
                userObj = objectMapper.readTree(user);
                //check here user is exist or not
                System.out.println("jwt user :"+userObj);
                webOperator= new User();

                JsonObject jsonObject =new JsonObject();
                jsonObject.add("id",new JsonPrimitive(userObj.get("id").asText()));
                webOperator=  objectMapper.treeToValue(webOperator.get(null,userObj), User.class);

                status=true;


        }
        return status;
    }
    public boolean isValidUser(HttpExchange exchange, JsonJackson jsonResponse) throws IOException {
        boolean status=false;
        String cause="";
        try {
            // Extract JWT from the Authorization header
            List<String> authorizationHeaders = exchange.getRequestHeaders().get("Authorization");

            if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                String authorizationHeader = authorizationHeaders.get(0);
                if (authorizationHeader.startsWith("Bearer ") ) {
                    // Extract the token without the "Bearer " prefix
                    status=this.validateJwtToken(authorizationHeader.substring(7),Config.JWT_KEY);


                }
            }
        }catch (Exception e ){
            cause=e.getMessage();
            e.printStackTrace();
            status=false;

        }finally {
           if(!status)
           {
               jsonResponse.setError(440,cause,"jwt token validation failed");
               exchange.sendResponseHeaders(440, jsonResponse.toString().length());
               OutputStream os = exchange.getResponseBody();
               os.write(jsonResponse.toString().getBytes());
               os.close();
           }

        }
        return !status;

    }
}
