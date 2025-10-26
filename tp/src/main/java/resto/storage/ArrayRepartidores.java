package resto.storage;

import resto.domain.Repartidor;

public class ArrayRepartidores {
    private final Repartidor[] data;
    private int size = 0;

    public ArrayRepartidores(int max) {
        this.data = new Repartidor[max];
    }

    public void add(Repartidor r) {
        data[size++] = r;
    }

    public Repartidor[] all() {
        Repartidor[] result = new Repartidor[size];
        for (int i = 0; i < size; i++) result[i] = data[i];
        return result;
    }

    public int nextDisponible() {
        for (int i = 0; i < size; i++) {
            if (data[i].disponible) return data[i].id;
        }
        return -1;
    }

    public void setDisponible(int id, boolean disponible) {
        for (int i = 0; i < size; i++) {
            if (data[i].id == id) {
                data[i].disponible = disponible;
                return;
            }
        }
    }

    public String nombreDe(int id) {
        for (int i = 0; i < size; i++) {
            if (data[i].id == id) return data[i].nombre;
        }
        return null;
    }

    public int size() {
        return size;
    }
}
