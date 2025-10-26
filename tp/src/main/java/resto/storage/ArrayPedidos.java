package resto.storage;

import resto.domain.*;

public class ArrayPedidos {
    private final Pedido[] data;
    private int size = 0;

    public ArrayPedidos(int max) {
        this.data = new Pedido[max];
    }

    public void add(Pedido p) {
        data[size++] = p;
    }

    public Pedido getById(int id) {
        for (int i = 0; i < size; i++) {
            if (data[i].id == id) return data[i];
        }
        return null;
    }

    public Pedido[] all() {
        Pedido[] result = new Pedido[size];
        for (int i = 0; i < size; i++) result[i] = data[i];
        return result;
    }

    // Devuelve el siguiente pedido pendiente, dando prioridad a los VIP
    public Pedido findNextPendingByPriority() {
        Pedido normal = null;
        for (int i = 0; i < size; i++) {
            Pedido p = data[i];
            if (p.estado == EstadoPedido.PENDIENTE) {
                if (p.prioridad == Prioridad.VIP) return p;
                if (normal == null) normal = p;
            }
        }
        return normal;
    }

    public Pedido findFirstReadyForDelivery() {
        for (int i = 0; i < size; i++) {
            Pedido p = data[i];
            if (p.estado == EstadoPedido.LISTO && p.tipo == TipoPedido.DOMICILIO) return p;
        }
        return null;
    }

    public int countByEstado(EstadoPedido e) {
        int count = 0;
        for (int i = 0; i < size; i++) if (data[i].estado == e) count++;
        return count;
    }

    public int countEntregadosPorRepartidor(int repId) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i].estado == EstadoPedido.ENTREGADO && data[i].repartidorId == repId) count++;
        }
        return count;
    }

    public String topClientePorCantidad() {
        String[] nombres = new String[size];
        int[] cont = new int[size];
        int n = 0;
        for (int i = 0; i < size; i++) {
            String cli = data[i].cliente;
            int idx = indexOf(nombres, n, cli);
            if (idx == -1) {
                nombres[n] = cli;
                cont[n] = 1;
                n++;
            } else {
                cont[idx]++;
            }
        }
        int best = -1, bestIdx = -1;
        for (int i = 0; i < n; i++) {
            if (cont[i] > best) {
                best = cont[i];
                bestIdx = i;
            }
        }
        return bestIdx == -1 ? null : nombres[bestIdx];
    }

    public int topPlatoId() {
        int[] ids = new int[1024];
        int[] cont = new int[1024];
        int n = 0;
        for (int i = 0; i < size; i++) {
            int[] ps = data[i].platos;
            for (int id : ps) {
                int idx = indexOf(ids, n, id);
                if (idx == -1) {
                    ids[n] = id;
                    cont[n] = 1;
                    n++;
                } else {
                    cont[idx]++;
                }
            }
        }
        int best = -1, bestIdx = -1;
        for (int i = 0; i < n; i++) {
            if (cont[i] > best) {
                best = cont[i];
                bestIdx = i;
            }
        }
        return bestIdx == -1 ? -1 : ids[bestIdx];
    }

    private int indexOf(String[] arr, int len, String s) {
        for (int i = 0; i < len; i++) {
            if (arr[i].equals(s)) return i;
        }
        return -1;
    }

    private int indexOf(int[] arr, int len, int v) {
        for (int i = 0; i < len; i++) {
            if (arr[i] == v) return i;
        }
        return -1;
    }

    public int size() {
        return size;
    }
}
