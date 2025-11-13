package resto.tda;
//diccionario simple
public interface DiccionarioTDA {
    int[] getKeys();   //devuelve una copia con las claves actuales
    int get(int key);  //pre: key existente
    void add(int key, int value);
    void remove(int key);
    boolean isEmpty();
}
