package resto.storage;

import resto.domain.Plato;

public class ArrayPlatos {
    private final Plato[] data;
    private int size = 0;

    public ArrayPlatos(int max) {
        this.data = new Plato[max];
    }

    public void add(Plato p) {
        data[size++] = p;
    }

    public Plato getById(int id) {
        for (int i = 0; i < size; i++) {
            if (data[i].id == id) return data[i];
        }
        return null;
    }

    public Plato[] all() {
        Plato[] result = new Plato[size];
        for (int i = 0; i < size; i++) result[i] = data[i];
        return result;
    }

    public int size() {
        return size;
    }
}
