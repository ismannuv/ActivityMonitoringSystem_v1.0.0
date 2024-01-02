package org.senergy.ams.hw;

import java.math.BigInteger;

public class KeySlot {
    private BigInteger configuredTagUid;
    private BigInteger presentTagUid;
    public KeySlot() {
        this.presentTagUid = BigInteger.ZERO;
        this.configuredTagUid = BigInteger.ZERO;
    }
    public BigInteger getConfiguredTagUid() {
        return configuredTagUid;
    }

    public void setConfiguredTagUid(BigInteger configuredTagUid) {
        this.configuredTagUid = configuredTagUid;
    }

    public BigInteger getPresentTagUid() {
        return presentTagUid;
    }

    public void setPresentTagUid(BigInteger presentTagUid) {
        this.presentTagUid = presentTagUid;
    }
    public boolean isConfigured(){
        return this.configuredTagUid!=null || this.presentTagUid.equals(BigInteger.ZERO);
    }

    public boolean correctKeyPresentAtThisSlot()//
    {
        return this.configuredTagUid.equals(this.presentTagUid);
    }
    public boolean isEmpty(){
        return this.presentTagUid==null || this.presentTagUid.equals(BigInteger.ZERO);
    }

}
