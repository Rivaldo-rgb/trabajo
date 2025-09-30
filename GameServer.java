import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;

// Servidor simple que empareja a dos jugadores por batalla
public class GameServer {
    // Guardamos las sesiones activas (pares de handlers)
    private static final List<ClientHandler> waiting = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        // Puerto donde escucha el servidor
        int port = 5000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado en puerto " + port);

        while (true) {
            // Espera conexiones entrantes
            Socket clientSocket = serverSocket.accept();
            System.out.println("Nuevo cliente conectado: " + clientSocket.getRemoteSocketAddress());

            // Crea un handler para gestionar ese cliente en un hilo separado
            ClientHandler handler = new ClientHandler(clientSocket);
            handler.start(); // start() porque ClientHandler extiende Thread

            // Guardamos en la lista de espera para emparejar
            synchronized (waiting) {
                waiting.add(handler);

                // Emparejamiento funcional usando Stream
                if (waiting.size() >= 2) {
                    List<ClientHandler> pareja = waiting.stream().limit(2).collect(Collectors.toList());
                    waiting.removeAll(pareja);

                    pareja.get(0).setOpponent(pareja.get(1));
                    pareja.get(1).setOpponent(pareja.get(0));
                    pareja.forEach(h -> h.sendMessage("MATCH_START"));
                }
            }
        }
    }
}