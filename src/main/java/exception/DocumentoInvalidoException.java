package exception;

public class DocumentoInvalidoException extends Exception {
    public DocumentoInvalidoException() {
        super("Documento inválido.");
    }

    public DocumentoInvalidoException(String mensagem) {
        super("Documento inválido: " + mensagem);
    }
}