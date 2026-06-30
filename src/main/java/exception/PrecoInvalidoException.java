package main.java.exception;

public class PrecoInvalidoException extends RuntimeException {
    public PrecoInvalidoException(String message) {

        super("Preço deve ser maior que zero!");
    }

    public PrecoInvalidoException() {

    }
}
