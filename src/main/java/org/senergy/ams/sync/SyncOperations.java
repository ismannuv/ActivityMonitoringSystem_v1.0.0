/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.sync;

import SIPLlib.Helper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.entity.User;
import org.senergy.ams.server.HttpHandlers.AmsServer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author admin
 */

public class SyncOperations  {

    public static byte[] process(byte[] request) 
    {       
        byte[] reply=new byte[0];
        SyncPacket rxPkt=SyncPacket.decode(request);
        SyncPacket txPkt = null;
        if(rxPkt!=null)
        {
           switch(rxPkt.type)
           {
               case SyncPacket.SMT_PACKET:
                   txPkt= processStmRequest(rxPkt);
               break;
               case SyncPacket.BB_PACKET:
                   processBbResponse(rxPkt);
                   break;
           }
           if(txPkt!=null)
            reply=SyncPacket.encode(txPkt);
        }
        if(reply!=null)
        {
//            Config.logger.info("@@@@@@@@@@@@@@@@reply :"+Helper.byteArrayToHexString(reply));

        }
        return reply;
    }

    private static void processBbResponse(SyncPacket pkt) {
        switch (pkt.data[0])
        {
            case (byte) (SyncCommands.GET_DATETIME & 0xFF):
            {
                long epoch =Helper.getUint32_BE(pkt.data,1);
                Date date=new Date(epoch*1000);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // Format the current date and time using the defined formatter
                String formattedDateTime = simpleDateFormat.format(date);
                AmsServer.respObjectNode.put("date",formattedDateTime);
            }
            break;
            case (byte) (SyncCommands.SET_ENROLLMENT_CMD & 0xFF):
            {
                int status = Helper.getUint8(pkt.data,1);
                if(status==1){
                    AmsServer.respObjectNode.put("status",status);
                }else {
                    AmsServer.respObjectNode.put("status",0);
                    AmsServer.respObjectNode.put("error","Cabinet busy");
                }
                /*switch (replyOf)
                {
                    case 0://card
                        break;
                    case 1://fp
                        break;
                    case 2://key uid enroll
                        break;
                }*/
            }
            break;
            case (byte) (SyncCommands.GET_ENROLLMENT_CMD_STATUS & 0xFF):
            {
                int replyOf = Helper.getUint8(pkt.data,1);
                int status = Helper.getUint8(pkt.data,2);//0- Pending ,1- Success , 2- failed ,3 -timeout
                if (status==1){
                    switch (replyOf)
                    {
                        case 0://card

                            break;
                        case 1://fp
                            break;
                        case 2://key uid enroll
                            break;
                    }
                }else{
                    AmsServer.respObjectNode.put("status",status);
                }

            }
            break;
            case (byte) (SyncCommands.SEND_HEALTH_PKT & 0xFF):
            {
                Config.logger.info("health pkt resp received");
            }
            break;
            case (byte) (SyncCommands.GET_ALL_CURRENT_KEY_STATUS & 0xFF):
            {
                int offset=0;
                int status = Helper.getUint8(pkt.data,1);// cmd status 0- failed 1- success
                offset++;
                if(status==1){
                    for (int i = 0; i < AMS.keySlots.length; i++) {
                        AMS.keySlots[i].setPresentTagUid(Helper.getUint64_BE(pkt.data,offset));
                        offset+=8;
                    }
                    AMS.commandSync.setGotCurrentKeyStatus(true);
                }
            }
            break;

        }
    }

    private static SyncPacket processStmRequest(SyncPacket pkt)
    {
        switch (pkt.data[0])
        {
            case (byte) (SyncCommands.AUTHENTICATION & 0xFF):
            {
                try {
                    byte[] rx = processUserAuth(pkt);
                    pkt.data=rx;
                } catch (Exception e) {
                    AMS.error="Auth error - "+e.getMessage();
                    pkt.data=new byte[]{0x1};
                    e.printStackTrace();
                }
            }
            break;
            case (byte) (SyncCommands.EVENTS & 0xFF):
            {

            }
            break;
            case (byte) (SyncCommands.GET_USER_ACTIVITY & 0xFF):
            {

            }
            break;

        }
        return pkt;
    }

    private static byte[]  processUserAuth(SyncPacket pkt) throws Exception {
        byte [] rx = new byte[48];
        int authType= Helper.getUint8(pkt.data,1);
        User user= new User();
        switch (authType){
            case 1://card
            {
                user.cardUID= Helper.getUint64_BE(pkt.data, 2);
            }
            break;
            case 2://FP
            {
                long userId=Helper.getUint32_BE(pkt.data, 2);
                user.id= String.valueOf(userId);

            }
            break;
            case 3://PIN
            {
                long pin=Helper.getUint32_BE(pkt.data, 2);
                user.pin= String.valueOf(pin);
            }
            break;
        }
        user.getUserBy(authType);
        System.out.println("user :"+user);
        int offSet=0;
        Helper.setUint8(rx,offSet, pkt.data[0]);
        offSet++;
        //cabinet policy 0 - single, 1 -dual auth policy
        Helper.setUint8(rx,offSet, (short) 1);
        offSet++;
        //status
        Helper.setUint8(rx,offSet, (short) user.status.getValue());
        offSet++;

        if(user.status!= User.USER_STATUS_ENUM.INVALID){
            //fp enrolled or not , 0- not enrolled, 1- enrolled
            Helper.setUint8(rx,offSet, (short) user.fp1);
            offSet++;
            //user data
            Helper.setUint32_BE(rx,offSet, Long.parseLong(user.id));
            offSet+=4;
            Helper.setString(rx,offSet,user.name.substring(0,User.NAME_SIZE));
            offSet+=User.NAME_SIZE;
            Helper.setUint64_BE(rx,offSet,user.cardUID);
            offSet+=8;
            Helper.setUint32_BE(rx,offSet, Long.parseLong(user.pin));
            offSet+=4;
        }
        return rx;

    }
}
