package resto.service;

import resto.domain.*;
import resto.storage.ArrayPedidos;
import resto.storage.ArrayRepartidores;
import resto.tda.GraphWeightedMatrix;

public class RouterServiceBFS implements RouterService {
    private final ArrayPedidos pedidos;
    private final ArrayRepartidores reps;
    private final GraphWeightedMatrix g;
    private final int unitMeters;

    public RouterServiceBFS(ArrayPedidos pedidos, ArrayRepartidores reps, GraphWeightedMatrix g, int unitMeters) {
        this.pedidos = pedidos;
        this.reps = reps;
        this.g = g;
        this.unitMeters = unitMeters;
    }

    public int elegirProximoPedido(int repId) {
        int pos = getPos(repId); if(pos==-1) return -1;
        Pedido[] all = pedidos.all();
        int bestId=-1, bestDist=Integer.MAX_VALUE;

        for(int i=0;i<all.length;i++){
            Pedido p = all[i];
            if(p==null) continue;
            if(p.estado==EstadoPedido.LISTO && p.tipo==TipoPedido.DOMICILIO){
                int d = g.bfsDistanceMeters(pos, p.destinoVertexId, unitMeters);
                if(d>=0 && d<bestDist){ bestDist=d; bestId=p.id; }
            }
        }
        return bestId;
    }

    public void actualizarPosicionRepartidor(int repId, int destinoVertexId) {
        Repartidor[] rs = reps.all();
        for(int i=0;i<rs.length;i++){
            if(rs[i].id==repId){ rs[i].posVertexId = destinoVertexId; return; }
        }
    }

    private int getPos(int repId){
        Repartidor[] rs = reps.all();
        for(int i=0;i<rs.length;i++) if(rs[i].id==repId) return rs[i].posVertexId;
        return -1;
    }
}
