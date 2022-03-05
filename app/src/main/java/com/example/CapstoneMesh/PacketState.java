package com.example.CapstoneMesh;

public class PacketState {
    byte verb;

    public PacketState() {}
    public PacketState(byte verb) {
        this.verb = verb;
    }
    public byte getVerb() {
        return verb;
    }
}