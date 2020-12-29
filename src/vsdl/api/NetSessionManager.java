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

    private static int hostPort = 16385;
    private static int operatingMode = MODE_STANDBY;
    private static SessionHost sessionHost;
    private static TransmissionHandler transmissionHandler = null;

    public static TransmissionHandler getTransmissionHandler() {
        return transmissionHandler;
    }

    public static void setHostPort(int portNumber) {
        hostPort = portNumber;
    }

    public static void setOperatingMode(int mode) {
        if (mode < MODE_SERVER_HOST || mode > MODE_CLIENT_PRIVATE)
            throw new IllegalArgumentException("Illegal mode: " + mode);
        operatingMode = mode;
        if (operatingMode == MODE_SERVER_HOST || operatingMode == MODE_CLIENT_HOST) {
            try {
                sessionHost = new SessionHost(hostPort);
                sessionHost.start();
            } catch (IOException ioe) {
                throw new TransmissionFailureException("IOException during host creation: " + ioe.getMessage());
            }
        } else if (sessionHost != null) {
            sessionHost.close();
        }
    }

    public static void setTransmissionHandler(TransmissionHandler handler) {
        transmissionHandler = handler;
    }
}
