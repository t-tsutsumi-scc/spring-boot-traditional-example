package app.exception;

import java.util.Objects;

public class UncheckedException extends RuntimeException {

    private static final long serialVersionUID = -3676363775393479708L;

    public UncheckedException(String message) {
        super(message);
    }

    public UncheckedException(Throwable cause) {
        super(Objects.requireNonNull(cause));
    }

    public UncheckedException(String message, Throwable cause) {
        super(message, Objects.requireNonNull(cause));
    }

}
