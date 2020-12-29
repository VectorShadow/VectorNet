package vsdl.core;

import static vsdl.core.VNConstants.*;

import vsdl.api.DataTransferObject;
import vsdl.cipher.RSA;
import vsdl.except.TransmissionFailureException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SessionHost extends Thread {

    private static final int MIN_ID = 0x1000_0000;
    private static final int MAX_ID = 0x5FFF_FFFF;

    private boolean isOpen = true;
    private final ServerSocket serverSocket;
    private int linkID;

    public SessionHost(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        linkID = randomInitialLinkID();
    }

    public void close() {
        isOpen = false;
    }

    public void run() {
        Socket socket;
        for(;;) {
            try {
                socket = serverSocket.accept();
                NetLink nl = new NetLink(linkID++, socket);
                nl.send(new DataTransferObject(RSA.getSessionPublicKey(), OPCODE_LINK_HANDSHAKE_PUBLIC));
            } catch (IOException e) { //no need to kill the server here, log the error and continue
                throw new TransmissionFailureException("Failed to accept connection: " + e.getMessage());
            }
        }
    }

    static int randomInitialLinkID() {
        return (new Random()).nextInt(MAX_ID) + MIN_ID;
    }

}