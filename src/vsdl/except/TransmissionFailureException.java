package vsdl.except;

public class TransmissionFailureException extends VectorNetException {
    public TransmissionFailureException() {
        super();
    }
    public TransmissionFailureException(String message) {
        super(message);
    }
}
