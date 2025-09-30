public final class MatchResults {
    private final String ganador;
    private final int dañoTotal;
    private final long duracionPartida; // en milisegundos

    public MatchResults(String ganador, int dañoTotal, long duracionPartida) {
        this.ganador = ganador;
        this.dañoTotal = dañoTotal;
        this.duracionPartida = duracionPartida;
    }

    public String getGanador() {
        return ganador;
    }

    public int getDañoTotal() {
        return dañoTotal;
    }

    public long getDuracionPartida() {
        return duracionPartida;
    }

    // Método puro para mostrar el resultado
    public String mostrarResultado() {
        return String.format(
            "Ganador: %s\nDaño total: %d\nDuración de la partida: %.2f segundos",
            ganador, dañoTotal, duracionPartida / 1000.0
        );
    }
}
