package org.senergy.ams.server.HttpHandlers;

import SIPLlib.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.Json;
import org.senergy.ams.server.JwtHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class DbEntityOperations implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
//        System.out.println(path);

        Headers headers = exchange.getResponseHeaders();

        try {
            InputStream requestBody = exchange.getRequestBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            Json jsonRequest = new Json(jsonNode.toString());
            Json jsonResponse = new Json(jsonRequest.operation);
            JwtHandler jwtHandler = new JwtHandler();
            //if user is login

            if(jwtHandler.isValidUser(exchange, jsonResponse))//checking jwt token validation & user validation
                return;

            try {
                switch (jsonRequest.operation){
                    case DBentity.ADD:
                    {

                    }
                    break;
                    case DBentity.GET:
                    {

                    }break;
                    default:
                    {
                        jsonResponse.setError("Operation not supported.");
                        jsonResponse.status = false;
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
