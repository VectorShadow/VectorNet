package vsdl.api;

import java.io.Serializable;

public interface TransmissionHandler {
    void handleDisconnection();
    void handleTransmission(int opcode, Serializable data);
    void handleTransmissionError();
}
