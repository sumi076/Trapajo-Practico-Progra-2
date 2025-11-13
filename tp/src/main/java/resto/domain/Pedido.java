package resto.domain;

public class Pedido {
    public int id;
    public String cliente;
    public int[] platos;
    public TipoPedido tipo;
    public Prioridad prioridad;
    public EstadoPedido estado;
    public int repartidorId;
    public int destinoVertexId;

    public Pedido(int id, String cliente, int[] platos, TipoPedido tipo, Prioridad prioridad, int destinoVertexId) {
        this.id = id;
        this.cliente = cliente;
        this.platos = platos;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = EstadoPedido.PENDIENTE;
        this.repartidorId = -1;
        this.destinoVertexId = destinoVertexId;
    }

    public Pedido(int id, String cliente, int[] platos, TipoPedido tipo, Prioridad prioridad) {
        this(id, cliente, platos, tipo, prioridad, 0);
    }
}
