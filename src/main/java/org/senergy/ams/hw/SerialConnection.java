package org.senergy.ams.hw;

import SIPLlib.Helper;
import SIPLlib.SerialCommunication;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.Config;
import org.senergy.ams.sync.SyncOperations;
import org.senergy.ams.sync.SyncPacket;
import org.senergy.ams.utils.Global;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SerialConnection extends SerialCommunication implements SerialPortEventListener {
    public SerialConnection(String portName,int baud)
    {
        super(portName,baud,8,1,0);
    }

    private static enum STATES{UNKNOWN,CONFIG_DATETIME,IDLE,OPEN,WAIT_FOR_REQ,SEND_WAIT,IAP};
    private static enum RX_STATES {UNKNOWN,RX_START_BYTE,RX_SEQ_NO,RX_SIZE,RX__DATA,RX_CRC};
    private STATES state=STATES.UNKNOWN,prevState=STATES.UNKNOWN;
    private RX_STATES rxState=RX_STATES.RX_START_BYTE,prevRxState=RX_STATES.UNKNOWN;
    private byte[] rxBuff=new byte[1024];
    private boolean reqRecvd=false,cmdRecvd=false,cmdRespRecvd=false;
    private int delay=0;
    private int rxInPtr=0,dataPtr=0,dataLen=0,charTimeout=0,reqLen=0,pktError=0;


    public void stateMachineNew()
    {
        String result="";
        if(this.prevState!=this.state)
        {
            Config.logger.info("Main State : "+this.prevState+"-->"+this.state);
            this.prevState=this.state;
        }
        if(this.delay>0)
        {
            this.delay--;
        }
        try{
            switch(this.state)
            {
                case CONFIG_DATETIME:
                    if(this.delay==0)
                    {
                        result=getCabinetDatetime();
                        if(result!=null)
                        {
                            if(setServerDatetime(result))
                            {
                                /*Device d=new Device();
                                d.getServerConfig();
                                if(d.serverIP!=null)
                                {
                                    setServerNetwork(d.serverIP,d.serverGateway,d.serverMask);
                                }*/
                                this.state=STATES.IDLE;
                            }
                            else
                            {
                                this.delay=100;
                            }
                        }
                        else
                        {
                            this.delay=100;
                        }
                    }
                    break;
                case IDLE:
                    if(AMS.IAPmode)
                    {
                        this.close();
                        this.state=STATES.IAP;
                    }
                    else if(!this.cmdRecvd)
                    {
                        this.state=STATES.OPEN;
                    }

                    break;
                case IAP:
                    if(AMS.IAPtimeout==0)
                    {
                        AMS.IAPmode=false;
                        this.state=STATES.IDLE;
                    }
                    break;
                case OPEN:
                    if(this.open(this,this.serialPort.MASK_RXCHAR))
                    {
                        reqRecvd=false;
                        rxInPtr=0;
                        rxState=RX_STATES.RX_START_BYTE;
                        this.state=STATES.WAIT_FOR_REQ;
                        AMS.portAvailable=Config.serialPort;
                    }
                    else
                    {
                        AMS.portAvailable=Config.serialPort+" Unavailable";
                    }
                    break;
                case WAIT_FOR_REQ:

                    if(this.reqRecvd || this.cmdRespRecvd)
                    {
                        byte[] reqPacket=new byte[this.reqLen];
                        System.arraycopy(this.rxBuff, 0, reqPacket, 0, this.reqLen);
                        AMS.pktRx++;
                        AMS.processingPktFrom="master";
                        if((Config.logConfig&2)>0)
                        {
                            Config.logger.info("$$$$  req :"+ Helper.byteArrayToHexString(reqPacket));
                            Config.logger.info("$$$$ seq :"+Helper.getUint16_LE(reqPacket,1));
                            Config.logger.info("$$$$ data len :"+Helper.getUint16_LE(rxBuff, 3));
                        }

//                        Config.logger.info("##### req :"+Helper.byteArrayToHexString(reqPacket));
                        byte[] replyPacket = SyncOperations.process(reqPacket);
                        if(replyPacket!=null && replyPacket.length>0)
                        {
                            this.serialPort.writeBytes(replyPacket);
                            AMS.pktTx++;
                            delay=10;
                            this.state=STATES.SEND_WAIT;
                        }
                        else
                        {
                            Config.logger.info("-----> IN STATE STATES.SEND_WAIT");
                            delay=2;
                            this.state=STATES.SEND_WAIT;
                        }
                    }
                    break;
                case SEND_WAIT:
                    if(this.delay==0)
                    {
                        this.close();
                        this.state=STATES.IDLE;
                    }
                    break;
                default:
                    if(Config.serverTimeSync)
                    {
                        this.delay=1000;
                        this.state=STATES.CONFIG_DATETIME;
                    }
                    else
                    {
                        this.state=STATES.IDLE;
                    }
                    break;
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
            AMS.error=e.toString();
        }
    }
    public void stateMachine()
    {
        String result="";
        if(this.prevState!=this.state)
        {
            Config.logger.info("Main State : "+this.prevState+"-->"+this.state);
            this.prevState=this.state;
        }
        if(this.delay>0)
        {
            this.delay--;
        }
        try{
            switch(this.state)
            {
                case CONFIG_DATETIME:
                    if(this.delay==0)
                    {
                        result=getCabinetDatetime();
                        if(result!=null)
                        {
                            if(setServerDatetime(result))
                            {
                                /*Device d=new Device();
                                d.getServerConfig();
                                if(d.serverIP!=null)
                                {
                                    setServerNetwork(d.serverIP,d.serverGateway,d.serverMask);
                                }*/
                                this.state=STATES.IDLE;
                            }
                            else
                            {
                                this.delay=100;
                            }
                        }
                        else
                        {
                            this.delay=100;
                        }
                    }
                    break;
                case IDLE:
                    if(AMS.IAPmode)
                    {
                        this.close();
                        this.state=STATES.IAP;
                    }
                    else if(!this.cmdRecvd)
                    {
                        this.state=STATES.OPEN;
                    }

                    break;
                case IAP:
                    if(AMS.IAPtimeout==0)
                    {
                        AMS.IAPmode=false;
                        this.state=STATES.IDLE;
                    }
                    break;
                case OPEN:
                    if(this.open(this,this.serialPort.MASK_RXCHAR))
                    {
                        reqRecvd=false;
                        rxInPtr=0;
                        rxState=RX_STATES.RX_START_BYTE;
                        this.state=STATES.WAIT_FOR_REQ;
                        AMS.portAvailable=Config.serialPort;
                    }
                    else
                    {
                        AMS.portAvailable=Config.serialPort+" Unavailable";
                    }
                    break;
                case WAIT_FOR_REQ:

                    if(this.cmdRecvd)
                    {
                        this.close();
                        this.state=STATES.IDLE;
                    }
                    else if(this.reqRecvd)
                    {
                        byte[] reqPacket=new byte[this.reqLen];
                        System.arraycopy(this.rxBuff, 0, reqPacket, 0, this.reqLen);
                        AMS.pktRx++;
                        AMS.processingPktFrom="master";
                        if((Config.logConfig&2)>0)
                        {
                            Config.logger.info("$$$$  req :"+ Helper.byteArrayToHexString(reqPacket));
                            Config.logger.info("$$$$ seq :"+Helper.getUint16_LE(reqPacket,1));
                            Config.logger.info("$$$$ data len :"+Helper.getUint16_LE(rxBuff, 3));
                        }

//                        Config.logger.info("##### req :"+Helper.byteArrayToHexString(reqPacket));
                        byte[] replyPacket = SyncOperations.process(reqPacket);
                        if(replyPacket!=null && replyPacket.length>0)
                        {
                            this.serialPort.writeBytes(replyPacket);
                            AMS.pktTx++;
                            delay=10;
                            this.state=STATES.SEND_WAIT;
                        }
                        else
                        {
                            Config.logger.info("-----> IN STATE STATES.SEND_WAIT");
                            delay=2;
                            this.state=STATES.SEND_WAIT;
                        }
                    }
                    break;
                case SEND_WAIT:
                    if(this.delay==0)
                    {
                        this.close();
                        this.state=STATES.IDLE;
                    }
                    break;
                default:
                    if(Config.serverTimeSync)
                    {
                        this.delay=1000;
                        this.state=STATES.CONFIG_DATETIME;
                    }
                    else
                    {
                        this.state=STATES.IDLE;
                    }
                    break;
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
            AMS.error=e.toString();
        }
    }
    public String getCabinetDatetime()
    {
        SyncPacket tx =new SyncPacket(SyncPacket.BB_PACKET, 0, new byte[]{0x11});
        byte[] resp=exchange(SyncPacket.encode(tx), Config.respTimeout);
        if(resp!=null)
        {
            SyncPacket rx=SyncPacket.decode(resp);
            if(rx!=null)
            {
                long epoch =Helper.getUint32_BE(rx.data,0);
                Date date=new Date(epoch*1000);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // Format the current date and time using the defined formatter
                String formattedDateTime = simpleDateFormat.format(date);

                if((Config.logConfig&111)>0){

                    Config.logger.info("datetime :"+formattedDateTime);
                }
                return formattedDateTime;
            }
            else
            {
                Config.logger.severe("Invalid Response for get Datetime command");
            }
        }
        else
        {
            Config.logger.severe("No Response from cabinet for get Datetime command");
        }
        return null;
    }
    public boolean setServerDatetime(String datetime)
    {
        String serverDt=Config.dateFormat.format(new Date());

        Config.logger.info("server datetime : "+serverDt);
        Config.logger.info("changing datetime to : "+datetime);

        String[] cmd=new String[]{"date","-s",datetime};
        Global.executeShellCommand(cmd);

        datetime=datetime.substring(0, 16);

        serverDt=Config.dateFormat.format(new Date());
        Config.logger.info("server datetime after change: "+serverDt);

        serverDt=serverDt.substring(0,16);
        Config.logger.info("comparing datetime after change : "+datetime+"=="+serverDt);

        if(datetime.equals(serverDt))
        {
            return true;
        }
        else
        {
            Config.logger.severe("failed to change datetime");
            return false;
        }
    }
    public static String setServerNetwork(String ip,String gateway,String mask)
    {
        String result;
        String[] cmd=new String[]{"ifconfig",Config.LANinterface,ip};
        result=Global.executeShellCommand(cmd);
        Config.logger.finest("ip set "+result);
        cmd=new String[]{"ifconfig",Config.LANinterface,"netmask",mask};
        result=Global.executeShellCommand(cmd);
        Config.logger.finest("mask set "+result);
        cmd=new String[]{"route","add","default","gw",gateway};
        result=Global.executeShellCommand(cmd);
        Config.logger.finest("gateway set "+result);
        return result;
    }
    public void pause(int rxTimeout)
    {
        Config.logger.info("Cammand Recvd");
        this.cmdRecvd=true;
        int timeout=rxTimeout/10;
        while(!this.state.equals(STATES.IDLE)){
            try{
                Thread.sleep(10);
                if(timeout>0)
                    timeout--;
                if(timeout==0)
                    break;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        if(timeout==0)
            Config.logger.info("Failed to pause Sync");
        else
            Config.logger.info("Sync Paused");
    }
    public void resume()
    {
        this.cmdRecvd=false;
        Config.logger.info("Sync Resumed");
    }
    public byte[] exchange(byte[] tx,int rxTimeout)
    {
        Config.logger.info("Sending Cammand");
        if(this.open())
        {
            try
            {
                this.serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
                charTimeout=0;
                this.cmdRespRecvd=false;
                if((Config.logConfig&2)>0)
                {
                    Config.logger.info("tx :"+Helper.byteArrayToHexString(tx));
                }

                this.serialPort.writeBytes(tx);
                boolean exit=false,respRecvd=false;
                do
                {
                    try{
                        byte[] rx=this.serialPort.readBytes(1,rxTimeout);
                        if(rx!=null)
                        {
                            respRecvd=this.rxHandler(rx);
                            rxTimeout=100;
                            if(respRecvd)
                                exit=true;
                        }
                        else
                        {
                            exit=true;
                        }
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                        this.serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
                        exit=true;
                    }

                }while(!exit);
                if((Config.logConfig&111)>0)
                {
                    Config.logger.info("Cammand Tx : "+tx.length);
                    Config.logger.info(Helper.byteArrayToHexString(tx));
                }

                if(this.cmdRespRecvd)
                {
                    byte[] respPacket=new byte[this.reqLen];
                    System.arraycopy(this.rxBuff, 0, respPacket, 0, this.reqLen);
                    if((Config.logConfig&111)>0)
                    {
                        Config.logger.info("####  res :"+Helper.byteArrayToHexString(respPacket));
                        Config.logger.info("### seq :"+Helper.getUint16_LE(respPacket,1));
                        Config.logger.info("### data len :"+Helper.getUint16_LE(respPacket, 3));
                        Config.logger.info("Cammand Rx : "+respPacket.length);
                    }

//                    Config.logger.info(Helper.byteArrayToHexString(respPacket));
                    return respPacket;
                }
                else
                {
                    Config.logger.info("Cammand Resp Timeout");
                }
            }
            catch(Exception e)
            {
                Config.logger.info("Cammand Resp Error : "+e.toString());
            }
            finally
            {
                this.close();
            }
        }
        else
        {
            Config.logger.info("Failed to open port for sending Cammand");
        }
        return null;
    }
    @Override
    public void serialEvent(SerialPortEvent spe) {
        if(spe.isRXCHAR() && spe.getEventValue() >0)
        {
            try
            {
                byte[] buff = this.serialPort.readBytes(spe.getEventValue());
                if(buff!=null)
                {
                    this.rxHandler(buff);
                }
            }
            catch (SerialPortException ex)
            {
                ex.printStackTrace();
            }
        }

    }

    private boolean rxHandler(byte[] rx)
    {

        if((Config.logConfig&2)>0)
        {
            Config.logger.info("%%%%%%%%%%%%% rx :"+Helper.byteArrayToHexString(rx));
        }


        boolean retVal=false;
        if(charTimeout==0)
        {
            rxState = RX_STATES.RX_START_BYTE;
            rxInPtr=0;
        }
        charTimeout=5;
        for(int i=0;i<rx.length;i++)
        {
            if(rxInPtr<rxBuff.length)
            {
                rxBuff[rxInPtr]=rx[i];
                rxInPtr++;
                if(this.prevRxState!=this.rxState)
                {
                    Config.logger.finer("RX State : "+this.prevRxState+"-->"+this.rxState);
                    this.prevRxState=this.rxState;
                }
                switch(rxState)
                {
                    case RX_START_BYTE:
                        if(rx[i]==SyncPacket.SMT_PACKET || rx[i]==SyncPacket.BB_PACKET)
                        {

                            //storing start byte at 0th position
                            rxInPtr=0;
                            rxBuff[rxInPtr]=rx[i];
                            rxInPtr++;

                            dataPtr=0;
                            dataLen=2;
                            rxState = RX_STATES.RX_SEQ_NO;
                            Config.logger.info("!!!!!!!!!!!!!!!!!!!!!! :"+rx[i]);
                        }
                        break;
                    case RX_SEQ_NO:
                        dataPtr++;
                        if(dataPtr>=dataLen)
                        {
                            dataPtr=0;
                            dataLen=2;
                            rxState = RX_STATES.RX_SIZE;
                                    /*if(Helper.getUint16_BE(rxBuff, 1)>0)
                                    {
                                        if(Helper.getUint16_BE(rxBuff, 1)==SyncPacket.preSeqNo || Helper.getUint16_BE(rxBuff, 1)==(SyncPacket.preSeqNo+1) )
                                        {
                                            dataPtr=0;
                                            dataLen=2;
                                            rxState = RX_STATES.RX_SIZE;
                                            pktError=0;
                                        }else
                                        {
                                            if(pktError>=5)
                                            {
                                                SyncPacket.preSeqNo=Helper.getUint16_BE(rxBuff, 1);
                                            }
                                            pktError++;
                                            rxState = RX_STATES.RX_START_BYTE;
                                            rxInPtr=0;
                                        }
                                    }else
                                    {
                                        SyncPacket.preSeqNo=Helper.getUint16_BE(rxBuff, 1);
                                        dataPtr=0;
                                        dataLen=2;
                                        rxState = RX_STATES.RX_SIZE;
                                        pktError=0;
                                    }*/

                        }
                        break;
                    case RX_SIZE:
                        dataPtr++;
                        if(dataPtr>=dataLen)
                        {
                            dataPtr=0;
                            dataLen=Helper.getUint16_LE(rxBuff, 3);
                            if(dataLen>1000)//660
                            {

                                if((Config.logConfig&2)>0)
                                {
                                    Config.logger.info("data max limit reached (1000) :" + dataLen);
                                    Config.logger.info("###########11 rx :"+Helper.byteArrayToHexString(rx));
                                    Config.logger.info("###########222 rxBuff :"+Helper.byteArrayToHexString(rxBuff));
                                }

                                rxState = RX_STATES.RX_START_BYTE;
                                rxInPtr=0;

                            }else {
                                if((Config.logConfig&2)>0)
                                {
                                    Config.logger.info("######### datalen :" + dataLen);
                                }

                                rxState = RX_STATES.RX__DATA;
                            }
                        }
                        break;
                    case RX__DATA:
                        dataPtr++;
                        if(dataPtr>=dataLen)
                        {
                            dataPtr=0;
                            dataLen=2;
                            rxState = RX_STATES.RX_CRC;
                        }
                        break;
                    case RX_CRC:
                        dataPtr++;
                        if(dataPtr>=dataLen)
                        {
                            if(this.rxBuff[0]==SyncPacket.BB_PACKET)
                            {
                                this.cmdRespRecvd=true;
                            }
                            else
                            {
                                this.reqRecvd=true;
                            }
                            this.reqLen=rxInPtr;
                            rxInPtr=0;
                            rxState = RX_STATES.RX_START_BYTE;
                            retVal=true;
                        }
                        break;
                    default:
                        rxInPtr=0;
                        rxState = RX_STATES.RX_START_BYTE;
                        break;
                }
            }
            else
            {
                try {
                    this.serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
                Config.logger.warning("rx overflow @"+rxInPtr);
                charTimeout=0;
                this.reqLen=0;
                rxInPtr=0;
                return true;
            }
        }
        return retVal;
    }
    public boolean writeBytes(byte[] tx){
        try {
            return this.serialPort.writeBytes(tx);
        } catch (SerialPortException e) {
        }
        return false;
    }

}
