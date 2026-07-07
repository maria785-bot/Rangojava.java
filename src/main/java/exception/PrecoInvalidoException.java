package exception;

public class PrecoInvalidoException extends Exception {
    public PrecoInvalidoException() {
        super("Preço inválido. O preço deve ser maior que zero.");
    }

    public PrecoInvalidoException(String mensagem) {
        super(mensagem);
    }
}