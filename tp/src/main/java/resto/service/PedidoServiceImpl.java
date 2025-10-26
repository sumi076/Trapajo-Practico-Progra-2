package resto.service;

import resto.domain.*;
import resto.storage.ArrayPedidos;
import resto.storage.ArrayPlatos;
import resto.storage.ArrayRepartidores;

public class PedidoServiceImpl implements PedidoService {
    private final ArrayPedidos pedidos;
    private final ArrayPlatos platos;
    private final ArrayRepartidores reps;
    private int nextId = 1;

    public PedidoServiceImpl(ArrayPedidos pedidos, ArrayPlatos platos, ArrayRepartidores reps) {
        this.pedidos = pedidos;
        this.platos = platos;
        this.reps = reps;
    }

    @Override
    public int crearPedido(String cliente, int[] platosIds, TipoPedido tipo, Prioridad prio) {
        Pedido p = new Pedido(nextId++, cliente, platosIds, tipo, prio);
        pedidos.add(p);
        return p.id;
    }

    @Override
    public Pedido verSiguientePendiente() {
        return pedidos.findNextPendingByPriority();
    }

    @Override
    public Pedido prepararSiguiente() {
        Pedido p = pedidos.findNextPendingByPriority();
        if (p != null) p.estado = EstadoPedido.EN_PREPARACION;
        return p;
    }

    @Override
    public void marcarListo(int id) {
        Pedido p = pedidos.getById(id);
        if (p != null) p.estado = EstadoPedido.LISTO;
    }

    @Override
    public void asignarRepartidor() {
        Pedido p = pedidos.findFirstReadyForDelivery();
        if (p == null) return;

        int repId = reps.nextDisponible();
        if (repId == -1) return;

        p.repartidorId = repId;
        p.estado = EstadoPedido.EN_ENVIO;
        reps.setDisponible(repId, false);
    }

    @Override
    public void marcarEntregado(int id) {
        Pedido p = pedidos.getById(id);
        if (p == null) return;
        p.estado = EstadoPedido.ENTREGADO;
        if (p.repartidorId != -1) reps.setDisponible(p.repartidorId, true);
    }

    // Reportes
    @Override
    public int pendientes() { return pedidos.countByEstado(EstadoPedido.PENDIENTE); }

    @Override
    public int finalizados() { return pedidos.countByEstado(EstadoPedido.ENTREGADO); }

    @Override
    public int repartosPor(int repId) { return pedidos.countEntregadosPorRepartidor(repId); }

    @Override
    public String clienteTop() { return pedidos.topClientePorCantidad(); }

    @Override
    public int platoMasPedidoId() { return pedidos.topPlatoId(); }
}
