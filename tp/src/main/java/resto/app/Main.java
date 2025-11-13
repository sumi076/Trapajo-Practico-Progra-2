package resto.app;

import resto.domain.*;
import resto.service.*;
import resto.storage.*;
import resto.tda.GraphWeightedMatrix;
import resto.tda.DiccionarioMTDA;

import java.util.Scanner;

public class Main {

    private static final int UNIT_METERS = 10;

    public static void main(String[] args) {
        ArrayPlatos platosStore = new ArrayPlatos(100);
        ArrayRepartidores repsStore = new ArrayRepartidores(50);
        ArrayPedidos pedidosStore = new ArrayPedidos(1000);

        CatalogoService catalogo = new CatalogoServiceImpl(platosStore);
        RepartidorService repartidores = new RepartidorServiceImpl(repsStore);
        PedidoService pedidos = new PedidoServiceImpl(pedidosStore, platosStore, repsStore);

        cargarPlatos(platosStore);
        cargarRepartidores(repsStore);
        cargarPedidosDemo(pedidos); // Demo actualizado para cargar datos previos con los TDA

        GraphWeightedMatrix grafo = cargarMapaGrafo();
        RouterService router = new RouterServiceBFS(pedidosStore, repsStore, grafo, UNIT_METERS);

        Scanner sc = new Scanner(System.in);
        int op;
        do {
            System.out.println("\n=== RESTO - Gestión de Pedidos ===");
            System.out.println("1) Registrar pedido");
            System.out.println("2) Ver siguiente pendiente (VIP tienen prioridad)");
            System.out.println("3) Preparar siguiente pedido");
            System.out.println("4) Marcar pedido LISTO");
            System.out.println("5) Asignar pedido a repartidor");
            System.out.println("6) Marcar pedido ENTREGADO");
            System.out.println("7) Reportes");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            op = leerEntero(sc);

            switch (op) {
                case 1 -> registrarPedido(sc, catalogo, pedidos, grafo);
                case 2 -> {
                    Pedido p = pedidos.verSiguientePendiente();
                    System.out.println(p == null ? "No hay pendientes."
                            : "Pendiente -> ID " + p.id + " / " + p.cliente + " / " + p.prioridad);
                }
                case 3 -> {
                    Pedido prep = pedidos.prepararSiguiente();
                    System.out.println(prep == null ? "No había pendientes."
                            : "Pasó a EN_PREPARACION -> ID " + prep.id);
                }
                case 4 -> {
                    System.out.print("ID de pedido a marcar LISTO: ");
                    int idListo = leerEntero(sc);
                    Pedido p = pedidos.getPedido(idListo);
                    if (p == null) {
                        System.out.println("No existe un pedido con ese ID.");
                    } else {
                        pedidos.marcarListo(idListo);
                        System.out.println("Pedido " + idListo + " marcado LISTO (si estaba en preparación).");
                    }
                }
                case 5 -> {
                    // Elegir repartidor disponible (Primero que este libre)
                    int repId = repartidores.siguienteDisponibleId();
                    if (repId == -1) {
                        System.out.println("No hay repartidores disponibles.");
                        break;
                    }

                    // Elegir pedido LISTO + DOMICILIO más cercano por BFS (metros)
                    int pedidoId = router.elegirProximoPedido(repId);
                    if (pedidoId == -1) {
                        System.out.println("No hay pedidos LISTO a domicilio.");
                        break;
                    }

                    // Asignar
                    pedidos.asignarPedidoARepartidor(pedidoId, repId);
                    System.out.println("Asignado pedido " + pedidoId + " al repartidor " + repId + ".");
                }
                case 6 -> {
                    System.out.print("ID de pedido a marcar ENTREGADO: ");
                    int idEnt = leerEntero(sc);
                    Pedido p = pedidos.getPedido(idEnt);
                    if (p == null) {
                        System.out.println("No existe.");
                        break;
                    }
                    int repId = p.repartidorId;
                    pedidos.marcarEntregado(idEnt);
                    if (repId != -1) {
                        router.actualizarPosicionRepartidor(repId, p.destinoVertexId);
                    }
                    System.out.println("OK (si existía).");
                }
                case 7 -> mostrarReportes(pedidos, repartidores, catalogo, grafo);
                case 0 -> System.out.println("Fin.");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    //helpers
    private static int leerEntero(Scanner sc) {
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Ingrese un número: ");
        }
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
        //DOMICILIO tienen destinoVertexId entre 1 y 5 (barrios del grafo)

        pedidos.crearPedidoConDestino("Juan",
                new int[]{1, 5},                  // Milanesa + Papas
                TipoPedido.DOMICILIO,
                Prioridad.NORMAL,
                1);                                // BARRIO_A

        pedidos.crearPedidoConDestino("María",
                new int[]{3},                      // Fugazzeta
                TipoPedido.LLEVAR,
                Prioridad.VIP,
                0);                                // sin barrio (retira en local)

        pedidos.crearPedidoConDestino("Santiago",
                new int[]{2, 5, 5},                // Hamburguesa + 2x Papas
                TipoPedido.DOMICILIO,
                Prioridad.NORMAL,
                4);                                // BARRIO_D

        pedidos.crearPedidoConDestino("Julia",
                new int[]{6},                      // Docena de empanadas
                TipoPedido.LLEVAR,
                Prioridad.NORMAL,
                0);

        pedidos.crearPedidoConDestino("María",
                new int[]{4, 1},                   // Ensalada + Milanesa
                TipoPedido.DOMICILIO,
                Prioridad.VIP,
                3);                                // BARRIO_C

        pedidos.crearPedidoConDestino("Ana",
                new int[]{2, 2},                   // 2 Hamburguesas
                TipoPedido.DOMICILIO,
                Prioridad.NORMAL,
                2);                                // BARRIO_B

        pedidos.crearPedidoConDestino("Luis",
                new int[]{1},                      // Milanesa
                TipoPedido.LLEVAR,
                Prioridad.VIP,
                0);

        //Pasamos 3 pedidos de PENDIENTE -> EN_PREPARACION
        pedidos.prepararSiguiente();  //saca el siguiente de la cola con prioridad y lo encola en cocina
        pedidos.prepararSiguiente();
        pedidos.prepararSiguiente();

        //De esos en cocina, pasamos 2 a LISTO
        //marcarListo ignora el id si la cola de cocina no está vacía
        pedidos.marcarListo(0);
        pedidos.marcarListo(0);

        //Resultado al iniciar el programa:
        //Algunos pedidos siguen PENDIENTES (en la PriorityQueueTDA).
        //pedido queda EN_PREPARACION (en la QueueTDA de cocina).
        //pedidos quedan LISTO (uno seguro DOMICILIO, ideal para probar el router BFS).
    }

    private static void registrarPedido(Scanner sc, CatalogoService catalogo, PedidoService pedidos, GraphWeightedMatrix g) {
        sc.nextLine();
        System.out.print("Cliente: ");
        String cliente = sc.nextLine();

        int tipoInt;
        do {
            System.out.print("Tipo (1=LLEVAR, 2=DOMICILIO): ");
            tipoInt = leerEntero(sc);
            if (tipoInt != 1 && tipoInt != 2) {
                System.out.println("Opción inválida, ingrese 1 o 2.");
            }
        } while (tipoInt != 1 && tipoInt != 2);
        TipoPedido tipo = (tipoInt == 2) ? TipoPedido.DOMICILIO : TipoPedido.LLEVAR;

        int prioInt;
        do {
            System.out.print("Prioridad (1=VIP, 2=NORMAL): ");
            prioInt = leerEntero(sc);
            if (prioInt != 1 && prioInt != 2) {
                System.out.println("Opción inválida, ingrese 1 o 2.");
            }
        } while (prioInt != 1 && prioInt != 2);
        Prioridad prioridad = (prioInt == 1) ? Prioridad.VIP : Prioridad.NORMAL;

        System.out.println("Catálogo de platos:");
        for (Plato pl : catalogo.listarPlatos()) {
            System.out.println(pl.id + ") " + pl.nombre + " - $" + pl.precio);
        }

        System.out.print("¿Cuántos ítems va a cargar? ");
        int cant = leerEntero(sc);
        int[] platosIds = new int[cant];
        for (int i = 0; i < cant; i++) {
            Plato plSel = null;
            int idPlato;
            do {
                System.out.print("ID plato #" + (i + 1) + ": ");
                idPlato = leerEntero(sc);
                plSel = catalogo.obtener(idPlato);
                if (plSel == null) {
                    System.out.println("No existe un plato con ese ID. Intente nuevamente.");
                }
            } while (plSel == null);
            platosIds[i] = idPlato;
        }

        int destinoVertexId = 0; // RESTAURANTE por defecto
        if (tipo == TipoPedido.DOMICILIO) {
            // Mostrar barrios
            System.out.println("Destinos disponibles:");
            for (int i = 0; i < g.size(); i++) {
                System.out.println(i + ") " + g.nameOf(i));
            }
            do {
                System.out.print("Ingrese ID de barrio destino (entre 1 y " + (g.size() - 1) + "): ");
                destinoVertexId = leerEntero(sc);
                if (destinoVertexId <= 0 || destinoVertexId >= g.size()) {
                    System.out.println("ID inválido. Debe ser un barrio distinto de RESTAURANTE.");
                }
            } while (destinoVertexId <= 0 || destinoVertexId >= g.size());
        }

        int id = pedidos.crearPedidoConDestino(cliente, platosIds, tipo, prioridad, destinoVertexId);
        System.out.println("Pedido creado con ID " + id + " (destino=" + g.nameOf(destinoVertexId) + ")");
    }

    private static void mostrarReportes(PedidoService pedidos,
                                        RepartidorService reps,
                                        CatalogoService catalogo,
                                        GraphWeightedMatrix grafo) {

        System.out.println("\n--- Reportes ---");
        System.out.println("Pendientes: " + pedidos.pendientes());
        System.out.println("Finalizados: " + pedidos.finalizados());

        // Entregas por repartidor (simple, por cantidad)
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

        //Pedidos agrupados por REPARTIDOR
        System.out.println("\n--- Pedidos agrupados por REPARTIDOR ---");
        DiccionarioMTDA porRep = pedidos.agruparPorRepartidor();
        if (porRep.isEmpty()) {
            System.out.println("No hay pedidos con repartidor asignado.");
        } else {
            int[] repIds = porRep.getKeys();
            for (int i = 0; i < repIds.length; i++) {
                int repId = repIds[i];
                String nombreRep = nombreRepartidorPorId(reps, repId);
                int[] pedidosIds = porRep.get(repId);

                System.out.print("Repartidor " + repId + " (" + nombreRep + ") -> pedidos: ");
                for (int j = 0; j < pedidosIds.length; j++) {
                    System.out.print(pedidosIds[j]);
                    if (j < pedidosIds.length - 1) System.out.print(", ");
                }
                System.out.println();
            }
        }

        //Pedidos agrupados por BARRIO destino (DOMICILIO)
        System.out.println("\n--- Pedidos agrupados por BARRIO destino (DOMICILIO) ---");
        DiccionarioMTDA porBarrio = pedidos.agruparPorBarrio();
        if (porBarrio.isEmpty()) {
            System.out.println("No hay pedidos a domicilio.");
        } else {
            int[] barrios = porBarrio.getKeys();
            for (int i = 0; i < barrios.length; i++) {
                int barrioId = barrios[i];
                String nombreBarrio = grafo.nameOf(barrioId);
                int[] pedidosIds = porBarrio.get(barrioId);

                System.out.print("Barrio " + barrioId + " (" + nombreBarrio + ") -> pedidos: ");
                for (int j = 0; j < pedidosIds.length; j++) {
                    System.out.print(pedidosIds[j]);
                    if (j < pedidosIds.length - 1) System.out.print(", ");
                }
                System.out.println();
            }
        }

        // Pedidos agrupados por CLIENTE (clave = índice en nombresClientes, valores = ids de pedidos)
        System.out.println("\n--- Pedidos agrupados por CLIENTE ---");
        int total = pedidos.totalPedidos();
        if (total == 0) {
            System.out.println("No hay pedidos.");
        } else {
            String[] nombresClientes = new String[total];
            DiccionarioMTDA porCliente = pedidos.agruparPorCliente(nombresClientes);

            if (porCliente.isEmpty()) {
                System.out.println("No hay pedidos.");
            } else {
                int[] indicesCliente = porCliente.getKeys();
                for (int i = 0; i < indicesCliente.length; i++) {
                    int idx = indicesCliente[i];
                    String nombre = nombresClientes[idx];
                    int[] pedidosIds = porCliente.get(idx);

                    System.out.print("Cliente " + nombre + " -> pedidos: ");
                    for (int j = 0; j < pedidosIds.length; j++) {
                        System.out.print(pedidosIds[j]);
                        if (j < pedidosIds.length - 1) System.out.print(", ");
                    }
                    System.out.println();
                }
            }
        }
    }

    private static String nombreRepartidorPorId(RepartidorService reps, int id) {
        Repartidor[] arr = reps.listar();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].id == id) return arr[i].nombre;
        }
        return "#" + id;
    }

    //GRAFO (restaurante + 5 barrios) en metros
    private static GraphWeightedMatrix cargarMapaGrafo() {
        GraphWeightedMatrix g = new GraphWeightedMatrix(10);
        int RESTO = g.addVertex("RESTAURANTE"); // 0
        int A = g.addVertex("BARRIO_A");        // 1
        int B = g.addVertex("BARRIO_B");        // 2
        int C = g.addVertex("BARRIO_C");        // 3
        int D = g.addVertex("BARRIO_D");        // 4
        int E = g.addVertex("BARRIO_E");        // 5

        //Todos múltiplos de 10 (unidad del BFS expandido)
        g.addEdgeMeters(RESTO, A, 50);
        g.addEdgeMeters(A, B, 10);
        g.addEdgeMeters(B, C, 30);
        g.addEdgeMeters(C, D, 20);
        g.addEdgeMeters(D, E, 10);
        g.addEdgeMeters(RESTO, E, 60);
        g.addEdgeMeters(A, E, 40);

        return g;
    }
}
