package resto.tda;

public class DiccionarioMultipleArray implements DiccionarioMTDA {

    private int[] keys;           //claves únicas
    private int[][] values;       //valores asociados a cada clave
    private int[] sizes;          //cantidad de valores por clave
    private int keyCount;         //cantidad de claves cargadas

    public DiccionarioMultipleArray(int maxKeys, int maxValuesPerKey) {
        keys = new int[maxKeys];
        values = new int[maxKeys][maxValuesPerKey];
        sizes = new int[maxKeys];
        keyCount = 0;
    }

    @Override
    public void add(int key, int value) {
        int idx = indexOfKey(key);

        if (idx == -1) {
            if (keyCount == keys.length) return; //diccionario lleno
            idx = keyCount++;
            keys[idx] = key;
            sizes[idx] = 0;
        }

        //agregar valor en esa clave
        if (sizes[idx] < values[idx].length) {
            values[idx][sizes[idx]++] = value;
        }
    }

    @Override
    public void remove(int key) {
        int idx = indexOfKey(key);
        if (idx == -1) return;

        //mover la última clave encima de esta
        keyCount--;
        keys[idx] = keys[keyCount];
        values[idx] = values[keyCount];
        sizes[idx] = sizes[keyCount];
    }

    @Override
    public void remove(int key, int value) {
        int idx = indexOfKey(key);
        if (idx == -1) return;

        int pos = indexOfValue(values[idx], sizes[idx], value);
        if (pos == -1) return;

        //compactar los valores
        sizes[idx]--;
        values[idx][pos] = values[idx][sizes[idx]];
    }

    @Override
    public int[] getKeys() {
        int[] res = new int[keyCount];
        for (int i = 0; i < keyCount; i++) res[i] = keys[i];
        return res;
    }

    @Override
    public int[] get(int key) {
        int idx = indexOfKey(key);
        if (idx == -1) return new int[0];

        int[] res = new int[sizes[idx]];
        for (int i = 0; i < sizes[idx]; i++) res[i] = values[idx][i];
        return res;
    }

    @Override
    public boolean isEmpty() {
        return keyCount == 0;
    }

    //Helpers privados

    private int indexOfKey(int key) {
        for (int i = 0; i < keyCount; i++) {
            if (keys[i] == key) return i;
        }
        return -1;
    }

    private int indexOfValue(int[] arr, int len, int value) {
        for (int i = 0; i < len; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }
}
