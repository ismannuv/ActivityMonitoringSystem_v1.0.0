package org.senergy.ams.server.HttpHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class AccountOperations implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange)  {

        // We need a signing key, so we'll create one just for this example. Usually
        // the key would be read from your application configuration instead.
        SecretKey key = Jwts.SIG.HS256.key().build();

        String jws = Jwts.builder().subject("Joe").signWith(key).compact();

        // Extract JWT from the Authorization header
        List<String> authorizationHeaders = exchange.getRequestHeaders().get("Authorization");

        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String authorizationHeader = authorizationHeaders.get(0);

            if (authorizationHeader.startsWith("Bearer ")) {
                // Extract the token without the "Bearer " prefix
                String jwtToken = authorizationHeader.substring(7);

                // Now you have the JWT token, and you can use it as needed
                System.out.println("JWT Token: " + jwtToken);

                // Add your logic to validate and process the JWT
                // For example, you might use a library like jjwt (Java JWT) to decode and verify the token

                // Send a response
                String response = "JWT Token: " + jwtToken;
                try {
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


        Headers headers = exchange.getResponseHeaders();
        System.out.println(headers);
        InputStream requestBody = exchange.getRequestBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(requestBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("jsonNode :"+jsonNode);
    }
}
