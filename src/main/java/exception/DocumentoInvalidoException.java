package main.java.exception;

public class DocumentoInvalidoException extends RuntimeException {
    public DocumentoInvalidoException(String tipo) {

        super(tipo + " inválido! Verifique os dígitos.");
    }
}
