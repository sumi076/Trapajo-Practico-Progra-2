package resto.tda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiccionarioSimpleArrayTest {

    @Test
    public void nuevoDiccionarioEstaVacioYGetDevuelveCero() {
        DiccionarioTDA dic = new DiccionarioSimpleArray(10);

        assertTrue(dic.isEmpty());
        assertEquals(0, dic.get(5), "Si la clave no existe, debe devolver 0");
        assertEquals(0, dic.get(100));
    }

    @Test
    public void addYGetFuncionanComoMapeoClaveValor() {
        DiccionarioTDA dic = new DiccionarioSimpleArray(10);

        dic.add(1, 10);
        dic.add(2, 20);

        assertFalse(dic.isEmpty());
        assertEquals(10, dic.get(1));
        assertEquals(20, dic.get(2));

        int[] keys = dic.getKeys();
        assertEquals(2, keys.length);
    }

    @Test
    public void addSobreMismaClaveActualizaValor() {
        DiccionarioTDA dic = new DiccionarioSimpleArray(10);

        dic.add(1, 5);
        assertEquals(5, dic.get(1));

        dic.add(1, 8); //actualizar
        assertEquals(8, dic.get(1), "La clave 1 debe tener el nuevo valor");
    }

    @Test
    public void removeEliminaClaveYGetDevuelveCero() {
        DiccionarioTDA dic = new DiccionarioSimpleArray(10);

        dic.add(1, 5);
        dic.add(2, 7);

        dic.remove(1);

        assertEquals(0, dic.get(1), "Después de remove, get debe devolver 0");
        assertEquals(7, dic.get(2));
    }
}
