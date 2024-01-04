package org.senergy.ams.hw;

import SIPLlib.Helper;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.Config;
import org.senergy.ams.sync.SyncCommands;
import org.senergy.ams.sync.SyncPacket;

import java.util.Date;

public class CommandSyncService implements Runnable{

    private static enum STATES{UNKNOWN,IDLE,PREPARE_HEALTH_PKT,SEND_HEALTH_PKT,SEND_COMMAND};
    private STATES state= STATES.UNKNOWN;
    private STATES prevState= STATES.UNKNOWN;
    private STATES nextState= STATES.PREPARE_HEALTH_PKT;
    private int sleepTime=100;
    private int nextStateTimeout=600;
    private int sixty_sec=600;
    private int ten_sec=100;
    private int zero_sec=0;
    private byte[] rxData=new byte[1024];
    private int rxLen=0;
    @Override
    public void run() {
        while (true) {
            try {

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
                if(AMS.serialComm.isOpened()){
                    if(this.nextStateTimeout<=0){
                        this.state=this.nextState;
                    }else {
                        this.nextStateTimeout--;
                    }
                }

            }
            break;
            case PREPARE_HEALTH_PKT:
            {
                Helper.setUint8(this.rxData,this.rxLen, (short) SyncCommands.SEND_HEALTH_PKT);
                this.rxLen++;
                Helper.setUint32_BE(this.rxData,this.rxLen,new Date().getTime());
                this.rxLen+=4;

                this.nextStateTimeout=zero_sec;
                this.state=STATES.SEND_HEALTH_PKT;
            }
            break;
            case SEND_HEALTH_PKT:
            {
                byte[] reqPacket=new byte[this.rxLen];
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
                this.state=STATES.IDLE;

            }
            break;
            case SEND_COMMAND:
            {

            }
            break;
            default:
                this.nextState=STATES.PREPARE_HEALTH_PKT;
                this.state=STATES.IDLE;
                break;
        }
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
        setNextStateTimeout(600);
        this.state=STATES.IDLE;
        this.nextState=STATES.PREPARE_HEALTH_PKT;
    }
}
