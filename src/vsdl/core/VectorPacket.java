package vsdl.core;

import java.util.Arrays;

import static vsdl.core.ByteUtil.*;
import static vsdl.core.VNConstants.*;

/**
 * The unit of data transmission in a VectorNet system.
 */
public class VectorPacket {

    private static final byte PAD = 0x00;

    private final int SENDER_ID;
    private final int XMIT_ID;
    private final int PACKET_SIZE;
    private final short SEQUENCE_ID;
    private final byte[] DATA;
    private final int CHECKSUM;

    VectorPacket(int senderID, int xmitID, int packetSize, short sequenceID, byte[] data) {
        SENDER_ID = senderID;
        XMIT_ID = xmitID;
        PACKET_SIZE = packetSize;
        SEQUENCE_ID = sequenceID;
        DATA = new byte[MAX_PACKET_DATA];
        int sum = 0;
        for (int i = 0; i < DATA.length; ++i) {
            DATA[i] = (i < data.length ? data[i] : PAD);
            sum += DATA[i];
        }
        CHECKSUM = Math.abs(sum);
    }

    public static byte[] toStream(VectorPacket packet) {
        byte[] stream = new byte[VNConstants.PACKET_SIZE];
        System.arraycopy(to4(packet.SENDER_ID), 0, stream, 0, 4);
        System.arraycopy(to4(packet.XMIT_ID), 0, stream, 4, 4);
        System.arraycopy(to3(packet.PACKET_SIZE), 0, stream, 8, 3);
        System.arraycopy(to2(packet.SEQUENCE_ID), 0, stream, 11, 2);
        for (int i = 0; i < MAX_PACKET_DATA; ++i) {
            stream[i+ PACKET_HEADER_LENGTH] = packet.DATA[i];
        }
        System.arraycopy(to3(packet.CHECKSUM), 0, stream, PACKET_HEADER_LENGTH + MAX_PACKET_DATA, 3);
        return stream;
    }

    public static VectorPacket fromStream(byte[] stream) {
        byte[] data = new byte[MAX_PACKET_DATA];
        System.arraycopy(stream, PACKET_HEADER_LENGTH, data, 0, MAX_PACKET_DATA);
        return new VectorPacket(
                toInt(stream, 0, 4),
                toInt(stream, 4, 4),
                toInt(stream, 8, 3),
                (short)toInt(stream, 11, 2),
                data
        );
    }

    public int getSenderID() {
        return SENDER_ID;
    }

    public int getXmitID() {
        return XMIT_ID;
    }

    public int getPacketSize() {
        return PACKET_SIZE;
    }

    public int getSequenceID() {
        return (int)SEQUENCE_ID;
    }

    public byte[] getData() {
        return DATA;
    }

    public int getChecksum() {
        return CHECKSUM;
    }

    @Override
    public boolean equals(Object o) {
        return
                o instanceof VectorPacket &&
                        SENDER_ID == ((VectorPacket) o).SENDER_ID &&
                        XMIT_ID == ((VectorPacket) o).XMIT_ID &&
                        PACKET_SIZE == ((VectorPacket) o).PACKET_SIZE &&
                        SEQUENCE_ID == ((VectorPacket) o).SEQUENCE_ID &&
                        Arrays.equals(DATA, ((VectorPacket) o).DATA) &&
                        CHECKSUM == ((VectorPacket) o).CHECKSUM;
    }
}
