package resto.tda;

public class QueueArray implements QueueTDA {

    private int[] data;
    private int ini;
    private int fin;
    private int cantidad;

    public QueueArray(int max) {
        data = new int[max];
        ini = 0;
        fin = 0;
        cantidad = 0;
    }

    @Override
    public void add(int value) {
        if (cantidad == data.length) return;   //cola llena, no encola
        data[fin] = value;
        fin = (fin + 1) % data.length;
        cantidad++;
    }

    @Override
    public void remove() {
        if (cantidad == 0) return;            //cola vacía, no hace nada
        ini = (ini + 1) % data.length;
        cantidad--;
    }

    @Override
    public int getFirst() {
        if (cantidad == 0) return -1;
        return data[ini];
    }

    @Override
    public boolean isEmpty() {
        return cantidad == 0;
    }
}
