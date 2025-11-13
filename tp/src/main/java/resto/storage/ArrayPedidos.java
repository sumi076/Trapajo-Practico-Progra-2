package resto.storage;

import resto.domain.*;
import resto.tda.DiccionarioTDA;
import resto.tda.DiccionarioSimpleArray;
import resto.tda.DiccionarioMTDA;
import resto.tda.DiccionarioMultipleArray;

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

    //Devuelve el siguiente pedido pendiente dando prioridad a los VIP
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
        if (size == 0) return null;

        //obtener lista de clientes únicos y mapearlos a índices
        String[] nombres = new String[size];
        int n = 0;
        for (int i = 0; i < size; i++) {
            String cli = data[i].cliente;
            int idx = indexOf(nombres, n, cli);
            if (idx == -1) {
                nombres[n] = cli;
                n++;
            }
        }

        //diccionario: clave = índice de cliente, valor = cantidad de pedidos
        DiccionarioTDA dic = new DiccionarioSimpleArray(n);

        for (int i = 0; i < size; i++) {
            String cli = data[i].cliente;
            int idx = indexOf(nombres, n, cli);  // índice del cliente
            int actual = dic.get(idx);
            dic.add(idx, actual + 1);
        }

        if (dic.isEmpty()) return null;

        int[] claves = dic.getKeys();
        int mejorIdxCliente = claves[0];
        int mejorCant = dic.get(mejorIdxCliente);

        for (int i = 1; i < claves.length; i++) {
            int k = claves[i];
            int cant = dic.get(k);
            if (cant > mejorCant) {
                mejorCant = cant;
                mejorIdxCliente = k;
            }
        }

        return nombres[mejorIdxCliente];
    }

    public int topPlatoId() {
        //clave = idPlato, valor = cantidad pedida
        DiccionarioTDA dic = new DiccionarioSimpleArray(2000);

        for (int i = 0; i < size; i++) {
            Pedido p = data[i];
            int[] ps = p.platos;
            for (int j = 0; j < ps.length; j++) {
                int platoId = ps[j];
                int actual = dic.get(platoId);
                dic.add(platoId, actual + 1);
            }
        }

        if (dic.isEmpty()) return -1;

        int[] claves = dic.getKeys();
        int mejorPlato = claves[0];
        int mejorCant = dic.get(mejorPlato);

        for (int i = 1; i < claves.length; i++) {
            int k = claves[i];
            int cant = dic.get(k);
            if (cant > mejorCant) {
                mejorCant = cant;
                mejorPlato = k;
            }
        }
        return mejorPlato;
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

 //Agrupoamiento de pedidos con DiccionarioMTDA
    //Pedidos agrupados por REPARTIDOR (clave = idRepartidor, valores = ids de pedidos)
    public DiccionarioMTDA agruparPorRepartidor() {
        DiccionarioMTDA dic = new DiccionarioMultipleArray(50, size);
        for (int i = 0; i < size; i++) {
            Pedido p = data[i];
            if (p.repartidorId != -1) {
                dic.add(p.repartidorId, p.id);
            }
        }
        return dic;
    }

    //Pedidos agrupados por BARRIO destino (clave = destinoVertexId, valores = ids de pedidos)
    public DiccionarioMTDA agruparPorBarrio() {
        DiccionarioMTDA dic = new DiccionarioMultipleArray(10, size);
        for (int i = 0; i < size; i++) {
            Pedido p = data[i];
            if (p.tipo == TipoPedido.DOMICILIO) {
                dic.add(p.destinoVertexId, p.id);
            }
        }
        return dic;
    }

    //Pedidos agrupados por CLIENTE (clave = índice de cliente, valores = ids de pedidos)
    public DiccionarioMTDA agruparPorCliente(String[] nombresClientes) {
        //construir lista de clientes únicos
        int n = 0;
        for (int i = 0; i < size; i++) {
            String cli = data[i].cliente;
            int idx = indexOf(nombresClientes, n, cli);
            if (idx == -1) {
                nombresClientes[n] = cli;
                n++;
            }
        }

        //Diccionario múltiple: clave = índice de cliente, valores = ids de pedidos
        DiccionarioMTDA dic = new DiccionarioMultipleArray(n, size);

        for (int i = 0; i < size; i++) {
            String cli = data[i].cliente;
            int idx = indexOf(nombresClientes, n, cli);
            dic.add(idx, data[i].id);
        }

        return dic;
    }
}
