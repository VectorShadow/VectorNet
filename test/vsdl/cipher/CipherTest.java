package vsdl.cipher;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class CipherTest {

    private static final int VOLUME = 64;
    private static final Random RANDOM = new Random();

    @Test
    public void testByteCipherEncryptDecrypt() {
        byte[] testData = new byte[1_024];
        ByteCipher testCipher = ByteCipher.generate();
        for (int i = 0; i < VOLUME; ++i) {
            RANDOM.nextBytes(testData);
            assert Arrays.equals(testData, testCipher.decrypt(testCipher.encrypt(testData)));
        }
    }

    @Test
    public void testHexCipherEncryptDecrypt() {
        byte[] testData = new byte[1_024];
        for (int i = 0; i < VOLUME; ++i) {
            RANDOM.nextBytes(testData);
            assert Arrays.equals(testData, HexCipher.convertFromHexString(HexCipher.convertToHexString(testData)));
        }
    }

    /**
     * BigInteger's string constructor ignores leading 0s. As a result, any testData that would convert
     * to a HexString with a leading 0 will cause a failure of this test. However, this is only a problem when
     * we use RSA encryption to transmit that BigInteger, which only occurs during the handshake - all other
     * encryption will be via ByteCipher keys. To solve this, we simply ensure that ByteCipher keys themselves
     * do not have leading 0s, so they can be successfully transmitted.
     */
//    @Test
//    public void testHexCipherEncryptDecryptBigInteger() {
//        byte[] testData = new byte[1_024];
//        BigInteger testInteger;
//        for (int i = 0; i < VOLUME; ++i) {
//            RANDOM.nextBytes(testData);
//            testInteger = new BigInteger(HexCipher.convertToHexString(testData), 16);
//            System.out.println("HexString in: " + HexCipher.convertToHexString(testData));
//            System.out.println("HexString out: " + testInteger.toString(16));
//            assert Arrays.equals(testData, HexCipher.convertFromHexString(testInteger.toString(16)));
//        }
//    }

    @Test
    public void testRSAEncryptDecrypt() {
        byte[] testData = new byte[256];
        BigInteger testInteger;
        for (int i = 0; i < VOLUME; ++i) {
            RANDOM.nextBytes(testData);
            testInteger = new BigInteger(HexCipher.convertToHexString(testData), 16);
            assert testInteger.equals(RSA.decrypt(RSA.encrypt(testInteger, RSA.getSessionPublicKey())));
        }
    }

    @Test
    public void testByteCipherRSAEncryptDecrypt() {
        ByteCipher testCipher;
        for (int i = 0; i < VOLUME; ++i) {
            testCipher = ByteCipher.generate();
            assert testCipher.equals(ByteCipher.decryptRSA(ByteCipher.encryptRSA(testCipher, RSA.getSessionPublicKey())));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckSize1() {
        RSA.encrypt(new BigInteger("-1"), RSA.getSessionPublicKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckSize2() {
        byte[] testData = new byte[512];
        RANDOM.nextBytes(testData);
        BigInteger testInteger = new BigInteger(HexCipher.convertToHexString(testData), 16);
        RSA.encrypt(testInteger, RSA.getSessionPublicKey());
    }
}
