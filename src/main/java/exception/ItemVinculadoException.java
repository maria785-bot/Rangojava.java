package exception;

public class ItemVinculadoException extends RuntimeException {
    public ItemVinculadoException(String message) {

        super("Este item está vinculado a pedidos abertos e não pode ser excluído!");
    }
}
