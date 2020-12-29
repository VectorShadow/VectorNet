package vsdl.cipher;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class ByteCipherTest {

    @Test
    public void testByteCipherEncryptDecrypt() {
        byte[] testData = new byte[1_024];
        ByteCipher testCipher = ByteCipher.generate();
        Random random = new Random();
        for (int i = 0; i < 256; ++i) {
            random.nextBytes(testData);
            assert Arrays.equals(testData, testCipher.decrypt(testCipher.encrypt(testData)));
        }
    }
}
