package resto.tda;

public class DiccionarioSimpleArray implements DiccionarioTDA {

    private int[] claves;
    private int[] valores;
    private int cant;

    public DiccionarioSimpleArray(int capacidad) {
        claves = new int[capacidad];
        valores = new int[capacidad];
        cant = 0;
    }

    @Override
    public void add(int key, int value) {
        int pos = buscar(key);
        if (pos != -1) {
            valores[pos] = value;
            return;
        }
        if (cant == claves.length) return; //lleno, no agregamos
        claves[cant] = key;
        valores[cant] = value;
        cant++;
    }

    @Override
    public void remove(int key) {
        int pos = buscar(key);
        if (pos == -1) return;
        cant--;
        claves[pos] = claves[cant];
        valores[pos] = valores[cant];
    }

    @Override
    public int get(int key) {
        int pos = buscar(key);
        if (pos == -1) return 0; //para simplificar
        return valores[pos];
    }

    @Override
    public int[] getKeys() {
        int[] res = new int[cant];
        for (int i = 0; i < cant; i++) res[i] = claves[i];
        return res;
    }

    @Override
    public boolean isEmpty() {
        return cant == 0;
    }

    private int buscar(int key) {
        for (int i = 0; i < cant; i++) {
            if (claves[i] == key) return i;
        }
        return -1;
    }
}
