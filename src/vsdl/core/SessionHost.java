package vsdl.core;

import vsdl.except.TransmissionFailureException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SessionHost extends Thread {

    private final int MIN_ID = 0x1000_0000;
    private final int MAX_ID = 0x7FFF_FFFF;

    private boolean isOpen = true;
    private final ServerSocket serverSocket;
    private int linkID;

    public SessionHost(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        linkID = (new Random()).nextInt(MAX_ID) - MIN_ID;
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
            } catch (IOException e) { //no need to kill the server here, log the error and continue
                throw new TransmissionFailureException("Failed to accept connection: " + e.getMessage());
            }
        }
    }

}