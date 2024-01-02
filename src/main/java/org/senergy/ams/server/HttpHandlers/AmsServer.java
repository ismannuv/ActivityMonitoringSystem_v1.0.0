package org.senergy.ams.server.HttpHandlers;

import SIPLlib.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.JsonJackson;
import org.senergy.ams.model.entity.User;
import org.senergy.ams.server.JwtHandler;
import org.senergy.ams.sync.SyncCommands;
import org.senergy.ams.sync.SyncPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AmsServer implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
//        System.out.println(path);

        Headers headers = exchange.getResponseHeaders();
        // Allow all origins (replace "*" with the specific origin you want to allow)
        headers.add("Access-Control-Allow-Origin", "*");

        // Allow specific headers
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Allow specific HTTP methods
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Set the maximum age for the preflight request (in seconds)
        headers.add("Access-Control-Max-Age", "3600");
        headers.add("Access-Control-Allow-Credentials", "true");

        try {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                // For CORS preflight requests, just return 200 OK
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            InputStream requestBody = exchange.getRequestBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            JsonJackson jsonRequest = new JsonJackson(jsonNode.toString());
            JsonJackson jsonResponse = new JsonJackson(jsonRequest.operation);
            try {
                switch (jsonRequest.operation){
                    case SyncCommands.GET_DATETIME:
                    {
                        SyncPacket tx =new SyncPacket(SyncPacket.BB_PACKET, 0, new byte[]{0x12});
                        if (AMS.serialComm.writeBytes(SyncPacket.encode(tx)))
                        {

                        }else{

                        }
                    }
                    break;
                    case SyncCommands.SET_ENROLLMENT_CMD:
                    {

                    }
                    break;
                }
            } catch (Exception ex) {
                jsonResponse.setError(ex);
                Helper.printStackTrace(ex);
            }

            exchange.sendResponseHeaders(200, jsonResponse.toString().length());
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.toString().getBytes());
            os.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
