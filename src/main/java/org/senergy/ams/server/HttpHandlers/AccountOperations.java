package org.senergy.ams.server.HttpHandlers;

import SIPLlib.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.Json;
import org.senergy.ams.model.entity.Operator;
import org.senergy.ams.server.JwtHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class AccountOperations implements HttpHandler {
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

        try {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                // For CORS preflight requests, just return 200 OK
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            InputStream requestBody = exchange.getRequestBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            Json jsonRequest = new Json(jsonNode.toString());
            Json jsonResponse = new Json(jsonRequest.operation);
            JwtHandler jwtHandler = new JwtHandler();
            //if user is login
            if(jsonRequest.operation != Operator.Operation_enum.LOGIN.getValue())
            {
                if(jwtHandler.isValidUser(exchange, jsonResponse))
                    return;
            }
            try {
                switch (Operator.Operation_enum.get(jsonRequest.operation)){
                    case LOGIN:
                    {
                        Operator newOp = new Operator();
                        newOp.fromJson(jsonRequest.data.get(0).getAsJsonObject());
                        if (newOp.login()) {
                            jsonResponse.data.add(newOp.toJson());
                            jsonResponse.status = true;

                            String jwt = jwtHandler.createJwtToken(newOp.toJson().toString(), Config.JWT_KEY);
                            headers.add("Set-Cookie", "jwt=" + jwt);
                        } else {
                            jsonResponse.data.add(newOp.toJson());
                            jsonResponse.setError("Invalid user!");
                            jsonResponse.status = false;
                        }
                    }
                    break;
                    case LOGOUT:
                    {

                    }
                    break;
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
