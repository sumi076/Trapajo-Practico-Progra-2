package resto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resto.domain.*;
import resto.storage.ArrayPedidos;
import resto.storage.ArrayPlatos;
import resto.storage.ArrayRepartidores;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoServiceImplTest {

    private ArrayPlatos platosStore;
    private ArrayRepartidores repsStore;
    private ArrayPedidos pedidosStore;
    private PedidoServiceImpl service;
    private RepartidorService repartidorService;

    @BeforeEach
    public void setUp() {
        platosStore = new ArrayPlatos(100);
        repsStore = new ArrayRepartidores(50);
        pedidosStore = new ArrayPedidos(1000);

        // mismos platos que en el main (o simplificados)
        platosStore.add(new Plato(1, "Milanesa", 6000, 20));
        platosStore.add(new Plato(2, "Hamburguesa", 6000, 15));
        platosStore.add(new Plato(3, "Pizza", 15000, 18));

        // repartidores
        repsStore.add(new Repartidor(1, "Ana", true));
        repsStore.add(new Repartidor(2, "Luis", true));

        service = new PedidoServiceImpl(pedidosStore, platosStore, repsStore);
        repartidorService = new RepartidorServiceImpl(repsStore);
    }

    @Test
    public void crearPedidoGeneraIdYLoDejaPendiente() {
        int[] platos = new int[]{1, 2};
        int id = service.crearPedido("Juan", platos, TipoPedido.LLEVAR, Prioridad.NORMAL);

        assertEquals(1, id, "El primer pedido debería tener ID 1");
        Pedido p = service.getPedido(id);
        assertNotNull(p, "El pedido recién creado no debe ser null");
        assertEquals(EstadoPedido.PENDIENTE, p.estado, "Un pedido nuevo debe estar en estado PENDIENTE");
        assertEquals(1, service.pendientes(), "Debe haber un pedido pendiente");
    }

    @Test
    public void prioridadVIPDebeSalirAntesQueNormal() {
        int[] platos = new int[]{1};
        int idNormal = service.crearPedido("ClienteNormal", platos, TipoPedido.LLEVAR, Prioridad.NORMAL);
        int idVip = service.crearPedido("ClienteVIP", platos, TipoPedido.LLEVAR, Prioridad.VIP);

        Pedido siguiente = service.verSiguientePendiente();
        assertNotNull(siguiente);
        assertEquals(Prioridad.VIP, siguiente.prioridad,
                "El siguiente pendiente debe ser el VIP aunque se haya creado después");
        assertEquals(idVip, siguiente.id, "El ID del pedido retornado debe ser el del VIP");
    }

    @Test
    public void prepararSiguienteMueveAPreparacionYUsaColaCocina() {
        int[] platos = new int[]{1};
        int id = service.crearPedido("Juan", platos, TipoPedido.LLEVAR, Prioridad.NORMAL);

        Pedido preparado = service.prepararSiguiente();
        assertNotNull(preparado, "Debe devolver un pedido al preparar");
        assertEquals(id, preparado.id);
        assertEquals(EstadoPedido.EN_PREPARACION, preparado.estado,
                "El pedido debe pasar a EN_PREPARACION");
        assertEquals(0, service.pendientes(), "Ya no debe haber pedidos pendientes");
    }

    @Test
    public void marcarListoRespetaOrdenFIFODeCocina() {
        int[] platos = new int[]{1};

        int id1 = service.crearPedido("C1", platos, TipoPedido.LLEVAR, Prioridad.NORMAL);
        int id2 = service.crearPedido("C2", platos, TipoPedido.LLEVAR, Prioridad.NORMAL);

        service.prepararSiguiente();
        service.prepararSiguiente();
        service.marcarListo(0);
        Pedido p1 = service.getPedido(id1);
        Pedido p2 = service.getPedido(id2);

        assertEquals(EstadoPedido.LISTO, p1.estado, "El primer pedido preparado debe quedar LISTO");
        assertEquals(EstadoPedido.EN_PREPARACION, p2.estado, "El segundo sigue EN_PREPARACION");


        service.marcarListo(0);
        p2 = service.getPedido(id2);

        assertEquals(EstadoPedido.LISTO, p2.estado, "Luego, el segundo debe quedar LISTO");
    }

    @Test
    public void flujoCompletoEntregaLiberaRepartidor() {
        int[] platos = new int[]{1};


        int id = service.crearPedido("DomCliente", platos, TipoPedido.DOMICILIO, Prioridad.VIP);


        service.prepararSiguiente();
        service.marcarListo(0);


        service.asignarPedidoARepartidor(id, 1);
        Pedido p = service.getPedido(id);
        assertEquals(EstadoPedido.EN_ENVIO, p.estado);
        assertEquals(1, p.repartidorId);


        service.marcarEntregado(id);
        p = service.getPedido(id);
        assertEquals(EstadoPedido.ENTREGADO, p.estado, "El pedido debe quedar ENTREGADO");


        int repLibre = repartidorService.siguienteDisponibleId();
        assertEquals(1, repLibre, "El repartidor debería volver a aparecer como disponible");
    }
}
