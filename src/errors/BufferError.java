package errors;

public class BufferError extends Exception {

    public BufferError() {
        super("Unspecified BufferError");
    }

    public BufferError(String message) {
        super("BufferError: " + message);
    }
}
