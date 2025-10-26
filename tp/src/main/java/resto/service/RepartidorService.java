package resto.service;

import resto.domain.Repartidor;

public interface RepartidorService {
    Repartidor[] listar();
    int siguienteDisponibleId();
    void marcarOcupado(int id);
    void marcarDisponible(int id);
    String nombreDe(int id);
}
