package vsdl.api;

import vsdl.cipher.RSA;

/**
 * The hub for a process connecting to a VectorNet system.
 */
public class NetSessionManager {
    public static final int MODE_STANDBY = -1;
    public static final int MODE_MASTER_SERVER = 0;
    public static final int MODE_CLIENT_HOST = 1;
    public static final int MODE_CLIENT_GUEST = 1;

    private static NetSessionManager instance = null;

    private int operatingMode = MODE_STANDBY;

    private NetSessionManager() {
        RSA.generateSessionKeys();
    }

    public static NetSessionManager getInstance() {
        if (instance == null) {
            instance = new NetSessionManager();
        }
        return instance;
    }

    public void setOperatingMode(int mode) {
        if (mode < MODE_MASTER_SERVER || mode > MODE_CLIENT_GUEST)
            throw new IllegalArgumentException("Illegal mode: " + mode);
        operatingMode = mode;
    }

}
