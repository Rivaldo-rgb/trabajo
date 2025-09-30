import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

// Cliente simple que se conecta al servidor y permite enviar comandos por consola
public class GameClient {
    public static void main(String[] args) throws IOException {
        String host = "localhost"; // cambiar si el server está en otra máquina
        int port = 5000;

        try (
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in)
        ) {
            // Hilo lector para mensajes del servidor
            new Thread(() -> in.lines().forEach(s -> System.out.println("[SERVER] " + s))).start();

            System.out.print("Tu nombre: ");
            out.println("NAME:" + sc.nextLine()); // enviamos el nombre al servidor

            // Menú funcional de selección de personajes
            List<String> personajes = List.of("Vampiro", "Mago", "Caballero");
            personajes.forEach(p -> System.out.println((personajes.indexOf(p) + 1) + ". " + p));
            System.out.print("Opción (1-3): ");
            int opcion = Optional.of(sc.nextLine().trim())
                .map(Integer::parseInt)
                .filter(i -> i >= 1 && i <= personajes.size())
                .orElse(3);
            out.println("CHARACTER:" + personajes.get(opcion - 1)); // enviamos el personaje al servidor

            // Procesamiento funcional de comandos
            Map<String, Consumer<PrintWriter>> comandos = new HashMap<>();
            comandos.put("ATTACK", o -> o.println("ATTACK"));
            comandos.put("STATUS", o -> o.println("STATUS"));
            comandos.put("HEAL", o -> o.println("HEAL"));

            // Bucle principal: leer comandos desde la consola y enviarlos al servidor
            while (true) {
                System.out.print("Comando (ATTACK/STATUS/EXIT/HEAL): ");
                String cmd = sc.nextLine().trim().toUpperCase();
                if ("EXIT".equals(cmd)) break;
                comandos.getOrDefault(cmd, o -> o.println(cmd)).accept(out);
            }
        }
    }
}