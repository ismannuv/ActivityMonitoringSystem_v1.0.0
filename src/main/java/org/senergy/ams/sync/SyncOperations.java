/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.sync;

import SIPLlib.Helper;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.entity.User;

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
            case (byte) (SyncCommands.AUTHENTICATION & 0xFF):
            {

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
