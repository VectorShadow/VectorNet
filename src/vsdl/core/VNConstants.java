package vsdl.core;

public class VNConstants {
    static final int MAX_TRANSMISSION_PACKETS = 0x7fff; //32767
    static final int MAX_PACKET_DATA = 1_024;


    public static final short OPCODE_LINK = 0;
    public static final short OPCODE_LINK_HANDSHAKE_PUBLIC = 0;
    public static final short OPCODE_LINK_HANDSHAKE_PRIVATE = 1;
    public static final short OPCODE_LINK_HANDSHAKE_ACK = 2;
    public static final short OPCODE_LINK_ERROR = 3;

    public static final short OPCODE_USER = 64;

    //sender = 4 + xmit = 4 + seq = 2 + size = 3 => 13
    static final int PACKET_HEADER_LENGTH = 13;
    static final int PACKET_TRAILER_LENGTH = 3; //trailer = 3

    static final int PACKET_SIZE = PACKET_HEADER_LENGTH + MAX_PACKET_DATA + PACKET_TRAILER_LENGTH;
}
