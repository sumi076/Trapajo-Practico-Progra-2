package resto.app;

import resto.domain.*;
import resto.service.*;
import resto.storage.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // --- storage (arreglos simples, fase 1) ---
        ArrayPlatos platosStore = new ArrayPlatos(100);
        ArrayRepartidores repsStore = new ArrayRepartidores(50);
        ArrayPedidos pedidosStore = new ArrayPedidos(1000);

        // --- services ---
        CatalogoService catalogo = new resto.service.CatalogoServiceImpl(platosStore);
        RepartidorService repartidores = new resto.service.RepartidorServiceImpl(repsStore);
        PedidoService pedidos = new resto.service.PedidoServiceImpl(pedidosStore, platosStore, repsStore);

        // --- datos iniciales ---
        cargarPlatos(platosStore);
        cargarRepartidores(repsStore);
        cargarPedidosDemo(pedidos); // 5 pedidos

        // --- menú ---
        Scanner sc = new Scanner(System.in);
        int op;
        do {
            System.out.println("\n=== RESTO - Gestión de Pedidos ===");
            System.out.println("1) Registrar pedido");
            System.out.println("2) Ver siguiente pendiente (prioridad)");
            System.out.println("3) Preparar siguiente pedido");
            System.out.println("4) Marcar pedido LISTO");
            System.out.println("5) Asignar pedido a repartidor");
            System.out.println("6) Marcar pedido ENTREGADO");
            System.out.println("7) Reportes");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            op = leerEntero(sc);

            switch (op) {
                case 1 -> registrarPedido(sc, catalogo, pedidos);
                case 2 -> {
                    Pedido p = pedidos.verSiguientePendiente();
                    System.out.println(p == null ? "No hay pendientes." :
                            "Pendiente -> ID " + p.id + " / " + p.cliente + " / " + p.prioridad);
                }
                case 3 -> {
                    Pedido prep = pedidos.prepararSiguiente();
                    System.out.println(prep == null ? "No había pendientes."
                            : "Pasó a EN_PREPARACION -> ID " + prep.id);
                }
                case 4 -> {
                    System.out.print("ID de pedido a marcar LISTO: ");
                    int idListo = leerEntero(sc);
                    pedidos.marcarListo(idListo);
                    System.out.println("OK (si existía).");
                }
                case 5 -> {
                    pedidos.asignarRepartidor();
                    System.out.println("Asignación realizada si había LISTO y repartidor disponible.");
                }
                case 6 -> {
                    System.out.print("ID de pedido a marcar ENTREGADO: ");
                    int idEnt = leerEntero(sc);
                    pedidos.marcarEntregado(idEnt);
                    System.out.println("OK (si existía).");
                }
                case 7 -> mostrarReportes(pedidos, repartidores, catalogo);
                case 0 -> System.out.println("Fin.");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    // ---------- helpers ----------
    private static int leerEntero(Scanner sc) {
        while (!sc.hasNextInt()) { sc.next(); System.out.print("Ingrese un número: "); }
        return sc.nextInt();
    }

    private static void cargarPlatos(ArrayPlatos store) {
        store.add(new Plato(1, "Milanesa Napolitana", 6000, 20));
        store.add(new Plato(2, "Hamburguesa de la Casa", 6000, 15));
        store.add(new Plato(3, "Fugazzeta", 15000, 18));
        store.add(new Plato(4, "Plato de Ensalada", 4200, 5));
        store.add(new Plato(5, "Porcion de Papas Fritas", 3000, 8));
        store.add(new Plato(6, "Docena de Empanadas", 12000, 20));
    }

    private static void cargarRepartidores(ArrayRepartidores store) {
        store.add(new Repartidor(1, "Ana", true));
        store.add(new Repartidor(2, "Luis", true));
        store.add(new Repartidor(3, "Carla", true));
        store.add(new Repartidor(4, "Diego", true));
        store.add(new Repartidor(5, "Rocío", true));
        store.add(new Repartidor(6, "Nico", true));
        store.add(new Repartidor(7, "Sofi", true));
        store.add(new Repartidor(8, "Marcos", true));
        store.add(new Repartidor(9, "Lola", true));
        store.add(new Repartidor(10, "Pablo", true));
    }

    private static void cargarPedidosDemo(PedidoService pedidos) {
        pedidos.crearPedido("Juan", new int[]{1, 5}, TipoPedido.DOMICILIO, Prioridad.NORMAL);
        pedidos.crearPedido("María", new int[]{3}, TipoPedido.LLEVAR, Prioridad.VIP);
        pedidos.crearPedido("Santiago", new int[]{2, 5, 5}, TipoPedido.DOMICILIO, Prioridad.NORMAL);
        pedidos.crearPedido("Julia", new int[]{6}, TipoPedido.LLEVAR, Prioridad.NORMAL);
        pedidos.crearPedido("María", new int[]{4, 1}, TipoPedido.DOMICILIO, Prioridad.VIP);
    }

    private static void registrarPedido(Scanner sc, CatalogoService catalogo, PedidoService pedidos) {
        sc.nextLine(); // limpiar buffer
        System.out.print("Cliente: ");
        String cliente = sc.nextLine();

        System.out.print("Tipo (1=LLEVAR, 2=DOMICILIO): ");
        TipoPedido tipo = (leerEntero(sc) == 2) ? TipoPedido.DOMICILIO : TipoPedido.LLEVAR;

        System.out.print("Prioridad (1=VIP, 2=NORMAL): ");
        Prioridad prioridad = (leerEntero(sc) == 1) ? Prioridad.VIP : Prioridad.NORMAL;

        System.out.println("Catálogo de platos:");
        for (Plato pl : catalogo.listarPlatos()) {
            System.out.println(pl.id + ") " + pl.nombre + " - $" + pl.precio);
        }

        System.out.print("¿Cuántos ítems va a cargar? ");
        int cant = leerEntero(sc);
        int[] platosIds = new int[cant];
        for (int i = 0; i < cant; i++) {
            System.out.print("ID plato #" + (i + 1) + ": ");
            platosIds[i] = leerEntero(sc);
        }

        int id = pedidos.crearPedido(cliente, platosIds, tipo, prioridad);
        System.out.println("Pedido creado con ID " + id);
    }

    private static void mostrarReportes(PedidoService pedidos, RepartidorService reps, CatalogoService catalogo) {
        System.out.println("\n--- Reportes ---");
        System.out.println("Pendientes: " + pedidos.pendientes());
        System.out.println("Finalizados: " + pedidos.finalizados());

        for (Repartidor r : reps.listar()) {
            int c = pedidos.repartosPor(r.id);
            if (c > 0) System.out.println("Entregas de " + r.nombre + ": " + c);
        }

        String topCli = pedidos.clienteTop();
        System.out.println("Cliente con más pedidos: " + (topCli == null ? "-" : topCli));

        int idPlato = pedidos.platoMasPedidoId();
        if (idPlato == -1) System.out.println("Plato más pedido: -");
        else {
            Plato pl = catalogo.obtener(idPlato);
            System.out.println("Plato más pedido: " + (pl == null ? ("#" + idPlato) : pl.nombre));
        }
    }
}
