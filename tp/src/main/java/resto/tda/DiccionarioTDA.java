package resto.tda;

public interface DiccionarioTDA {
    // por simplicidad, usamos int->int (clave->valor)
    int[] getKeys();   // devuelve una copia con las claves actuales
    int get(int key);  // pre: key existente
    void add(int key, int value);
    void remove(int key);
    boolean isEmpty();
}
