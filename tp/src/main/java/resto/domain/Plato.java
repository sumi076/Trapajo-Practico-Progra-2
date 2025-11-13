package resto.domain;

public class Plato {
    public int id;
    public String nombre;
    public int precio;
    public int tiempoPrep;

    public Plato(int id, String nombre, int precio, int tiempoPrep) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.tiempoPrep = tiempoPrep;
    }

    @Override
    public String toString() {
        return "Plato{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", tiempoPrep=" + tiempoPrep +
                '}';
    }
}
