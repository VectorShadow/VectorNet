package vsdl.except;

public abstract class VectorNetException extends RuntimeException {
    public VectorNetException() {
        super();
    }
    public VectorNetException(String message) {
        super(message);
    }
}
