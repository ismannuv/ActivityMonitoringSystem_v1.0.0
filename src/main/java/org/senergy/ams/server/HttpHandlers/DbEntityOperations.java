package org.senergy.ams.server.HttpHandlers;

import SIPLlib.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.Json;
import org.senergy.ams.model.entity.PrivilegeGroup;
import org.senergy.ams.model.entity.User;
import org.senergy.ams.server.JwtHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class DbEntityOperations implements HttpHandler {
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
            Json jsonRequest = new Json(jsonNode.toString());
            Json jsonResponse = new Json(jsonRequest.operation);
            JwtHandler jwtHandler = new JwtHandler();
            //if user is login

            if(jwtHandler.isValidUser(exchange, jsonResponse))//checking jwt token validation & user validation
                return;

            User webOperator=jwtHandler.webOperator;
            try {
                DBentity[] dbObjects;
                String pkgName = User.class.getName();
                pkgName= pkgName.substring(0,pkgName.lastIndexOf('.'));
                Object DBentityClass = Class.forName(pkgName+'.'+jsonRequest.type).newInstance();

                switch (jsonRequest.operation){
                    case DBentity.ADD:
                    {
                        dbObjects = new DBentity[jsonRequest.data.size()];
                        for (int i = 0; i < dbObjects.length; i++) {
                            dbObjects[i] = ((DBentity) DBentityClass);
                            dbObjects[i].fromJson(jsonRequest.data.get(i).getAsJsonObject());
                            if (webOperator.addDBentity(dbObjects[i])) {
                                jsonResponse.status = true;
                            }
                        }
                    }
                    break;
                    case DBentity.UPDATE:
                    {
                        dbObjects = new DBentity[jsonRequest.data.size()];
                        for (int i = 0; i < dbObjects.length; i++) {
                            dbObjects[i] = ((DBentity) DBentityClass);
                            dbObjects[i].fromJson(jsonRequest.data.get(i).getAsJsonObject());
                            if (webOperator.updateDBentity(dbObjects[i])) {
                                jsonResponse.status = true;
                            }
                        }
                    }
                    break;
                    case DBentity.GET:
                    {
                        dbObjects = new DBentity[1];
                        dbObjects[0]=User.getDBentity(((DBentity) DBentityClass),(jsonRequest.data.get(0).getAsJsonObject()));
                        if(dbObjects[0] != null)
                        {
                            jsonResponse.status = true;
                            jsonResponse.data.add(dbObjects[0].toJson());
                        }
                    }break;
                    case DBentity.TEMPORARY_DELETE:
                        if (webOperator.temporaryDeleteDBentity(((DBentity) DBentityClass), jsonRequest.data)) {
                            jsonResponse.status = true;
                        }
                        break;
                    case DBentity.RESTORE:
                        if (webOperator.restoreDBentity(((DBentity) DBentityClass), jsonRequest.data)) {
                            jsonResponse.status = true;
                        }
                        break;
                    case DBentity.GET_ALL_DISABLED:
                    case DBentity.GET_ALL_ENABLED:
                    case DBentity.GET_ALL:
                        dbObjects = User.getAllDBentity( ((DBentity) DBentityClass),jsonRequest.data.get(0).getAsJsonObject());
                        if(dbObjects != null){
                            jsonResponse.status = true;
                            for (int i = 0; i < dbObjects.length; i++) {
                                jsonResponse.data.add(dbObjects[i].toJson());
                            }
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
