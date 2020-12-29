package vsdl.api;

import vsdl.cipher.RSA;
import vsdl.core.SessionHost;
import vsdl.except.TransmissionFailureException;

import java.io.IOException;

/**
 * The hub for a process connecting to a VectorNet system.
 */
public class NetSessionManager {
    public static final int MODE_STANDBY = -1;
    public static final int MODE_SERVER_HOST = 0;
    public static final int MODE_CLIENT_HOST = 1;
    public static final int MODE_CLIENT_GUEST = 2;
    public static final int MODE_CLIENT_PRIVATE = 3;

    private static NetSessionManager instance = null;
    private static int hostPort = 16385;

    private int operatingMode = MODE_STANDBY;
    private SessionHost host;

    private NetSessionManager() {
        RSA.generateSessionKeys();
    }

    public static NetSessionManager getInstance() {
        if (instance == null) {
            instance = new NetSessionManager();
        }
        return instance;
    }

    public static void setHostPort(int portNumber) {
        hostPort = portNumber;
    }

    public void setOperatingMode(int mode) {
        if (mode < MODE_SERVER_HOST || mode > MODE_CLIENT_PRIVATE)
            throw new IllegalArgumentException("Illegal mode: " + mode);
        operatingMode = mode;
        if (operatingMode == MODE_SERVER_HOST || operatingMode == MODE_CLIENT_HOST) {
            try {
                host = new SessionHost(hostPort);
                host.start();
            } catch (IOException ioe) {
                throw new TransmissionFailureException("IOException during host creation: " + ioe.getMessage());
            }
        } else if (host != null) {
            host.close();
        }
    }

}
