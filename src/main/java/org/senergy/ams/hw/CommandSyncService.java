package org.senergy.ams.hw;

import SIPLlib.Helper;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.Config;
import org.senergy.ams.sync.SyncCommands;
import org.senergy.ams.sync.SyncPacket;

import java.util.Date;

public class CommandSyncService implements Runnable{
    private BBCommand BBCommand;



    private static enum STATES{UNKNOWN,IDLE,PREPARE_HEALTH_PKT,CHECK_FOR_FP_SYNC_PKT,SEND_COMMAND,WAIT_FOR_CMD_RESP_TO_PROCESSED};
    private STATES state= STATES.UNKNOWN;
    private STATES prevState= STATES.UNKNOWN;
    private STATES nextState= STATES.PREPARE_HEALTH_PKT;
    private int sleepTime=10;// 10 to 1000
    private int nextStateTimeout=600;
    private int sixty_sec=60000/sleepTime;
    private int ten_sec=10000/sleepTime;
    private int zero_sec= 0;
    private byte[] rxData=new byte[1024];
    private int rxLen=0;
    @Override
    public void run() {
        while (true) {
            try {
//                System.out.println("sixty_sec :"+sixty_sec);
//                System.out.println("ten_sec :"+ten_sec);
                stateMachine();
                Thread.sleep(sleepTime);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void stateMachine(){
        if(this.prevState!=this.state)
        {
            Config.logger.info("Command sync service State : "+this.prevState+"-->"+this.state +"-->"+this.nextState);
            this.prevState=this.state;
        }
        switch (this.state){
            case IDLE:
            {
                if(this.nextState==STATES.IDLE){
                    this.nextState=STATES.PREPARE_HEALTH_PKT;
                }
                if(AMS.serialComm.isOpened())
                {
                    if(this.BBCommand!=null){// if web cmd

                        this.nextStateTimeout=sixty_sec;
                        this.nextState=STATES.IDLE;
                        this.state=STATES.SEND_COMMAND;
                    }else {
                        if(this.nextStateTimeout<=0){
                            this.state=this.nextState;
                        }else {
                            this.nextStateTimeout--;
                        }
                    }
                }


            }
            break;
            case PREPARE_HEALTH_PKT:
            {
                this.rxLen=0;
                Helper.setUint8(this.rxData,this.rxLen, (short) SyncCommands.SEND_HEALTH_PKT);
                this.rxLen++;
                Helper.setUint32_BE(this.rxData,this.rxLen,new Date().getTime());
                this.rxLen+=4;

                //setting cmd for STM
                byte[] reqPacket=new byte[this.rxLen];
                System.arraycopy(this.rxData, 0, reqPacket, 0, this.rxLen);
                this.setBBCommand(reqPacket,ten_sec);

                this.nextStateTimeout=zero_sec;
                this.nextState=STATES.CHECK_FOR_FP_SYNC_PKT;
                this.state=STATES.SEND_COMMAND;
            }
            break;
            case CHECK_FOR_FP_SYNC_PKT:
            {
                if(false)//fp cmd preset to sync
                {
                    this.state=STATES.SEND_COMMAND;
                    this.nextState=STATES.CHECK_FOR_FP_SYNC_PKT;

                }else{
                    resetStateToIdleHealthPkt();
                }
                /*byte[] reqPacket=new byte[this.rxLen];
                System.arraycopy(this.rxData, 0, reqPacket, 0, this.rxLen);
                SyncPacket tx =new SyncPacket(SyncPacket.BB_PACKET, 0, reqPacket);

                if (!AMS.serialComm.isCabinetBusy()){
                    Config.logger.info("sending health pkt");
                    if(AMS.serialComm.writeBytes(SyncPacket.encode(tx))){
                        Config.logger.info("health pkt sent");
                    }else {
                        Config.logger.info("failed to send health pkt");
                    }
                }else{

                }
                this.nextStateTimeout=sixty_sec;
                this.nextState=STATES.PREPARE_HEALTH_PKT;
                this.state=STATES.IDLE;*/

            }
            break;
            case SEND_COMMAND:
            {
                if (this.BBCommand!=null){
                    if (!AMS.serialComm.isCabinetBusy()){
                        Config.logger.info("sending health pkt");
                        if(AMS.serialComm.writeBytesSynchronously(this.BBCommand.data)){

                            this.state=STATES.WAIT_FOR_CMD_RESP_TO_PROCESSED;
                            Config.logger.info("health pkt sent");
                            break;
                        }else {
                            Config.logger.info("failed to send health pkt");
                        }
                    }else{
                        Config.logger.info("cabinet busy");
                    }
                }
                resetStateToIdleHealthPkt();
            }
            break;
            case WAIT_FOR_CMD_RESP_TO_PROCESSED:
            {
                if(AMS.serialComm.checkCmdRespStatus()){
                    resetBBCommand();
                    this.state=STATES.IDLE;

                }else {
                    if (this.BBCommand.timeout > 0) {
                        this.BBCommand.timeout--;
                    } else {
                        resetStateToIdleHealthPkt();
                    }
                }

            }
            break;
            default:
                this.nextStateTimeout=sixty_sec;
                this.nextState=STATES.PREPARE_HEALTH_PKT;
                this.state=STATES.IDLE;
                break;
        }
    }
    private void resetStateToIdleHealthPkt(){
        this.resetBBCommand();
        this.nextStateTimeout=sixty_sec;
        this.nextState=STATES.PREPARE_HEALTH_PKT;
        this.state=STATES.IDLE;
    }
    public void resetBBCommand() {
        this.BBCommand=null;
    }

    public int getNextStateTimeout() {
        return nextStateTimeout;
    }

    public void setNextStateTimeout(int nextStateTimeout) {
        this.nextStateTimeout = nextStateTimeout;
    }
    public void slowDownCommandSync(){
        setNextStateTimeout(600);

    }
    public void putThreadToIdleState(){
        this.resetStateToIdleHealthPkt();
    }
    public boolean checkBBisBusy(){
        return this.BBCommand !=null;
    }
    public void setBBCommand(byte[] data,int timeout){
        AMS.serialComm.resetCmdRespProcessed();
        SyncPacket tx =new SyncPacket(SyncPacket.BB_PACKET, 0, data);
        this.BBCommand=new BBCommand(SyncPacket.encode(tx),timeout);
    }

    private class BBCommand {
        byte[] data;
        int timeout;

        public BBCommand(byte[] data, int timeout) {
            this.data = data;
            this.timeout = timeout;
        }
    }
}
