package org.senergy.ams.hw;

import SIPLlib.Helper;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.Config;
import org.senergy.ams.server.HttpHandlers.AmsServer;
import org.senergy.ams.sync.SyncCommands;
import org.senergy.ams.sync.SyncPacket;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.*;

public class CommandSyncService implements Runnable{
    private BBCommand BBCommand;
    private boolean datetimeConfigured;



    private boolean gotCurrentKeyStatus;


    private static enum STATES{UNKNOWN,CONFIGURE_DATETIME,IDLE,PREPARE_HEALTH_PKT,CHECK_FOR_FP_SYNC_PKT,SEND_COMMAND,WAIT_FOR_CMD_RESP_TO_PROCESSED};
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
    private int fpStateCnt=5;
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
            Config.logger.info("Command sync nextStateTimeout : "+this.nextStateTimeout);
            this.prevState=this.state;
        }
        if(this.state == STATES.CHECK_FOR_FP_SYNC_PKT && this.fpStateCnt<=0){//change FP sync state after 5 iteration to health pkt state
            resetStateToIdleHealthPkt();
        }
        switch (this.state){
            case CONFIGURE_DATETIME:
            {
                if(!this.datetimeConfigured){
                    setDateTimeCmd();

                    this.nextStateTimeout=sixty_sec;//wait for 60 sec and send again datetime cmd
                    this.nextState=STATES.CONFIGURE_DATETIME;
                    this.state=STATES.SEND_COMMAND;

                }else if (!this.gotCurrentKeyStatus){//send get all current
                    setGetAllKeyStatusCmd();

                    this.nextStateTimeout=sixty_sec;
                    this.nextState=STATES.CONFIGURE_DATETIME;
                    this.state=STATES.SEND_COMMAND;
                }else {
                    resetToIdleState(sixty_sec);
                }

            }
            break;
            case IDLE:
            {
                if(this.nextState==STATES.IDLE){
                    this.nextState=STATES.PREPARE_HEALTH_PKT;
                }
                if(!AMS.serialComm.isOpened())
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
                byte[] reqPacket=new byte[this.rxLen];
                System.arraycopy(this.rxData, 0, reqPacket, 0, this.rxLen);
                //setting cmd for STM

                this.setBBCommand(reqPacket,ten_sec);

                this.nextStateTimeout=zero_sec;
                this.nextState=STATES.CHECK_FOR_FP_SYNC_PKT;
                this.state=STATES.SEND_COMMAND;
            }
            break;
            case CHECK_FOR_FP_SYNC_PKT:
            {
                fpStateCnt--;
                byte[] fpRxPkt = getFpSyncPkt();
                if(fpRxPkt !=null)//fp cmd present to sync
                {
                    this.setBBCommand(fpRxPkt,ten_sec);
                    this.state=STATES.SEND_COMMAND;
                    this.nextStateTimeout=sixty_sec;
                    this.nextState=STATES.PREPARE_HEALTH_PKT;// alternate heath pkt then FP then again Health pkt

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
                        Config.logger.info("sending cmd pkt");
                        if(AMS.serialComm.writeBytesSynchronously(this.BBCommand.data)){

                            this.state=STATES.WAIT_FOR_CMD_RESP_TO_PROCESSED;
                            Config.logger.info("cmd pkt sent");
                            break;
                        }else {
                            Config.logger.info("failed to send cmd pkt");
                        }
                    }else{
                        Config.logger.info("cabinet busy");
                    }
                }
                resetToIdleState(sixty_sec);

            }
            break;
            case WAIT_FOR_CMD_RESP_TO_PROCESSED:
            {
                if(AMS.serialComm.checkCmdRespStatus()){
                    resetToIdleState(zero_sec);

                }else {
                    if (this.BBCommand.timeout > 0) {
                        this.BBCommand.timeout--;
                    } else {
//                        resetStateToIdleHealthPkt();
                        resetToIdleState(sixty_sec);
                    }
                }

            }
            break;
            default:
                this.nextStateTimeout=sixty_sec;
                this.nextState=STATES.CONFIGURE_DATETIME;
                this.state=STATES.CONFIGURE_DATETIME;
                break;
        }
    }

    private void setGetAllKeyStatusCmd() {
        this.rxLen=0;
        Helper.setUint8(this.rxData,this.rxLen, (short) SyncCommands.GET_ALL_CURRENT_KEY_STATUS);
        byte[] reqPacket=new byte[this.rxLen];
        System.arraycopy(this.rxData, 0, reqPacket, 0, this.rxLen);
        this.setBBCommand(reqPacket,ten_sec);
    }

    private void setDateTimeCmd() {
        this.rxLen=0;
        Helper.setUint8(this.rxData,this.rxLen, (short) SyncCommands.GET_DATETIME);
        byte[] reqPacket=new byte[this.rxLen];
        System.arraycopy(this.rxData, 0, reqPacket, 0, this.rxLen);
        this.setBBCommand(reqPacket,ten_sec);
    }

    private byte[] getFpSyncPkt() {
        return null;
    }

    public int getSleepTime() {
        return sleepTime;
    }
    private void resetStateToIdleHealthPkt(){
        this.fpStateCnt=5;
        System.out.println("####### resetStateToIdleHealthPkt");
        this.resetBBCommand();
        this.nextStateTimeout=sixty_sec;
        this.nextState=STATES.PREPARE_HEALTH_PKT;
        this.state=STATES.IDLE;
    }
    private void resetToIdleState(int timeout){
        this.fpStateCnt=5;
        System.out.println("####### resetToIdleState");
        this.resetBBCommand();
        this.nextStateTimeout=timeout;
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
    public void setGotCurrentKeyStatus(boolean gotCurrentKeyStatus) {
        this.gotCurrentKeyStatus = gotCurrentKeyStatus;
    }
    public void putThreadToIdleState(){
        this.resetStateToIdleHealthPkt();
    }
    public void setBBCommand(byte[] data,int timeout){
        AMS.serialComm.resetCmdRespProcessed();
        SyncPacket tx =new SyncPacket(SyncPacket.BB_PACKET, 0, data);
        this.BBCommand=new BBCommand(SyncPacket.encode(tx),timeout);
    }

    private class BBCommand {
        byte[] data;
        int timeout;

        @Override
        public String toString() {
            return "BBCommand{" +
                    "data=" + Arrays.toString(data) +
                    ", timeout=" + timeout +
                    '}';
        }

        public BBCommand(byte[] data, int timeout) {
            this.data = data;
            this.timeout = timeout;//
            System.out.println(this);
        }
    }
    public boolean isBBbusy() {
        return this.BBCommand !=null;
    }
    public int exchangeWebCommand(byte[] tx ,int timeout){
        int status=0;//success
        try {
            status=executeWithTimeout(() -> {
                this.setBBCommand(tx,timeout/this.getSleepTime());


                while (AmsServer.respObjectNode.isEmpty()){
                    //wait till response

                }
                return 1;// success
            }, timeout);
        } catch (ExecutionException e) {
            status=2;//timeout
        } catch (InterruptedException e) {
            status=3;
        } catch (TimeoutException e) {
            status=4;
        }
//        this.commandSync.resetBBCommand();
        return status;
        /*try {
            this.commandSync.setBBCommand(tx,timeout);

            int sleeptime=1;
            if (timeout>10000)
                timeout=10000;
            while (AmsServer.respObjectNode.isEmpty() && timeout>0){

                Thread.sleep(sleeptime);
//                System.out.println(timeout);
                timeout=timeout-sleeptime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }
    public <T> T executeWithTimeout(Callable<T> task, long timeoutMillis) throws ExecutionException, InterruptedException, TimeoutException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(task);

        try {
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            executor.shutdownNow(); // Shut down the executor service
        }
    }
}
