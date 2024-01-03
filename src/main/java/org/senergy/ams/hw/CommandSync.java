package org.senergy.ams.hw;

public class CommandSync implements Runnable{
    private static enum STATES{UNKNOWN,IDLE,SEND_HEALTH_PKT,SEND_COMMAND};
    private STATES state= STATES.UNKNOWN;
    private STATES nextState= STATES.SEND_HEALTH_PKT;
    private int sleepTime=100;
    private int nextStateTimeout=600;

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

        switch (this.state){
            case IDLE:
            {
                if(this.nextStateTimeout<=0){
                    this.state=this.nextState;
                }else {
                    this.nextStateTimeout--;
                }
            }
                break;
            case SEND_HEALTH_PKT:
            {

            }
                break;
            case SEND_COMMAND:
            {

            }
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
}
