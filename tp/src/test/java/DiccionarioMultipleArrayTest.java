package resto.tda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiccionarioMultipleArrayTest {

    @Test
    public void agregarVariosValoresMismaClave() {
        DiccionarioMTDA dic = new DiccionarioMultipleArray(10, 20);

        dic.add(1, 10);
        dic.add(1, 20);
        dic.add(1, 30);

        assertFalse(dic.isEmpty());

        int[] vals = dic.get(1);
        assertEquals(3, vals.length);
        assertEquals(10, vals[0]);
        assertEquals(20, vals[1]);
        assertEquals(30, vals[2]);
    }

    @Test
    public void variasClavesConSusListas() {
        DiccionarioMTDA dic = new DiccionarioMultipleArray(10, 20);

        dic.add(1, 10);
        dic.add(1, 20);
        dic.add(2, 30);

        int[] keys = dic.getKeys();
        boolean tiene1 = false;
        boolean tiene2 = false;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == 1) tiene1 = true;
            if (keys[i] == 2) tiene2 = true;
        }
        assertTrue(tiene1 && tiene2, "Debe contener claves 1 y 2");

        int[] vals1 = dic.get(1);
        int[] vals2 = dic.get(2);

        assertEquals(2, vals1.length);
        assertEquals(1, vals2.length);
        assertEquals(30, vals2[0]);
    }

    @Test
    public void removeValorEspecificoYRemoveTotal() {
        DiccionarioMTDA dic = new DiccionarioMultipleArray(10, 20);

        dic.add(1, 10);
        dic.add(1, 20);
        dic.add(1, 30);

        dic.remove(1, 20);

        int[] vals = dic.get(1);
        assertEquals(2, vals.length);
        assertEquals(10, vals[0]);
        assertEquals(30, vals[1]);

        dic.remove(1);
        assertTrue(dic.isEmpty(), "Después de eliminar la única clave, debe estar vacío");
    }
}
