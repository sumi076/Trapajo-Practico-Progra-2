package resto.service;

import resto.domain.Repartidor;
import resto.storage.ArrayRepartidores;

public class RepartidorServiceImpl implements RepartidorService {
    private final ArrayRepartidores reps;

    public RepartidorServiceImpl(ArrayRepartidores reps) {
        this.reps = reps;
    }

    @Override
    public Repartidor[] listar() {
        return reps.all();
    }

    @Override
    public int siguienteDisponibleId() {
        return reps.nextDisponible();
    }

    @Override
    public void marcarOcupado(int id) {
        reps.setDisponible(id, false);
    }

    @Override
    public void marcarDisponible(int id) {
        reps.setDisponible(id, true);
    }

    @Override
    public String nombreDe(int id) {
        return reps.nombreDe(id);
    }
}
