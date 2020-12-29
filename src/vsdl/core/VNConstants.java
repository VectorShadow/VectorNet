package vsdl.core;

public class VNConstants {
    static final int MAX_TRANSMISSION_PACKETS = 127;
    static final int MAX_PACKET_DATA = 8192;


    public static final short OPCODE_LINK = 0;
    public static final short OPCODE_LINK_HANDSHAKE_PUBLIC = 0;
    public static final short OPCODE_LINK_HANDSHAKE_PRIVATE = 1;
    public static final short OPCODE_LINK_HANDSHAKE_ACK = 2;
    public static final short OPCODE_LINK_ERROR = 3;

    public static final short OPCODE_USER = 64;

    //sender = 4 + xmit = 4 + seq = 1 + size = 3 => 12
    static final int PACKET_HEADER_LENGTH = 12;
    static final int PACKET_TRAILER_LENGTH = 3; //trailer = 3

    static final int PACKET_SIZE = PACKET_HEADER_LENGTH + MAX_PACKET_DATA + PACKET_TRAILER_LENGTH;
}
