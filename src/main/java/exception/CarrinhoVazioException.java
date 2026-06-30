package main.java.exception;

public class CarrinhoVazioException extends RuntimeException {
    public CarrinhoVazioException(String message) {
        super("Não é possível finalizar pedido com carrinho vazio!");
    }
}
