package resto.service;

import resto.domain.*;
import resto.storage.ArrayPedidos;
import resto.storage.ArrayPlatos;
import resto.storage.ArrayRepartidores;
import resto.tda.PriorityQueueArray;
import resto.tda.PriorityQueueTDA;
import resto.tda.QueueArray;
import resto.tda.QueueTDA;
import resto.tda.DiccionarioMTDA;

public class PedidoServiceImpl implements PedidoService {
    private final ArrayPedidos pedidos;
    private final ArrayPlatos platos;
    private final ArrayRepartidores reps;
    private final PriorityQueueTDA pendientesPQ; //cola con prioridad (pendientes)
    private final QueueTDA colaCocina;           //cola FIFO (en preparación)
    private int nextId = 1;

    public PedidoServiceImpl(ArrayPedidos pedidos, ArrayPlatos platos, ArrayRepartidores reps) {
        this.pedidos = pedidos;
        this.platos = platos;
        this.reps = reps;
        this.pendientesPQ = new PriorityQueueArray(5000);
        this.colaCocina = new QueueArray(5000);
    }

    // === ALTAS ===

    @Override
    public int crearPedido(String cliente, int[] platosIds, TipoPedido tipo, Prioridad prio) {
        //Si no especifican destino y es DOMICILIO, por defecto 0 (RESTAURANTE)
        int destino = (tipo == TipoPedido.DOMICILIO) ? 0 : 0;
        return crearPedidoConDestino(cliente, platosIds, tipo, prio, destino);
    }

    @Override
    public int crearPedidoConDestino(String cliente, int[] platosIds, TipoPedido tipo,
                                     Prioridad prio, int destinoVertexId) {
        Pedido p = new Pedido(nextId++, cliente, platosIds, tipo, prio, destinoVertexId);
        pedidos.add(p);

        //Los pedidos nuevos arrancan como PENDIENTE
        int priority = (prio == Prioridad.VIP) ? 1 : 2;
        pendientesPQ.add(p.id, priority);

        return p.id;
    }

    //FLUJO DE ESTADOS

    @Override
    public Pedido verSiguientePendiente() {
        return peekPendingValid();
    }

    @Override
    public Pedido prepararSiguiente() {
        //Saco el siguiente PENDIENTE (VIP primero) de la cola con prioridad
        Pedido p = pollPendingValid();
        if (p != null) {
            p.estado = EstadoPedido.EN_PREPARACION;
            //Entra en la cola de cocina (FIFO)
            colaCocina.add(p.id);
        }
        return p;
    }

    @Override
    public void marcarListo(int id) {
        //Si hay algo en la cola de cocina, respetamos FIFO
        if (!colaCocina.isEmpty()) {
            int idCocina = colaCocina.getFirst();
            colaCocina.remove();
            Pedido p = pedidos.getById(idCocina);
            if (p != null) {
                p.estado = EstadoPedido.LISTO;
            }
        } else {
            //Si por alguna razón la cola está vacía, mantenemos comportamiento anterior
            Pedido p = pedidos.getById(id);
            if (p != null) {
                p.estado = EstadoPedido.LISTO;
            }
        }
    }

    @Override
    public void asignarRepartidor() {
        //Legacy: asigna el primer LISTO DOMICILIO al primer repartidor disponible
        Pedido p = pedidos.findFirstReadyForDelivery();
        if (p == null) return;
        int repId = reps.nextDisponible();
        if (repId == -1) return;
        p.repartidorId = repId;
        p.estado = EstadoPedido.EN_ENVIO;
        reps.setDisponible(repId, false);
    }

    @Override
    public void asignarPedidoARepartidor(int pedidoId, int repartidorId) {
        Pedido p = pedidos.getById(pedidoId);
        if (p == null || p.estado != EstadoPedido.LISTO) return;
        p.repartidorId = repartidorId;
        p.estado = EstadoPedido.EN_ENVIO;
        reps.setDisponible(repartidorId, false);
    }

    @Override
    public void marcarEntregado(int id) {
        Pedido p = pedidos.getById(id);
        if (p == null) return;
        p.estado = EstadoPedido.ENTREGADO;
        if (p.repartidorId != -1) {
            reps.setDisponible(p.repartidorId, true);
        }
    }

    @Override
    public Pedido getPedido(int pedidoId) {
        return pedidos.getById(pedidoId);
    }

    //REPORTES

    @Override
    public int pendientes() {
        return pedidos.countByEstado(EstadoPedido.PENDIENTE);
    }

    @Override
    public int finalizados() {
        return pedidos.countByEstado(EstadoPedido.ENTREGADO);
    }

    @Override
    public int repartosPor(int repId) {
        return pedidos.countEntregadosPorRepartidor(repId);
    }

    @Override
    public String clienteTop() {
        return pedidos.topClientePorCantidad();
    }

    @Override
    public int platoMasPedidoId() {
        return pedidos.topPlatoId();
    }

    //helpers PQ (cola con prioridad de pendientes)

    private Pedido peekPendingValid() {
        while (!pendientesPQ.isEmpty()) {
            int id = pendientesPQ.getElement();
            Pedido p = pedidos.getById(id);
            if (p != null && p.estado == EstadoPedido.PENDIENTE) return p;
            pendientesPQ.remove();
        }
        return null;
    }

    private Pedido pollPendingValid() {
        while (!pendientesPQ.isEmpty()) {
            int id = pendientesPQ.getElement();
            pendientesPQ.remove();
            Pedido p = pedidos.getById(id);
            if (p != null && p.estado == EstadoPedido.PENDIENTE) return p;
        }
        return null;
    }

    @Override
    public int totalPedidos() {
        return pedidos.size();
    }

    @Override
    public DiccionarioMTDA agruparPorRepartidor() {
        return pedidos.agruparPorRepartidor();
    }

    @Override
    public DiccionarioMTDA agruparPorBarrio() {
        return pedidos.agruparPorBarrio();
    }

    @Override
    public DiccionarioMTDA agruparPorCliente(String[] nombresClientes) {
        return pedidos.agruparPorCliente(nombresClientes);
    }
}
