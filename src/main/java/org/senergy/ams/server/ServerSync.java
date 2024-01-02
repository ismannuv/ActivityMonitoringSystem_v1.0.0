/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.server;

import com.sun.net.httpserver.*;
import org.senergy.ams.app.Constants;
import org.senergy.ams.model.Config;
import org.senergy.ams.server.HttpHandlers.AccountOperations;
import org.senergy.ams.server.HttpHandlers.AmsServer;
import org.senergy.ams.server.HttpHandlers.DBEntityOperations;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Level;


/**
 * @author ismannuv
 */
public class ServerSync {

    public static boolean isBusy;
    public static void start(String ip,int port)  {
        try{

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
//            HttpsServer server = HttpsServer.create(new InetSocketAddress("192.168.1.65",port), 0);
            server.createContext(Constants.ACCOUNT_OPERATION_URL,new AccountOperations());
            server.createContext(Constants.DBENTITY_OPERATION_URL,new DBEntityOperations());
            server.createContext(Constants.AMS_SERVER_URL,new AmsServer());
//                context.setHandler(ServerSync::handleRequest);
            server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
            if(!Config.enableHttps)
            {
                server.start();
                System.out.println("http server started at : http:/"+ server.getAddress());
            }else
            {

//                Config.logger.info("Staring HTTPS port at :"+port);
//
//                SSLContext sslContext = SSLContext.getInstance("TLS");
//
//                // initialise the keystore
//                char[] password = Config.keyStorePassword.toCharArray();
//                KeyStore ks = KeyStore.getInstance(Config.keyStoreType);
//                FileInputStream fis = new FileInputStream(Config.sslKeyStorePathWithName);
//                ks.load(fis, password);
//
//                // setup the key manager factory
//                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
////            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//                kmf.init(ks, password);
//
//                // setup the trust manager factory
//                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//                tmf.init(ks);
//
//                // setup the HTTPS context and parameters
//                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//                server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
//                    @Override
//                    public void configure(HttpsParameters params) {
//                        try {
//                            // initialise the SSL context
//                            SSLContext c = getSSLContext();
//                            SSLEngine engine = c.createSSLEngine();
//                            /*//System.out.println("$$$$$$$$"+c.getDefault().getSupportedSSLParameters().getProtocols());
//                            for (String s:c.getDefault().getSupportedSSLParameters().getProtocols()
//                                 ) {
//                                //System.out.println("$$$$$$$$1 :"+s);
//                            }*/
//                            params.setNeedClientAuth(true);
//                            params.setCipherSuites(engine.getEnabledCipherSuites());
//                            params.setProtocols(engine.getEnabledProtocols());
//
//                            // Set the SSL parameters
//                            SSLParameters sslParameters = c.getSupportedSSLParameters();
//                            params.setSSLParameters(sslParameters);
//
//                        } catch (Exception ex) {
////                            //System.out.println("Failed to create HTTPS port");
//                            Config.logger.info("Failed to create HTTPS port");
//                            //System.out.println(ex.getMessage());
//                            ex.printStackTrace();
//                        }
//                    }
//                });
//                server.start();
//                Config.logger.info("HTTPS Server running  port at :https:/"+server.getAddress());
            }



        }
        catch(Exception e)
        {
            Config.logException(Level.SEVERE, e);
        }
//        try{
//            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
//            HttpContext context = server.createContext(Config.postUrl);
//            context.setHandler(DataSyncServer::handleRequest);
//            server.start();
//            Config.logger.info("/*************************** SERVER STARTED AT PORT : "+port+" **********************************/");
//
//        }
//        catch(Exception e)
//        {
//            Config.logException(Level.SEVERE, e);
//        }
   }
   private static void handleRequest(HttpExchange exchange) {
        try {
            byte[] buff=new byte[100000];
            int len=0;
            int rd;
            InputStream is = exchange.getRequestBody();
//            //System.out.println("going in while");
            while((rd=is.read())!=-1)
            {
                buff[len]=(byte)rd;
                len++;
                if(len>=buff.length)
                    break;
            }
//            //System.out.println("out side while");
            byte[] requestData=new byte[len];
//            //System.out.println("requestData :"+requestData.length);
            System.arraycopy(buff, 0, requestData, 0, len);
            is.close();
//            //System.out.println("new String(requestData) :"+new String(requestData));


            byte[] response = process(requestData);

            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Headers", "*");

            if(response ==null)
            {
                //response code and length
                exchange.sendResponseHeaders(500, 1);
                OutputStream os = exchange.getResponseBody();
                os.write(new byte[]{0});
                os.close();
            }else {
                //response code and length
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

        }catch (Throwable ex)
        {
            ex.printStackTrace();
        }finally {

        }
    }

    private static byte[] process(byte[] requestData) {
        byte[] bytes = new byte[3];
        return bytes;
    }



    /*private static byte[] processAES(byte[] requestData)
    {
        if ((Config.logConfig & 2) > 0) {
            Config.logger.info("Req :"+Helper.byteArrayToHexString(requestData));
        }

        byte[] resp = null;

        if(requestData.length>6)
        {
            byte[] keyBytes = new byte[]{'S','e','n','e','r','g','y','A','E','S',0,0,0,0,0,0};
            System.arraycopy(requestData,0, keyBytes, 10, 6);
//                //System.out.println("key:"+Helper.byteArrayToHexString(keyBytes));
            AES aes=new AES(keyBytes);
            byte[] data=aes.decrypt(requestData,6,requestData.length-6);
            ResponsePacket responsePacket = new ResponsePacket();
            if(data!=null)
            {
                RequestPacket requestPacket = new RequestPacket(new String(data));
                if(requestPacket.isValid)
                {
                    responsePacket.setOperation(requestPacket.operation);
                    switch (requestPacket.operation)
                    {
                        case RequestPacket.ENROLL_USER:
                        {
                            isBusy=true;
                            FaceApi faceApi = new FaceApi();
                            try {
                                faceApi.connectDevice(requestPacket.deviceIp,requestPacket.gatewayIp,requestPacket.devicePort,requestPacket.useSSL);
                                faceApi.enrollUserWithFace(requestPacket.userIds[0]);

                                responsePacket.setStatus(true);
                                return responsePacket.toByteArray(aes,true);
                            } catch (Exception e) {
                                Config.logException(Level.SEVERE, e);
                            }finally {
                                isBusy=false;
                            }

                        }
                        break;
                        case RequestPacket.UPLOAD_USER:
                        {

                        }
                        break;
                        case RequestPacket.DELETE_USER:
                        {

                        }
                        break;
                        case RequestPacket.SEARCH_USER:
                        {

                        }
                        break;
                        case RequestPacket.DELETE_ALL_USER:
                        {

                        }
                        break;
                        default:
                            break;
                    }
                }else
                {
                    responsePacket.setStatus(false);
                    responsePacket.setError("Request packet is not valid");
                    return responsePacket.toByteArray(aes,true);
                }

            }
            else
            {
                //System.out.println("AES failed");
                responsePacket.setStatus(false);
                responsePacket.setError("AES failed");
                return responsePacket.toByteArray(aes,true);
            }
        }

        if ((Config.logConfig & 2) > 0) {
            Config.logger.info("Client req : "+ Helper.byteArrayToHexString(resp));
        }
        return resp;
    }*/

    /*private static byte[] process(byte[] requestData)
    {
        byte[] resp = null;

        ResponsePacket responsePacket = new ResponsePacket();
        if(isBusy)
        {
            responsePacket.setStatus(false);
            responsePacket.setError("Please wait ...Server is busy");
            return responsePacket.toByteArray(null,false);
        }
        if(requestData!=null)
        {
            RequestPacket requestPacket = new RequestPacket(new String(requestData));
            if(requestPacket.isValid)
            {
                responsePacket.setOperation(requestPacket.operation);
                switch (requestPacket.operation)
                {
                    case RequestPacket.ENROLL_USER:
                    {
                        isBusy=true;
                        FaceApi faceApi = new FaceApi();
                        try {
                            faceApi.connectDevice(requestPacket.deviceIp,requestPacket.gatewayIp,requestPacket.devicePort,requestPacket.useSSL);
                            faceApi.enrollUserWithFace(requestPacket.userIds[0]);

                            responsePacket.setStatus(true);
                            responsePacket.addProcessedUserToResponse(requestPacket.userIds[0],true);
                            return responsePacket.toByteArray(null,false);
                        } catch (Exception e) {

                            Config.logException(Level.SEVERE, e);
                            responsePacket.setStatus(false);

                            if(e.getCause()==null)
                            {
                                responsePacket.setError(e.getMessage());
                            }else
                            {
                                responsePacket.setError(e.getCause().getMessage());
                            }



                        }finally {
                            try {
                                faceApi.closeDevice();
                            } catch (Exception e) {
                                Config.logException(Level.SEVERE, e);
                            }
                            isBusy=false;
                        }

                    }
                    break;
                    case RequestPacket.UPLOAD_USER:
                    {
                        isBusy=true;
                        FaceApi faceApi = new FaceApi();
                        try {
                            faceApi.connectDevice(requestPacket.deviceIp,requestPacket.gatewayIp,requestPacket.devicePort,requestPacket.useSSL);
                            faceApi.deleteExistingUserMulti(requestPacket.userIds);
                            for (int i = 0; i < requestPacket.userIds.length; i++) {
                                String userId = requestPacket.userIds[i];
                                if(faceApi.uploadUser(userId)){
                                    responsePacket.addProcessedUserToResponse(userId,true);
                                }else
                                {
                                    responsePacket.addProcessedUserToResponse(userId,true);
                                }
                            }
                            responsePacket.setStatus(true);
                            return responsePacket.toByteArray(null,false);
                        } catch (Exception e) {

                            Config.logException(Level.SEVERE, e);
                            responsePacket.setStatus(false);
                            if(e.getCause()==null)
                            {
                                responsePacket.setError(e.getMessage());
                            }else
                            {
                                responsePacket.setError(e.getCause().getMessage());
                            }

                        }finally {
                            try {
                                faceApi.closeDevice();
                            } catch (Exception e) {
                                Config.logException(Level.SEVERE, e);
                            }
                            isBusy=false;
                        }
                    }
                    break;
                    case RequestPacket.DELETE_USER:
                    {
                        isBusy=true;
                        FaceApi faceApi = new FaceApi();
                        try {
                            faceApi.connectDevice(requestPacket.deviceIp,requestPacket.gatewayIp,requestPacket.devicePort,requestPacket.useSSL);
                            for (int i = 0; i < requestPacket.userIds.length; i++) {
                                String userId = requestPacket.userIds[i];
                                if(faceApi.checkUserExists(userId))
                                {
                                    if(faceApi.deleteExistingUser(userId))
                                    {
                                        responsePacket.addProcessedUserToResponse(userId,true);
                                    }
                                }else
                                {
                                    responsePacket.addProcessedUserToResponse(userId,true);
                                }

                            }
                            responsePacket.setStatus(true);
                            return responsePacket.toByteArray(null,false);
                        } catch (Exception e) {

                            Config.logException(Level.SEVERE, e);
                            responsePacket.setStatus(false);
                            if(e.getCause()==null)
                            {
                                responsePacket.setError(e.getMessage());
                            }else
                            {
                                responsePacket.setError(e.getCause().getMessage());
                            }

                        }finally {
                            try {
                                faceApi.closeDevice();
                            } catch (Exception e) {
                                Config.logException(Level.SEVERE, e);
                            }
                            isBusy=false;
                        }

                    }
                    break;
                    case RequestPacket.SEARCH_USER:
                    {
                        isBusy=true;
                        FaceApi faceApi = new FaceApi();
                        try {
                            faceApi.connectDevice(requestPacket.deviceIp,requestPacket.gatewayIp,requestPacket.devicePort,requestPacket.useSSL);
                            List<UserInfo> userInfoList= faceApi.checkUserExistsMulti(requestPacket.userIds);
                            for (int i = 0; i < requestPacket.userIds.length; i++) {
                                if(userInfoList.isEmpty())
                                {
                                    responsePacket.addProcessedUserToResponse(requestPacket.userIds[i],false);
                                }else {
                                    boolean userFoundWithFace =false;
                                    for (UserInfo user : userInfoList) {
                                        if (requestPacket.userIds[i].equalsIgnoreCase(user.getHdr().getID())) {
                                            if (user.getFacesCount() > 0) {
                                                userFoundWithFace = true;

                                            }
                                            break;
                                        }
                                    }
                                    if(userFoundWithFace)
                                    {
                                        responsePacket.addProcessedUserToResponse(requestPacket.userIds[i], true);
                                    }else
                                    {
                                        responsePacket.addProcessedUserToResponse(requestPacket.userIds[i], false);
                                    }
                                }
                            }
                            responsePacket.setStatus(true);
                            return responsePacket.toByteArray(null,false);
                        } catch (Exception e) {

                            Config.logException(Level.SEVERE, e);
                            responsePacket.setStatus(false);
                            if(e.getCause()==null)
                            {
                                responsePacket.setError(e.getMessage());
                            }else
                            {
                                responsePacket.setError(e.getCause().getMessage());
                            }

                        }finally {
                            try {
                                faceApi.closeDevice();
                            } catch (Exception e) {
                                Config.logException(Level.SEVERE, e);
                            }
                            isBusy=false;
                        }
                    }
                    break;
                    case RequestPacket.DELETE_ALL_USER:
                    {
                        isBusy=true;
                        FaceApi faceApi = new FaceApi();
                        try {
                            faceApi.connectDevice(requestPacket.deviceIp,requestPacket.gatewayIp,requestPacket.devicePort,requestPacket.useSSL);
                            faceApi.deleteAllUsers();
                            responsePacket.setStatus(true);
                            return responsePacket.toByteArray(null,false);
                        } catch (Exception e) {
                            Config.logException(Level.SEVERE, e);
                            responsePacket.setStatus(false);
                            if(e.getCause()==null)
                            {
                                responsePacket.setError(e.getMessage());
                            }else
                            {
                                responsePacket.setError(e.getCause().getMessage());
                            }

                        }finally {
                            try {
                                faceApi.closeDevice();
                            } catch (Exception e) {
                                Config.logException(Level.SEVERE, e);
                            }
                            isBusy=false;
                        }
                    }
                    break;
                    default:
                        break;
                }

            }else
            {
                responsePacket.setStatus(false);
                responsePacket.setError("Request packet is not valid - "+ requestPacket.getValidPktError());
                return responsePacket.toByteArray(null,false);
            }

        }
        else
        {
//            //System.out.println("AES failed");
            responsePacket.setStatus(false);
            responsePacket.setError("Failed to process your request, invalid pkt");
            return responsePacket.toByteArray(null,false);
        }


        if(responsePacket.getOperation()==0)
        {
            responsePacket.setStatus(false);
            responsePacket.setError("Failed to process your request");
        }

        return responsePacket.toByteArray(null,false);
    }*/


}
