package exception;

public class StatusInvalidoException extends Exception {
    public StatusInvalidoException() {
        super("Status inválido.");
    }

    public StatusInvalidoException(String mensagem) {
        super(mensagem);
    }
}