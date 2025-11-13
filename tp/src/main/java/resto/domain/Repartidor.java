package resto.domain;

public class Repartidor {
    public int id;
    public String nombre;
    public boolean disponible;
    public int posVertexId;   //posición actual en el grafo

    public Repartidor(int id, String nombre, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.disponible = disponible;
        this.posVertexId = 0; //por defecto: RESTAURANTE
    }
}
