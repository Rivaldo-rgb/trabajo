import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

// Servidor simple que empareja a dos jugadores por batalla
public class GameServer {
    // Lista concurrente para jugadores esperando
    private static final List<ClientHandler> waiting = new CopyOnWriteArrayList<>();
    // Resultados acumulados
    private static final List<MatchResults> resultados = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado en puerto " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();

                emparejar(handler);
            }
        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }

    private static void emparejar(ClientHandler handler) {
        waiting.add(handler);

        // Intentar formar pares
        if (waiting.size() >= 2) {
            List<ClientHandler> par = waiting.stream()
                                             .limit(2)
                                             .collect(Collectors.toList());

            waiting.removeAll(par);

            ClientHandler a = par.get(0);
            ClientHandler b = par.get(1);

            a.setOpponent(b);
            b.setOpponent(a);

            a.sendMessage("MATCH_START contra " + Optional.ofNullable(b.getPlayerName()).orElse("Jugador"));
            b.sendMessage("MATCH_START contra " + Optional.ofNullable(a.getPlayerName()).orElse("Jugador"));
        }
    }

    public static synchronized void addMatchResult(MatchResults result) {
        resultados.add(result);
        System.out.println(result);

        StatsProcessor stats = new StatsProcessor(resultados);

        // Declarativo con Stream
        Stream.of(
            "📊 Ranking actual: " + stats.getRanking(),
            "📊 Promedio daño: " + stats.getPromedioDaño(),
            "📊 Promedio duración: " + stats.getPromedioDuracion() + "s"
        ).forEach(System.out::println);
    }
}