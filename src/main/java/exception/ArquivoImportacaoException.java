package exception;

public class ArquivoImportacaoException extends Exception {
    public ArquivoImportacaoException() {
        super("Erro na importação do arquivo.");
    }

    public ArquivoImportacaoException(String mensagem) {
        super(mensagem);
    }
}