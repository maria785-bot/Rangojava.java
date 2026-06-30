package exception;

public class CancelamentoNaoPermitidoException extends Exception {
    public CancelamentoNaoPermitidoException() {
        super("Cancelamento não permitido.");
    }

    public CancelamentoNaoPermitidoException(String mensagem) {
        super(mensagem);
    }
}