package resto.tda;
//diccionario multiple
public interface DiccionarioMTDA {
    int[] getKeys();
    int[] get(int key); //conjunto/lista de valores asociados a esa key
    void add(int key, int value);
    void remove(int key);
    void remove(int key, int value);
    boolean isEmpty();
}
