package resto.tda;

public interface QueueTDA {
    void add(int value);   //encola al final
    void remove();         //desencola el primero
    int getFirst();        //devuelve el primer elemento (sin sacarlo)
    boolean isEmpty();     //true si está vacía
}
