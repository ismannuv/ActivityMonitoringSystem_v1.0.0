/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.sync;

import SIPLlib.Helper;

import java.math.BigInteger;

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

                       txPkt=processServerPacket(rxPkt);
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
    private static SyncPacket processServerPacket(SyncPacket pkt)
    {
        switch (pkt.data[0])
        {
            case (byte) (SyncCommands.AUTHENTICATION & 0xFF):
            {
                int authType= Helper.getUint8(pkt.data,1);
                switch (authType){
                    case 1://card
                    {
                        BigInteger cardUid=Helper.getUint64_BE(pkt.data, 2);

                    }
                    break;
                    case 2://FP
                    {
                        long userId=Helper.getUint32_BE(pkt.data, 2);
                    }
                    break;
                    case 3://PINs
                    {
                        long pin=Helper.getUint32_BE(pkt.data, 2);
                    }
                    break;
                }
            }
            break;
            case (byte) (SyncCommands.EVENTS & 0xFF):
            {

            }
            break;

        }
        return pkt;
    }
}
