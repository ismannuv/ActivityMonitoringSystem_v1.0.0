/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.sync;

import SIPLlib.Helper;
import org.senergy.ams.model.Config;

/**
 *
 * @author admin
 */
public class SyncPacket
{
    static final int HEADER_SIZE=5;
    static final int CRC_SIZE=2;
    public static final byte SMT_PACKET ='$', BB_PACKET ='!';
    byte type;
    int seqNo;
    public static int preSeqNo;
    public byte[] data;
    public SyncPacket(byte type,int seqNo,byte[] data)
    {
        this.seqNo=seqNo;
        this.data=data;
        this.type=type;
    }
    public static SyncPacket decode(byte[] request)            
    {
        if(request!=null)
        {
//            System.out.println("Rx:"+Helper.byteArrayToHexString(request));
            Config.logger.finer("Rx:"+Helper.byteArrayToHexString(request));
            if(request.length>SyncPacket.HEADER_SIZE)
            {
                int seqNo= (request[2] & 0xFF) << 8 | (request[1] & 0xFF);
                int size= (request[4] & 0xFF) << 8 | (request[3] & 0xFF);
                if(request[0]==SyncPacket.SMT_PACKET || request[0]==SyncPacket.BB_PACKET)
                {
                    if((request.length-(SyncPacket.HEADER_SIZE+SyncPacket.CRC_SIZE)) == size)
                    {
                        if(Helper.checkCRC(request, 0, request.length))
                        {
                            byte[] data=new byte[size];
                            System.arraycopy(request, SyncPacket.HEADER_SIZE, data, 0, size);
                            return new SyncPacket(request[0],seqNo,data);
                        }else
                        {
                            Config.logger.info("############################555555 CRC failed :");
                        }
                    }
                }
            }
        }
        return null;
    }
    public static byte[] encode(SyncPacket pkt) 
    {
        byte[] response=new byte[pkt.data.length+(SyncPacket.HEADER_SIZE+SyncPacket.CRC_SIZE)];
        response[0]=(byte) (pkt.type & 0xFF);
        preSeqNo= pkt.seqNo;
        response[1]=(byte) ((pkt.seqNo ) & 0xFF);
        response[2]=(byte) ((pkt.seqNo>> 8) & 0xFF);
        response[3]=(byte) ((pkt.data.length ) & 0xFF);
        response[4]=(byte) ((pkt.data.length>> 8) & 0xFF);
        if(pkt.data.length>0)
            System.arraycopy(pkt.data,0,response,SyncPacket.HEADER_SIZE,pkt.data.length);
        byte[] crc = Helper.getCRC(response, 0, response.length-SyncPacket.CRC_SIZE);
        response[response.length-2]=crc[0];
        response[response.length-1]=crc[1];    
        
        Config.logger.finer("Tx:"+Helper.byteArrayToHexString(response));
        return response;
    }
}