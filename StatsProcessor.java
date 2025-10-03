import java.util.*;
import java.util.stream.*;

public class StatsProcessor {
    private final List<MatchResults> results;

    public StatsProcessor(List<MatchResults> results) {
        this.results = results;
    }

    public Map<String, Long> getRanking() {
        return results.stream()
            .filter(r -> r.winner() != null)
            .collect(Collectors.groupingBy(
                MatchResults::winner,
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a,b) -> a,
                LinkedHashMap::new
            ));
    }

    public double getPromedioDaño() {
        return results.stream().collect(Collectors.averagingInt(MatchResults::damage));
    }

    public double getPromedioDuracion() {
        return results.stream().collect(Collectors.averagingInt(MatchResults::duration));
    }
}