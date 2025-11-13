package resto.tda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PriorityQueueArrayTest {

    @Test
    public void vipDebeSalirAntesQueNormal() {
        PriorityQueueTDA pq = new PriorityQueueArray(10);

        pq.add(100, 2); // normal
        pq.add(200, 1); // VIP
        pq.add(300, 2); // normal

        assertFalse(pq.isEmpty());

        int primero = pq.getElement();
        assertEquals(200, primero, "El primero debe ser el VIP (priority=1)");
        pq.remove();

        // los siguientes pueden ser 100 o 300 en cualquier orden,
        // mientras sigan siendo de prioridad 2 (NORMAL)
        int segundo = pq.getElement();
        assertTrue(segundo == 100 || segundo == 300);
        pq.remove();

        int tercero = pq.getElement();
        assertTrue(tercero == 100 || tercero == 300);
        pq.remove();

        assertTrue(pq.isEmpty());
    }

    @Test
    public void mismaPrioridadDevuelveTodosLosElementosSinPerderNinguno() {
        PriorityQueueTDA pq = new PriorityQueueArray(10);

        pq.add(10, 2);
        pq.add(20, 2);
        pq.add(30, 2);

        int a = pq.getElement();
        pq.remove();
        int b = pq.getElement();
        pq.remove();
        int c = pq.getElement();
        pq.remove();

        assertTrue(pq.isEmpty());

        //verificamos que los 3 valores están entre {10,20,30}
        int[] vals = new int[]{a, b, c};
        boolean tiene10 = false, tiene20 = false, tiene30 = false;
        for (int i = 0; i < vals.length; i++) {
            if (vals[i] == 10) tiene10 = true;
            if (vals[i] == 20) tiene20 = true;
            if (vals[i] == 30) tiene30 = true;
        }

        assertTrue(tiene10 && tiene20 && tiene30,
                "La cola debe devolver los tres elementos agregados, sin importar el orden");
    }
}
