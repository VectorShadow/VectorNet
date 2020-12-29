package vsdl.core;

import static vsdl.core.VNConstants.*;

import org.junit.Test;

import java.util.Random;

public class VectorPacketTest {

    private static final int VOLUME = 32;
    private static final Random RANDOM = new Random();

    @Test
    public void testToAndFromStream() {
        VectorPacket testPacket;
        byte[] testData = new byte[MAX_PACKET_DATA];
        for (int i = 0; i < VOLUME; ++i) {
            RANDOM.nextBytes(testData);
            testPacket = new VectorPacket(
                    SessionHost.randomInitialLinkID(),
                    RANDOM.nextInt(Integer.MAX_VALUE),
                    MAX_PACKET_DATA,
                    (short)RANDOM.nextInt(MAX_TRANSMISSION_PACKETS),
                    testData
            );
            assert testPacket.equals(VectorPacket.fromStream(VectorPacket.toStream(testPacket)));
        }
    }
}
