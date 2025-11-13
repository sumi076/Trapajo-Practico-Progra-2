package resto.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resto.domain.EstadoPedido;
import resto.domain.Pedido;
import resto.domain.Prioridad;
import resto.domain.TipoPedido;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayPedidosTest {

    private ArrayPedidos array;

    @BeforeEach
    public void setUp() {
        array = new ArrayPedidos(100);
    }

    private Pedido nuevoPedido(int id, String cliente, int[] platos) {
        // Constructor: (id, cliente, platos, tipo, prioridad, destinoVertexId)
        return new Pedido(id, cliente, platos, TipoPedido.LLEVAR, Prioridad.NORMAL, 0);
    }

    @Test
    public void topClientePorCantidadDebeDevolverElMasFrecuente() {
        Pedido p1 = nuevoPedido(1, "Juan", new int[]{1});
        Pedido p2 = nuevoPedido(2, "Ana", new int[]{2});
        Pedido p3 = nuevoPedido(3, "Juan", new int[]{3});
        Pedido p4 = nuevoPedido(4, "Pedro", new int[]{1});
        Pedido p5 = nuevoPedido(5, "Juan", new int[]{2});

        array.add(p1);
        array.add(p2);
        array.add(p3);
        array.add(p4);
        array.add(p5);

        String top = array.topClientePorCantidad();
        assertEquals("Juan", top, "Juan es el cliente con más pedidos");
    }

    @Test
    public void topPlatoIdDebeDevolverElPlatoMasPedido() {
        Pedido p1 = nuevoPedido(1, "C1", new int[]{1, 2});
        Pedido p2 = nuevoPedido(2, "C2", new int[]{2, 2});
        Pedido p3 = nuevoPedido(3, "C3", new int[]{3, 2});

        array.add(p1); //platos: 1,2
        array.add(p2); //platos: 2,2
        array.add(p3); //platos: 3,2

        //Conteo:
        //plato 1 -> 1 vez
        //plato 2 -> 4 veces
        //plato 3 -> 1 vez

        int topPlato = array.topPlatoId();
        assertEquals(2, topPlato, "El plato 2 es el más pedido");
    }

    @Test
    public void countByEstadoDebeContarCorrectamente() {
        Pedido p1 = nuevoPedido(1, "C1", new int[]{1});
        Pedido p2 = nuevoPedido(2, "C2", new int[]{2});
        Pedido p3 = nuevoPedido(3, "C3", new int[]{3});

        p1.estado = EstadoPedido.PENDIENTE;
        p2.estado = EstadoPedido.ENTREGADO;
        p3.estado = EstadoPedido.ENTREGADO;

        array.add(p1);
        array.add(p2);
        array.add(p3);

        assertEquals(1, array.countByEstado(EstadoPedido.PENDIENTE));
        assertEquals(2, array.countByEstado(EstadoPedido.ENTREGADO));
    }

    @Test
    public void countEntregadosPorRepartidorDebeFiltrarPorId() {
        Pedido p1 = nuevoPedido(1, "C1", new int[]{1});
        Pedido p2 = nuevoPedido(2, "C2", new int[]{2});
        Pedido p3 = nuevoPedido(3, "C3", new int[]{3});

        p1.estado = EstadoPedido.ENTREGADO;
        p1.repartidorId = 1;

        p2.estado = EstadoPedido.ENTREGADO;
        p2.repartidorId = 2;

        p3.estado = EstadoPedido.ENTREGADO;
        p3.repartidorId = 1;

        array.add(p1);
        array.add(p2);
        array.add(p3);

        assertEquals(2, array.countEntregadosPorRepartidor(1),
                "El repartidor 1 entregó 2 pedidos");
        assertEquals(1, array.countEntregadosPorRepartidor(2),
                "El repartidor 2 entregó 1 pedido");
    }
}
