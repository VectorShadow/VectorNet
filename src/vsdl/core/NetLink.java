package vsdl.core;

import static vsdl.core.VNConstants.*;

import vsdl.api.DataTransferObject;
import vsdl.api.NetSessionManager;
import vsdl.cipher.ByteCipher;
import vsdl.except.StreamSizeException;
import vsdl.except.TransmissionFailureException;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;

/**
 * A NetLink is the basic connection for a VectorNet system.
 * It represents a single link between two Nodes, and is responsible for tracking all data passed between those nodes.
 *
 */
public class NetLink extends Thread {
    private final int ID_SELF;
    private int idPartner = -1;

    private final Socket SOCK;

    private boolean encrypted = false;
    private ByteCipher key = null;
    private boolean isOpen = true;
    private int transmissionCount = 0;

    NetLink(int id, Socket socket) {
        ID_SELF = id;
        SOCK = socket;
    }

    NetLink(int id, String host, int port) throws IOException {
        this(id, new Socket(host, port));
    }

    void close() {
        isOpen = false;
    }

    void send(DataTransferObject dto) {
        if (dto.getOpcode() > OPCODE_LINK_HANDSHAKE_ACK && !encrypted)
            throw new IllegalStateException("RSA Handshake must be performed prior to all other transmission.");
        byte[] rawData = DataTransferObject.pack(dto);
        int packetCount = (rawData.length / MAX_PACKET_DATA) + 1;
        if (packetCount > MAX_TRANSMISSION_PACKETS)
            throw new StreamSizeException("Stream exceeds transmission capacity: " +
                    "(" + packetCount + " > " + MAX_TRANSMISSION_PACKETS + ")");
        VectorPacket[] packets = new VectorPacket[packetCount];
        byte[] streamData = new byte[packetCount * PACKET_SIZE];
        for (int i = 0; i < packetCount; ++i) {
            byte[] packetData = new byte[i == packetCount - 1 ? rawData.length % MAX_PACKET_DATA : MAX_PACKET_DATA];
            System.arraycopy(rawData, i * MAX_PACKET_DATA, packetData, 0, packetData.length);
            packets[i] = new VectorPacket(
                    ID_SELF,
                    transmissionCount,
                    i == 0 ? rawData.length : packetData.length,
                    (byte)i,
                    encrypted ? key.encrypt(packetData) : packetData
            );
            System.arraycopy(VectorPacket.toStream(packets[i]), 0, streamData, i * PACKET_SIZE, PACKET_SIZE);
        }
        try {
            SOCK.getOutputStream().write(streamData);
        } catch (SocketException se) {
            NetSessionManager.getTransmissionHandler().handleDisconnection();
        } catch (IOException ioe) {
            throw new TransmissionFailureException("Unexpected IOException on data transmission: " + ioe.getMessage());
        }
        ++transmissionCount;
    }

    public void run() {
        byte[] packetData = new byte[PACKET_SIZE];
        VectorPacket vectorPacket;
        VectorPacket[] transmission = new VectorPacket[0];
        int transmissionSize = 0;
        int transmissionIndex = -1;
        int packetsExpected = 0;
        int packetsReceived = 0;
        boolean streamCorrupted = false;
        do {
            try {
                if (!streamCorrupted) {
                    while (SOCK.getInputStream().available() < PACKET_SIZE)
                        Thread.sleep(5);
                    if (SOCK.getInputStream().read(packetData, 0, PACKET_SIZE) < 0)
                        throw new TransmissionFailureException("Error reading from socket stream.");
                    vectorPacket = VectorPacket.fromStream(packetData);
                    ++packetsReceived;
                    //verify that the sender id of the header matches our partner:
                    if (idPartner >= 0 && vectorPacket.getSenderID() != idPartner) {
                        streamCorrupted = true;
                        continue;
                    }
                    //first packet in a transmission:
                    if (vectorPacket.getSequenceID() == 0) {
                        //if we are expecting more packets in the current transmission:
                        if (packetsReceived < packetsExpected) {
                            streamCorrupted = true;
                            continue;
                        }
                        //otherwise initialize a new transmission:
                        transmission = new VectorPacket[packetsExpected];
                        transmissionIndex = vectorPacket.getXmitID();
                        transmissionSize = vectorPacket.getPacketSize();
                        packetsExpected = (transmissionSize / MAX_PACKET_DATA) + 1;
                    } else if (vectorPacket.getXmitID() != transmissionIndex || //packet is from wrong transmission
                            vectorPacket.getSequenceID() + 1 != packetsReceived) { //packet is out of order
                        streamCorrupted = true;
                        continue;
                    }
                    transmission[vectorPacket.getSequenceID()] = vectorPacket; //log this packet
                    //complete transmission received
                    if (packetsReceived == packetsExpected) {
                        byte[] dtoStream = new byte[transmissionSize];
                        for (int i = 0; i < packetsExpected; ++i) {
                            System.arraycopy(
                                    transmission[i].getData(),
                                    0,
                                    encrypted ? key.decrypt(dtoStream) : dtoStream,
                                    i * MAX_PACKET_DATA,
                                    i == packetsExpected - 1 ? vectorPacket.getPacketSize() : MAX_PACKET_DATA
                            );
                        }
                        handle(DataTransferObject.unpack(dtoStream));
                    }
                } else {
                    throw new TransmissionFailureException("Socket stream was corrupted and is not yet handled.");
                    //todo - notify sender of corruption on transmission <transmissionIndex>, then find the next
                    // transmission by stepping through the stream until we found 4 consecutive bytes that match
                    // idPartner, then start the stream from there, and set streamCorrupted to false
                }
            } catch (SocketException se) {
                NetSessionManager.getTransmissionHandler().handleDisconnection();
            } catch (IOException | InterruptedException e) {
                throw new TransmissionFailureException("Unexpected Exception on data reception - "
                        + e.getClass() + ": " + e.getMessage());
            }
        } while (isOpen);
    }

    private void handle(DataTransferObject dto) {
        int opcode = dto.getOpcode();
        Serializable data = dto.getData();
        if (opcode < OPCODE_USER) {
            switch (opcode) {
                case OPCODE_LINK_HANDSHAKE_PUBLIC:
                    key = ByteCipher.generate();
                    send(new DataTransferObject(ByteCipher.encryptRSA(key, (BigInteger)data), OPCODE_LINK_HANDSHAKE_PRIVATE));
                    break;
                case OPCODE_LINK_HANDSHAKE_PRIVATE:
                    key = ByteCipher.decryptRSA((BigInteger)data);
                    send(new DataTransferObject(ID_SELF, OPCODE_LINK_HANDSHAKE_ACK));
                    encrypted = true;
                    break;
                case OPCODE_LINK_HANDSHAKE_ACK:
                    encrypted = true;
                    idPartner = (int)data;
                    break;
                case OPCODE_LINK_ERROR:
                    NetSessionManager.getTransmissionHandler().handleTransmissionError();
                    break;
                default:
                    throw new TransmissionFailureException("Unexpected opcode " + opcode);
            }
        } else {
            NetSessionManager.getTransmissionHandler().handleTransmission(opcode, data);
        }
    }

}
