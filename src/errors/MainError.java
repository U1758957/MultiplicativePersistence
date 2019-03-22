package errors;

public class MainError extends Exception {

    public MainError() {
        super("Unspecified MainError");
    }

    public MainError(String message) {
        super("BufferError: " + message);
    }
}
