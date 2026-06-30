package main.java.exception;

public class StatusInvalidoException extends RuntimeException {
    public StatusInvalidoException(String message) {

        super("Transição de status não permitida!");
    }

    public StatusInvalidoException() {

    }
}
