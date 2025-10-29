package resto.tda;

public interface PriorityQueueTDA {
    // Devuelve el primer elemento (sin quitarlo).
    int getElement();

    // Devuelve la prioridad del primer elemento.
    int getPriority();

    // Agrega un elemento con una prioridad asociada.
    void add(int value, int priority);

    // Elimina el primer elemento (el de mayor prioridad).
    void remove();

    // Indica si la estructura no tiene elementos.
    boolean isEmpty();
}
