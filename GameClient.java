import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 5000;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            new Thread(() -> {
                in.lines().forEach(s -> System.out.println("[SERVER] " + s));
            }).start();

            System.out.print("Tu nombre: ");
            out.println("NAME:" + sc.nextLine());

            System.out.println("Selecciona tu personaje:\n1. Vampiro\n2. Mago\n3. Caballero");
            System.out.print("Opción (1-3): ");
            String personaje = switch (sc.nextLine().trim()) {
                case "1" -> "Vampiro";
                case "2" -> "Mago";
                case "3" -> "Caballero";
                default -> "Caballero";
            };
            out.println("CHARACTER:" + personaje);

            while (true) {
                System.out.print("Comando (ATTACK/STATUS/EXIT/HEAL): ");
                String cmd = sc.nextLine().trim();
                if (cmd.equalsIgnoreCase("EXIT")) break;
                out.println(cmd);
            }
        }
    }
}