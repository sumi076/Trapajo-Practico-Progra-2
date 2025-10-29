package resto.tda;

public class PriorityQueueArray implements PriorityQueueTDA {
    private final int[] values;
    private final int[] prios;
    private int size = 0;

    public PriorityQueueArray(int capacity) {
        this.values = new int[capacity];
        this.prios  = new int[capacity];
    }

    @Override
    public int getElement() {
        if (size == 0) return -1;
        int idx = indexOfBest();
        return values[idx];
    }

    @Override
    public int getPriority() {
        if (size == 0) return -1;
        int idx = indexOfBest();
        return prios[idx];
    }

    @Override
    public void add(int value, int priority) {
        values[size] = value;
        prios[size]  = priority;
        size++;
    }

    @Override
    public void remove() {
        if (size == 0) return;
        int idx = indexOfBest();
        // compactar: mover el último a la posición eliminada
        size--;
        values[idx] = values[size];
        prios[idx]  = prios[size];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private int indexOfBest() {
        // “mejor” = prioridad más baja (1 mejor que 2)
        int bestIdx = 0;
        int bestP = prios[0];
        for (int i = 1; i < size; i++) {
            if (prios[i] < bestP) {
                bestP = prios[i];
                bestIdx = i;
            }
        }
        return bestIdx;
    }
}
