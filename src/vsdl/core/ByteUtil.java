package vsdl.core;

public class ByteUtil {

    private static final int MASK0 = 0xff00_0000;
    private static final int MASK1 = 0x00ff_0000;
    private static final int MASK2 = 0x0000_ff00;
    private static final int MASK3 = 0x0000_00ff;

    public static byte[] to2(int i) {
        if (i < 0 || i > 0xffff)
            throw new IllegalArgumentException("Int " + i + " out of bounds(0 -> 65,535");
        byte[] out = new byte[2];
        out[0] = (byte)((i & MASK2) >> 8);
        out[1] = (byte)(i & MASK3);
        return out;
    }

    public static byte[] to3(int i) {
        if (i < 0 || i > 0xff_ffff)
            throw new IllegalArgumentException("Int " + i + " out of bounds(0 -> 16,777,215");
        byte[] out = new byte[3];
        out[0] = (byte)((i & MASK1) >> 16);
        out[1] = (byte)((i & MASK2) >> 8);
        out[2] = (byte)(i & MASK3);
        return out;
    }

    public static byte[] to4(int i) {
        if (i < 0)
            throw new IllegalArgumentException("Int " + i + " out of bounds(0 -> 2,147,483,647");
        byte[] out = new byte[4];
        out[0] = (byte)((i & MASK0) >> 24);
        out[1] = (byte)((i & MASK1) >> 16);
        out[2] = (byte)((i & MASK2) >> 8);
        out[3] = (byte)(i & MASK3);
        return out;
    }

    /**
     * Find an integer value associated with up to 4 consecutive bytes at the specified offset within the provided
     * byte array.
     */
    public static int toInt(byte[] bytes, int offset, int size) {
        if (offset < 0 || offset >= bytes.length)
            throw new IllegalArgumentException("Offset " + offset + " must be in bounds of bytes: 0 -> "
                    + bytes.length);
        if (size <= 0)
            throw new IllegalArgumentException("Size must be greater than 0!");
        if (offset + size > bytes.length)
            throw new IllegalArgumentException("Size " + size + " + offset " + offset
                    + " must be less than or equal to the number of bytes: " + bytes.length);
        int value = 0;
        for (int counter = size; counter > 0; --counter) {
            byte b = bytes[offset + (size - counter)];
            switch (counter) {
                case 1:
                    value |= (int)b & MASK3;
                    break;
                case 2:
                    value |= ((int)b << 8) & MASK2;
                    break;
                case 3:
                    value |= ((int)b << 16) & MASK1;
                    break;
                case 4:
                    value |= ((int)b << 24) & MASK0;
                    break;
                default:
                    throw new IllegalArgumentException("Size exceeds int capacity: " + size + " > " + " 4 bytes.");
            }
        }
        return value;
    }
}
