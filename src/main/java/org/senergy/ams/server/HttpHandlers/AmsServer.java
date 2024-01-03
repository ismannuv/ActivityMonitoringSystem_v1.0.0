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
import java.text.SimpleDateFormat;
import java.util.Date;

public class AmsServer implements HttpHandler {

    public static ObjectNode respObjectNode;
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
            if (respObjectNode == null)
            {
                respObjectNode= objectMapper.createObjectNode();
            }
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            JsonJackson jsonRequest = new JsonJackson(jsonNode.toString());
            JsonJackson jsonResponse = new JsonJackson(jsonRequest.operation);
            if(AMS.serialComm.isCabinetBusy()){
                jsonResponse.setError("Cabinet is busy");

            }else{
                try {
                    switch (jsonRequest.operation){
                        case SyncCommands.GET_DATETIME:
                        {
                            SyncPacket tx =new SyncPacket(SyncPacket.BB_PACKET, 0, new byte[]{0x11});
                            AMS.serialComm.exchangeNew(SyncPacket.encode(tx),2000);
                            if(!respObjectNode.isEmpty())
                            {

                                jsonResponse.data.add(respObjectNode);
                                jsonResponse.status=true;


                            }else{
                                jsonResponse.setError("failed to get resp or timeout");
                            }

                        }
                        break;
                        case SyncCommands.SET_ENROLLMENT_CMD:
                        {

                        }
                        break;
                        default:
                            jsonResponse.setError("operation not supported");
                            break;
                    }
                } catch (Exception ex) {
                    jsonResponse.setError(ex);
                    Helper.printStackTrace(ex);
                }
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
