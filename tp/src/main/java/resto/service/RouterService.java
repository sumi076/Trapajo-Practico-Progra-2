package resto.service;

public interface RouterService {
    int elegirProximoPedido(int repartidorId);
    void actualizarPosicionRepartidor(int repartidorId, int destinoVertexId);
}
