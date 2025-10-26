package resto.service;

import resto.domain.Plato;

public interface CatalogoService {
    Plato[] listarPlatos();
    Plato obtener(int id);
}
