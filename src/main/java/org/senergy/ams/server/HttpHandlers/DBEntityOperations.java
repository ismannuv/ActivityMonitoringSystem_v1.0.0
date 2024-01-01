package org.senergy.ams.server.HttpHandlers;

import SIPLlib.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.JsonJackson;
import org.senergy.ams.model.entity.User;
import org.senergy.ams.server.JwtHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class DBEntityOperations implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        long totalRAM =Runtime.getRuntime().totalMemory();
        long freeRAM=Runtime.getRuntime().freeMemory();
        long RAMinUse=totalRAM-freeRAM;
        System.out.println("RAM t:"+totalRAM+",f:"+freeRAM+",u:"+RAMinUse);
        System.gc();
         totalRAM =Runtime.getRuntime().totalMemory();
         freeRAM=Runtime.getRuntime().freeMemory();
         RAMinUse=totalRAM-freeRAM;
        System.out.println("RAM gc t:"+totalRAM+",f:"+freeRAM+",u:"+RAMinUse);
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
            JwtHandler jwtHandler = new JwtHandler();
            //if user is login

            if(!Config.debugMode){
//                if(jwtHandler.isValidUser(exchange, jsonResponse))//checking jwt token validation & user validation
//                    return;
            }else {
                jwtHandler.webOperator=new User();
            }


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
                            dbObjects[i].fromJson(jsonRequest.data.get(i));
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
                            dbObjects[i].fromJson(jsonRequest.data.get(i));
                            if (webOperator.updateDBentity(dbObjects[i])) {
                                jsonResponse.status = true;
                            }
                        }
                    }
                    break;
                    case DBentity.GET:
                    {
                        jsonResponse.data=User.getDBentity(((DBentity) DBentityClass),(jsonRequest.data.get(0)));
                        jsonResponse.status = true;

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
                        jsonResponse.data = User.getAllDBentity(((DBentity) DBentityClass), (jsonRequest.data.get(0)));
                        jsonResponse.status = true;
                        break;
                    case DBentity.GET_COUNT:
                        OutputStream os = null;
                        try {
                            User.getAllDBentityNew( ((DBentity) DBentityClass),jsonRequest.data.get(0));
                            exchange.sendResponseHeaders(200,0);
                            os = exchange.getResponseBody();
                            os.write("{ \"data\" :[".getBytes());
                            os.flush();
                            boolean first=true;

                            while ( true ){
                                if(AMS.liveData.startSending &&  !AMS.liveData.getAllQueueDataArrayNode.isEmpty() ){
//                                    jsonResponse.data = objectMapper.createArrayNode().add(AMS.liveData.getAllQueueData.remove());

                                    if(first){

                                        JsonNode res = AMS.liveData.getAllQueueDataArrayNode.poll();
                                        if(res!=null){
                                            first=false;
                                            os.write(res.toString().replaceFirst("\\[","").replaceFirst("]","").getBytes());
                                            os.flush();
                                        }else {
                                            System.out.println("res null");
                                        }


                                    }else {
                                        JsonNode res = AMS.liveData.getAllQueueDataArrayNode.poll();
                                        if(res!=null){
                                            os.write(",".getBytes());
                                            os.flush();

                                            os.write(res.toString().replaceFirst("\\[","").replaceFirst("]","").getBytes());
                                            os.flush();
                                        }else{
                                            System.out.println("res null1");

                                        }

                                    }


                                }else if(!AMS.liveData.fetchingLargeData){
                                    break;
                                }


                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                            jsonResponse.setError(ex);
//                            AMS.liveData.setError(ex);
                        }finally {
//                            os.write("]}".getBytes());
//                            os.close();
                            if(!AMS.liveData.getError().isEmpty()){
                                jsonResponse.setError(AMS.liveData.getError());
                            }else{
                                jsonResponse.status = true;

                            }
                            String res = jsonResponse.toString();
                            if (os != null) {
//                                os.write(",".getBytes());
//                                os.flush();
                                os.write("] ,\"status\" :".getBytes());
                                os.flush();
                                os.write(res.getBytes());
                                os.flush();
                                os.write("}".getBytes());
                                os.close();

                                /** ---- Final structure -------
                                 * {
                                 *     "data":[
                                 *      {},{},{}
                                 *     ],
                                 *     "status":{
                                 *
                                 *     }
                                 * }
                                 *
                                 */
                            }

                        }
                        System.out.println("$$$$$$111 :"+System.currentTimeMillis());
                        return;

//                        jsonResponse.data = User.getAllDBentity( ((DBentity) DBentityClass),jsonRequest.data.get(0));



//                        exchange.sendResponseHeaders(200, res.length());
//                        OutputStream os = exchange.getResponseBody();
//                        os.write(res.getBytes());
//                        os.close();
//                    break;

                    default:
                    {
                        jsonResponse.setError("Operation not supported.");
                        jsonResponse.status = false;
                    }
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                jsonResponse.setError(ex);
                Helper.printStackTrace(ex);
            }


            System.out.println("############111 "+System.currentTimeMillis());
            String res =jsonResponse.toString();
            exchange.sendResponseHeaders(200, res.length());
            OutputStream os = exchange.getResponseBody();

//            System.out.println("############%%% "+System.currentTimeMillis());
//            res.getBytes();
//            System.out.println("############%%% "+System.currentTimeMillis());
            os.write(res.getBytes());
            System.out.println("############222 "+System.currentTimeMillis());
            jsonResponse=null;
            jsonRequest=null;
            jsonNode=null;
            res=null;
            os.close();
            System.out.println("############333 "+System.currentTimeMillis());


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
