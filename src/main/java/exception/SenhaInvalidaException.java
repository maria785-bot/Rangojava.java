package exception;

public class SenhaInvalidaException extends Exception {
    public SenhaInvalidaException() {
        super("Senha inválida.");
    }

    public SenhaInvalidaException(String mensagem) {
        super(mensagem);
    }
}