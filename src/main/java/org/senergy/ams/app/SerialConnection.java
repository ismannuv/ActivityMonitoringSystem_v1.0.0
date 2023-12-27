package org.senergy.ams.app;

import SIPLlib.SerialCommunication;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialConnection extends SerialCommunication implements SerialPortEventListener {
    public SerialConnection(String portName,int baud)
    {
        super(portName,baud,8,1,0);
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

    private void rxHandler(byte[] rx) {

    }
}
