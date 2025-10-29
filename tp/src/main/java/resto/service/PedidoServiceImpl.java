package resto.service;

import resto.domain.*;
import resto.storage.ArrayPedidos;
import resto.storage.ArrayPlatos;
import resto.storage.ArrayRepartidores;
import resto.tda.PriorityQueueArray;
import resto.tda.PriorityQueueTDA;

public class PedidoServiceImpl implements PedidoService {
    private final ArrayPedidos pedidos;
    private final ArrayPlatos platos;
    private final ArrayRepartidores reps;
    private final PriorityQueueTDA pendientesPQ; // cola con prioridad de IDs
    private int nextId = 1;

    public PedidoServiceImpl(ArrayPedidos pedidos, ArrayPlatos platos, ArrayRepartidores reps) {
        this.pedidos = pedidos;
        this.platos = platos;
        this.reps = reps;
        this.pendientesPQ = new PriorityQueueArray(5000); // capacidad simple
    }

    @Override
    public int crearPedido(String cliente, int[] platosIds, TipoPedido tipo, Prioridad prio) {
        Pedido p = new Pedido(nextId++, cliente, platosIds, tipo, prio);
        pedidos.add(p);
        // si está pendiente, va a la cola con su prioridad (VIP=1, NORMAL=2)
        int priority = (prio == Prioridad.VIP) ? 1 : 2;
        pendientesPQ.add(p.id, priority);
        return p.id;
    }

    @Override
    public Pedido verSiguientePendiente() {
        Pedido p = peekPendingValid();
        return p;
    }

    @Override
    public Pedido prepararSiguiente() {
        Pedido p = pollPendingValid(); // saca el válido de la cola
        if (p != null) {
            p.estado = EstadoPedido.EN_PREPARACION;
        }
        return p;
    }

    @Override
    public void marcarListo(int id) {
        Pedido p = pedidos.getById(id);
        if (p != null) {
            p.estado = EstadoPedido.LISTO;
            // No necesito remover de la PQ aquí: lazy removal lo salta luego.
        }
    }

    @Override
    public void asignarRepartidor() {
        // Busco un pedido listo para DOMICILIO (no depende de la PQ)
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

    // Reportes (igual que antes, delega en ArrayPedidos)
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

    // ---- helpers de cola con prioridad ----

    // Devuelve el siguiente PENDIENTE sin sacar de la cola (saltando inválidos)
    private Pedido peekPendingValid() {
        while (!pendientesPQ.isEmpty()) {
            int id = pendientesPQ.getElement();
            Pedido p = pedidos.getById(id);
            if (p != null && p.estado == EstadoPedido.PENDIENTE) {
                return p;
            } else {
                // inválido: estaba en la PQ pero ya no está PENDIENTE -> quitar y seguir
                pendientesPQ.remove();
            }
        }
        return null;
    }

    // Saca y devuelve el siguiente PENDIENTE válido
    private Pedido pollPendingValid() {
        while (!pendientesPQ.isEmpty()) {
            int id = pendientesPQ.getElement();
            Pedido p = pedidos.getById(id);
            pendientesPQ.remove(); // quito el tope siempre
            if (p != null && p.estado == EstadoPedido.PENDIENTE) {
                return p;
            }
            // si no era válido, sigo el loop (lazy removal)
        }
        return null;
    }
}
