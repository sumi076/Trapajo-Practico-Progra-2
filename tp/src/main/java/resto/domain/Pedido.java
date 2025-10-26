package resto.domain;

public class Pedido {
    public int id;
    public String cliente;
    public int[] platos; // ids de los platos
    public TipoPedido tipo;
    public Prioridad prioridad;
    public EstadoPedido estado;
    public int repartidorId; // -1 si no asignado

    public Pedido(int id, String cliente, int[] platos, TipoPedido tipo, Prioridad prioridad) {
        this.id = id;
        this.cliente = cliente;
        this.platos = platos;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = EstadoPedido.PENDIENTE;
        this.repartidorId = -1;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", cliente='" + cliente + '\'' +
                ", tipo=" + tipo +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                ", repartidorId=" + repartidorId +
                '}';
    }
}
