import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private ClientHandler opponent;
    private String playerName;
    private GamePlayer player;
    private final long startTime;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.startTime = System.currentTimeMillis();
    }

    public void setOpponent(ClientHandler opp) { this.opponent = opp; }
    public String getPlayerName() { return playerName; }
    public void sendMessage(String msg) { out.println(msg); }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                processCommand(line);
            }
        } catch (IOException e) {
            System.out.println("Error en handler: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void processCommand(String line) {
        Map<String, Runnable> commands = Map.of(
            "ATTACK", () -> Optional.ofNullable(opponent)
                                    .filter(o -> player != null && o.player != null)
                                    .ifPresent(this::doAttack),
            "HEAL",   () -> Optional.ofNullable(player).ifPresent(this::doHeal),
            "STATUS", () -> Optional.ofNullable(player).ifPresent(p ->
                        sendMessage("HP:" + p.getHp() + "/" + p.getMaxHp() +
                                    " | DAÑO:" + p.getDamage() +
                                    " | PERSONAJE:" + p.getCharacter()))
        );

        if (line.startsWith("NAME:")) {
            playerName = line.substring(5).trim();
            sendMessage("WELCOME " + playerName);
        } else if (line.startsWith("CHARACTER:")) {
            String character = line.substring(10).trim();
            player = new GamePlayer(playerName, character);
            sendMessage("CHARACTER_SELECTED " + character +
                " (HP:" + player.getMaxHp() + ", DAÑO:" + player.getDamage() + ")");
        } else {
            commands.entrySet().stream()
                .filter(e -> line.equalsIgnoreCase(e.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .ifPresentOrElse(Runnable::run, () -> sendMessage("UNKNOWN_CMD"));
        }
    }

    private void doAttack(ClientHandler opp) {
        synchronized (opp) {
            opp.player.takeDamage(player.getDamage());

            System.out.println("[SERVER] " + player.getName() + " (" + player.getCharacter() + ") atacó a "
                + opp.player.getName() + " (" + opp.player.getCharacter() + ") "
                + " | Daño: " + player.getDamage()
                + " | HP Oponente: " + opp.player.getHp());

            sendMessage("Has atacado a " + opp.player.getName() + " con " + player.getDamage() +
                " de daño. (HP oponente: " + opp.player.getHp() + ")");
            opp.sendMessage(player.getName() + " te atacó con " + player.getDamage() +
                " de daño. (Tu HP: " + opp.player.getHp() + ")");

            if (!opp.player.isAlive()) {
                endMatch(opp);
            }

            // Efecto mago
            if (player.getCharacter().equalsIgnoreCase("mago")) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (opp.player.isAlive()) {
                            opp.player.takeDamage(4);
                            opp.sendMessage("🔥 Te afectó la quemadura (-4 HP)");
                            sendMessage("🔥 Tu hechizo quemó a " + opp.player.getName() + " (-4 HP)");
                            if (!opp.player.isAlive()) endMatch(opp);
                        }
                    }
                }, 7000);
            }

            // Efecto vampiro
            if (player.getCharacter().equalsIgnoreCase("vampiro")) {
                int before = player.getHp();
                player.heal(5);
                int healed = player.getHp() - before;
                if (healed > 0) {
                    sendMessage("🧛 Recuperaste +" + healed + " HP (HP actual: " + player.getHp() + ")");
                }
            }
        }
    }

    private void doHeal(GamePlayer p) {
        if (p.getCharacter().equalsIgnoreCase("vampiro")) {
            sendMessage("HEAL_PROHIBIDO: El vampiro no puede usar HEAL.");
            return;
        }
        int before = p.getHp();
        p.heal(10);
        int healed = p.getHp() - before;
        if (healed > 0) {
            sendMessage("YOU_HEALED " + healed + " (HP:" + p.getHp() + ")");
            Optional.ofNullable(opponent)
                .map(o -> o.player)
                .ifPresent(op -> opponent.sendMessage(p.getName() + " HEALED " + healed + " (HP:" + p.getHp() + ")"));
        } else {
            sendMessage("HEAL_FAILED (HP al máximo)");
        }
    }

    private void endMatch(ClientHandler opp) {
        sendMessage("🏆 GANASTE contra " + opp.player.getName());
        opp.sendMessage("💀 PERDISTE contra " + player.getName());
        int duration = (int) ((System.currentTimeMillis() - startTime) / 1000);
        GameServer.addMatchResult(new MatchResults(player.getName(), player.getDamage(), duration));
    }
}