import java.util.*;
import java.util.stream.*;

public class StatsProcessor {

    // Clase inmutable para resultados de partidas
    public static final class MatchResults {
        private final String ganador;
        private final int dañoTotal;
        private final long duracionPartida; // en milisegundos

        public MatchResults(String ganador, int dañoTotal, long duracionPartida) {
            this.ganador = ganador;
            this.dañoTotal = dañoTotal;
            this.duracionPartida = duracionPartida;
        }

        public String getGanador() { return ganador; }
        public int getDañoTotal() { return dañoTotal; }
        public long getDuracionPartida() { return duracionPartida; }
    }

    // Ranking de jugadores por cantidad de victorias (funcional)
    public static Map<String, Long> rankingPorVictorias(List<MatchResults> partidas) {
        return partidas.stream()
                .collect(Collectors.groupingBy(
                        MatchResults::getGanador,
                        Collectors.counting()
                ));
    }

    // Ranking por daño total causado (funcional y paralelo)
    public static Map<String, Integer> rankingPorDañoTotal(List<MatchResults> partidas) {
        return partidas.parallelStream()
                .collect(Collectors.groupingBy(
                        MatchResults::getGanador,
                        Collectors.summingInt(MatchResults::getDañoTotal)
                ));
    }

    // Duración promedio de las partidas (funcional)
    public static double duracionPromedio(List<MatchResults> partidas) {
        return partidas.stream()
                .mapToLong(MatchResults::getDuracionPartida)
                .average()
                .orElse(0.0);
    }

    // Partida más corta (funcional)
    public static Optional<MatchResults> partidaMasCorta(List<MatchResults> partidas) {
        return partidas.stream()
                .min(Comparator.comparingLong(MatchResults::getDuracionPartida));
    }

    // Ejemplo de uso funcional
    public static void main(String[] args) {
        List<MatchResults> partidas = Arrays.asList(
            new MatchResults("Rivaldo", 120, 15000),
            new MatchResults("James", 90, 12000),
            new MatchResults("Rivaldo", 110, 18000),
            new MatchResults("James", 130, 9000),
            new MatchResults("Rivaldo", 140, 20000)
        );

        rankingPorVictorias(partidas)
            .forEach((k, v) -> System.out.println("Victorias de " + k + ": " + v));

        rankingPorDañoTotal(partidas)
            .forEach((k, v) -> System.out.println("Daño total de " + k + ": " + v));

        System.out.println("Duración promedio: " + (duracionPromedio(partidas) / 1000.0) + " segundos");

        partidaMasCorta(partidas)
            .ifPresent(p -> System.out.println("Partida más corta: Ganador " + p.getGanador() +
                ", Daño " + p.getDañoTotal() +
                ", Duración " + (p.getDuracionPartida() / 1000.0) + " segundos"));
    }

    // Recibes una lista de ClientHandler al terminar las partidas
    List<ClientHandler> handlers = ...;

    // Extraes los resultados funcionalmente
    List<MatchResults> resultados = handlers.stream()
        .map(ClientHandler::getMatchResults)
        .collect(Collectors.toList());

    // Procesas los resultados como antes
    Map<String, Long> rankingVictorias = StatsProcessor.rankingPorVictorias(resultados);
    Map<String, Integer> rankingDaño = StatsProcessor.rankingPorDañoTotal(resultados);
}
