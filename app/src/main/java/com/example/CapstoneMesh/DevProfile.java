package com.example.CapstoneMesh;

public  class DevProfile {
    byte[] mac;
    short id;
    int state;
    int policy;

    public  DevProfile(byte[] mac, short id, int state, int policy) {
        this.mac = mac;
        this.id = id;
        this.state = state;
        this.policy = policy;
    }
    public byte[] getMac() {
        return mac;
    }
    public int getPolicy() {
        return policy;
    }
    public int getState() {
        return state;
    }
    public short getId() {
        return id;
    }

    public void setMac (byte[] mac ){this.mac = mac; }
    public void setId (short id) { this.id = id; }
    public void setState (int state) { this.state = state; }
    public void setPolicy (int policy) {this.policy = policy; }
}
