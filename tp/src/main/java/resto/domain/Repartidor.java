package resto.domain;

public class Repartidor {
    public int id;
    public String nombre;
    public boolean disponible;

    public Repartidor(int id, String nombre, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Repartidor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}
