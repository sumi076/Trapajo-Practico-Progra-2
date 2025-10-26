package resto.service;

import resto.domain.Plato;
import resto.storage.ArrayPlatos;

public class CatalogoServiceImpl implements CatalogoService {
    private final ArrayPlatos platos;

    public CatalogoServiceImpl(ArrayPlatos platos) {
        this.platos = platos;
    }

    @Override
    public Plato[] listarPlatos() {
        return platos.all();
    }

    @Override
    public Plato obtener(int id) {
        return platos.getById(id);
    }
}
