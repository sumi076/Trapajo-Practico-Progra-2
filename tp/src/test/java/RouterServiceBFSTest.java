package resto.service;

import org.junit.jupiter.api.Test;
import resto.domain.*;
import resto.storage.ArrayPedidos;
import resto.storage.ArrayRepartidores;
import resto.tda.GraphWeightedMatrix;

import static org.junit.jupiter.api.Assertions.*;

public class RouterServiceBFSTest {

    @Test
    public void elegirProximoPedidoEligeElMasCercano() {
        //Grafo
        GraphWeightedMatrix g = new GraphWeightedMatrix(5);
        int RESTO = g.addVertex("RESTAURANTE"); //0
        int A = g.addVertex("BARRIO_A");        //1
        int B = g.addVertex("BARRIO_B");        //2

        //camino corto a A, largo a B
        g.addEdgeMeters(RESTO, A, 10);
        g.addEdgeMeters(RESTO, B, 50);

        //TDA de repartidores
        ArrayRepartidores repsStore = new ArrayRepartidores(10);
        repsStore.add(new Repartidor(1, "Ana", true));

        //TDA de pedidos
        ArrayPedidos pedidosStore = new ArrayPedidos(10);

        int[] platos = new int[]{1};

        Pedido p1 = new Pedido(1, "C1", platos, TipoPedido.DOMICILIO, Prioridad.NORMAL, A);
        Pedido p2 = new Pedido(2, "C2", platos, TipoPedido.DOMICILIO, Prioridad.NORMAL, B);

        p1.estado = EstadoPedido.LISTO;
        p2.estado = EstadoPedido.LISTO;

        pedidosStore.add(p1);
        pedidosStore.add(p2);

        RouterService router = new RouterServiceBFS(pedidosStore, repsStore, g, 10);

        int elegido = router.elegirProximoPedido(1);
        assertEquals(1, elegido, "Debe elegir el pedido con destino más cercano (BARRIO_A)");
    }

    @Test
    public void actualizarPosicionRepartidorMueveSuOrigen() {
        GraphWeightedMatrix g = new GraphWeightedMatrix(5);
        int RESTO = g.addVertex("RESTAURANTE");
        int A = g.addVertex("BARRIO_A");

        ArrayRepartidores repsStore = new ArrayRepartidores(10);
        repsStore.add(new Repartidor(1, "Ana", true));

        ArrayPedidos pedidosStore = new ArrayPedidos(10);

        RouterService router = new RouterServiceBFS(pedidosStore, repsStore, g, 10);
        assertDoesNotThrow(() -> router.actualizarPosicionRepartidor(1, A));
        assertDoesNotThrow(() -> router.actualizarPosicionRepartidor(1, RESTO));
    }
}
