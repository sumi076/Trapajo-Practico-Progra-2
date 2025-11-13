package resto.tda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GraphWeightedMatrixTest {

    @Test
    public void agregarVerticesActualizaSizeYNombres() {
        GraphWeightedMatrix g = new GraphWeightedMatrix(10);

        int resto = g.addVertex("RESTAURANTE");
        int a = g.addVertex("BARRIO_A");
        int b = g.addVertex("BARRIO_B");

        assertEquals(0, resto);
        assertEquals(1, a);
        assertEquals(2, b);

        assertEquals(3, g.size(), "El size debe coincidir con la cantidad de vértices agregados");

        assertEquals("RESTAURANTE", g.nameOf(resto));
        assertEquals("BARRIO_A", g.nameOf(a));
        assertEquals("BARRIO_B", g.nameOf(b));
    }

    @Test
    public void agregarAristasNoLanzaErrores() {
        GraphWeightedMatrix g = new GraphWeightedMatrix(5);

        int v0 = g.addVertex("V0");
        int v1 = g.addVertex("V1");

        //solo verificamos que se pueda agregar sin lanzar excepción
        assertDoesNotThrow(() -> g.addEdgeMeters(v0, v1, 50));
    }
}
