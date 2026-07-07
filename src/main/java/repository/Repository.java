package repository;

import java.util.List;

public interface Repository<T, ID> {
    void salvar(T objeto);
    T buscarPorId(ID id);
    List<T> listarTodos();
    void atualizar(T objeto);
    void excluir(ID id);
}
