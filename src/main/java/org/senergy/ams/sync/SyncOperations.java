/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.sync;


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
        switch (pkt.type)
        {
        }
        return pkt;
    }
}
