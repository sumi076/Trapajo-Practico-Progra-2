package resto.service;

import resto.domain.*;

public interface PedidoService {
    int crearPedido(String cliente, int[] platos, TipoPedido tipo, Prioridad prio);
    Pedido verSiguientePendiente();
    Pedido prepararSiguiente();
    void marcarListo(int pedidoId);
    void asignarRepartidor();
    void marcarEntregado(int pedidoId);

    // Reportes
    int pendientes();
    int finalizados();
    int repartosPor(int repartidorId);
    String clienteTop();
    int platoMasPedidoId();
}
