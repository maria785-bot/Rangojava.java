package main.java.exception;

public class ArquivoImportacaoException extends RuntimeException {
    public ArquivoImportacaoException(String mensagem) {

        super("Erro na importação: " + mensagem);
    }
}
