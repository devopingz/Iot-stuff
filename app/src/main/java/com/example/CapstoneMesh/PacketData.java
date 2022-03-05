package com.example.CapstoneMesh;



public class PacketData extends PacketState {

    short paired_device_id;
    short num_dev;
    DevProfile[] dev;

    public PacketData(byte verb, short paired_device_id, short num_dev, DevProfile[] dev) {
        this.verb = verb;
        this.paired_device_id = paired_device_id;
        this.num_dev = num_dev;
        this.dev = dev;
    }

    public byte getVerb() {
        return verb;
    }
    public DevProfile[] getDev() {
        return dev;
    }
    public short getNum_dev() {
        return num_dev;
    }
    public short getPaired_device_id() {
        return paired_device_id;
    }
}
