package resto.service;

import resto.domain.*;
import resto.tda.DiccionarioMTDA;

public interface PedidoService {
    //Altas
    int crearPedido(String cliente, int[] platos, TipoPedido tipo, Prioridad prio);
    int crearPedidoConDestino(String cliente, int[] platos, TipoPedido tipo, Prioridad prio, int destinoVertexId);

    //Flujo
    Pedido verSiguientePendiente();
    Pedido prepararSiguiente();
    void marcarListo(int pedidoId);

    void asignarRepartidor(); // legacy
    void asignarPedidoARepartidor(int pedidoId, int repartidorId);

    void marcarEntregado(int pedidoId);

    Pedido getPedido(int pedidoId);

    //Reportes simples
    int pendientes();
    int finalizados();
    int repartosPor(int repartidorId);
    String clienteTop();
    int platoMasPedidoId();

    //Reportes avanzados
    int totalPedidos();

    DiccionarioMTDA agruparPorRepartidor();
    DiccionarioMTDA agruparPorBarrio();
    DiccionarioMTDA agruparPorCliente(String[] nombresClientes);
}
