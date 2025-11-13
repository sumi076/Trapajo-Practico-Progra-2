package resto.tda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueueArrayTest {

    @Test
    public void colaDebeSerFifo() {
        QueueTDA q = new QueueArray(10);

        assertTrue(q.isEmpty());

        q.add(1);
        q.add(2);
        q.add(3);

        assertFalse(q.isEmpty());
        assertEquals(1, q.getFirst());
        q.remove();
        assertEquals(2, q.getFirst());
        q.remove();
        assertEquals(3, q.getFirst());
        q.remove();

        assertTrue(q.isEmpty());
    }

    @Test
    public void getFirstNoDebeCambiarLaCola() {
        QueueTDA q = new QueueArray(10);
        q.add(5);
        q.add(7);

        int primero = q.getFirst();
        assertEquals(5, primero);
        assertFalse(q.isEmpty());
        assertEquals(5, q.getFirst(), "getFirst no debe eliminar el elemento");
    }
}
