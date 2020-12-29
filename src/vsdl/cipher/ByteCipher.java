package vsdl.cipher;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

public class ByteCipher implements Serializable {

    private static final int SHIFT = 8;
    private static final int KEY_SIZE = 256;

    private static final int BYTE_MASK = 0x0000_00ff;

    private final byte[] KEY;

    private ByteCipher(byte[] key) {
        KEY = key;
    }

    /**
     * Encrypt a byte array for secure transmission.
     */
    public byte[] encrypt(byte[] data){
        return bytewiseShiftLeft(xorApplyKey(data, KEY));
    }

    /**
     * Decrypt an encrypted byte array.
     */
    public byte[] decrypt(byte[] encryptedData) {
        return xorApplyKey(bytewiseShiftRight(encryptedData), KEY);
    }

    /**
     * Reconstruct a ByteCipher from a BigInteger corresponding to an RSA encrypted ByteCipher.
     */
    public static ByteCipher decryptRSA(BigInteger bigInteger) {
        return new ByteCipher(HexCipher.convertFromHexString(RSA.decrypt(bigInteger).toString(16)));
    }

    /**
     * Encrypt the BigInteger representation of the provided ByteCipher's KEY.
     */
    public static BigInteger encryptRSA(ByteCipher byteCipher, BigInteger publicKey) {
        return RSA.encrypt(new BigInteger(HexCipher.convertToHexString(byteCipher.KEY), 16), publicKey);
    }

    /**
     * Generate a new ByteCipher with a random key.
     */
    public static ByteCipher generate() {
        byte[] key = new byte[KEY_SIZE];
        for (int i = 0; i < KEY_SIZE; ++i) {
            key[i] = randomByte();
        }
        return new ByteCipher(key);
    }

    /**
     * Encryption step - rotate bytes leftwards.
     */
    private static byte[] bytewiseShiftLeft(byte[] inputData) {
        byte[] outputData = new byte[inputData.length];
        int shiftDegree;
        for (int i = 0; i < inputData.length; ++i) {
            shiftDegree = (i % SHIFT);
            outputData[i] = shift(inputData[i], shiftDegree, true);
        }
        return outputData;
    }

    /**
     * Decryption step - rotate bytes rightwards (reverse bytewiseShiftLeft).
     */
    private static byte[] bytewiseShiftRight(byte[] inputData) {
        byte[] outputData = new byte[inputData.length];
        int shiftDegree;
        for (int i = 0; i < inputData.length; ++i) {
            shiftDegree = (i % SHIFT);
            outputData[i] = shift(inputData[i], shiftDegree, false);
        }
        return outputData;
    }

    private static byte randomByte() {
        int b = 0;
        final SecureRandom SECURE_RANDOM = new SecureRandom();
        for (int i = 7; i >= 0; --i) {
            b |= (int)(Math.pow(2, i) * SECURE_RANDOM.nextInt(2));
        }
        return (byte)b;
    }

    /**
     * Circular shift a byte by the specified magnitude, either left or right.
     */
    private static byte shift(byte b, int mag, boolean left) {
        if (mag < 0 || mag >= SHIFT) throw new IllegalArgumentException("Mag must be in range [0-7].");
        if (mag == 0) return b;
        byte shiftMask = 0b0000_0000;
        int factor = 1;
        int oppositeMag = SHIFT - mag;
        for (int i = 0; i < (left ? oppositeMag : mag); ++i) {
            shiftMask += factor;
            factor *= 2;
        }
        int oppositeMask = ~shiftMask & BYTE_MASK;
        int leftShiftPart = b & shiftMask;
        int rightShiftPart = b & oppositeMask;
        return (byte)((leftShiftPart << (left ? mag : oppositeMag)) | (rightShiftPart >>> (left ? oppositeMag : mag)));
    }

    /**
     * Encryption/Decryption step - xor the source text with the provided key.
     * This encrypts plain text or decrypts cipher text.
     */
    private static byte[] xorApplyKey(byte[] inputData, byte[] key) {
        int dataSize = inputData.length;
        byte[] outputData = new byte[dataSize];
        for (int i = 0; i < dataSize; ++i)
            outputData[i] = (byte) (inputData[i] ^ key[i % KEY_SIZE]);
        return outputData;
    }
}
