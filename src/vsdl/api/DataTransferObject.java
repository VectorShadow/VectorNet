package vsdl.api;

import vsdl.except.DataConversionException;

import java.io.*;

/**
 * Wraps all data sent as packets.
 */
public class DataTransferObject implements Serializable {

    private final Serializable DATA;
    private final short OPCODE;

    public DataTransferObject(Serializable data, short opcode) {
        DATA = data;
        OPCODE = opcode;
    }

    public Serializable getData() {
        return DATA;
    }

    public short getOpcode() {
        return OPCODE;
    }

    public static byte[] pack(DataTransferObject dto) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        byte[] b;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(dto);
            out.flush();
            b = bos.toByteArray();
        } catch (Exception e) {
            throw new DataConversionException(e.getMessage());
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return b;
    }

    public static DataTransferObject unpack(byte[] streamData) throws ClassCastException, StreamCorruptedException {
        ByteArrayInputStream bis = new ByteArrayInputStream(streamData);
        ObjectInput objectInput = null;
        DataTransferObject dto;
        try {
            objectInput = new ObjectInputStream(bis);
            dto = (DataTransferObject)objectInput.readObject();
        } catch (ClassCastException | StreamCorruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new DataConversionException(e.getMessage());
        } finally {
            try {
                if (objectInput != null) {
                    objectInput.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataTransferObject && DATA.equals(((DataTransferObject) o).DATA) &&
                OPCODE == ((DataTransferObject) o).OPCODE;
    }
}
