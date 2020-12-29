package vsdl.core;

import static vsdl.core.ByteUtil.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class ByteUtilTest {

    private static final int VOLUME = 16;
    private static final Random RANDOM = new Random();

    @Test
    public void testTo2() {
        int testInt;
        byte[] testBytes;
        for (int i = 0; i < VOLUME; ++i) {
            testInt = RANDOM.nextInt(0xffff);
            testBytes = to2(testInt);
            assert testInt == toInt(testBytes, 0, 2);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTo2OutOfBounds() {
        to2(0x0001_0000);
    }

    @Test
    public void testTo3() {
        int testInt;
        byte[] testBytes;
        for (int i = 0; i < VOLUME; ++i) {
            testInt = RANDOM.nextInt(0x00ff_ffff);
            testBytes = to3(testInt);
            assert testInt == toInt(testBytes, 0, 3);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTo3OutOfBounds() {
        to3(0x0100_0000);
    }

    @Test
    public void testTo4() {
        int testInt;
        byte[] testBytes;
        for (int i = 0; i < VOLUME; ++i) {
            testInt = RANDOM.nextInt(0x7fff_ffff);
            testBytes = to4(testInt);
            assert testInt == toInt(testBytes, 0, 4);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTo4OutOfBounds() {
        to4(-1);
    }

    @Test
    public void testToInt() {
        int testIndex;
        int testInt;
        byte[] testBytes = new byte[128];
        RANDOM.nextBytes(testBytes);
        for (int i = 0; i < VOLUME; ++i) {
            testIndex = RANDOM.nextInt(128);
            testInt = toInt(testBytes, testIndex, 2);
            assert Arrays.equals(to2(testInt), Arrays.copyOfRange(testBytes, testIndex, testIndex + 2));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToIntOffsetError() {
        byte[] testBytes = new byte[16];
        RANDOM.nextBytes(testBytes);
        toInt(testBytes, 16, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToIntNegativeSizeError() {
        byte[] testBytes = new byte[16];
        RANDOM.nextBytes(testBytes);
        toInt(testBytes, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToIntOffsetPlusSizeError() {
        byte[] testBytes = new byte[16];
        RANDOM.nextBytes(testBytes);
        toInt(testBytes, 12, 6);
    }
}
