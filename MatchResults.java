public record MatchResults(String winner, int damage, int duration) {
    @Override
    public String toString() {
        return "🏆 MatchResult: Winner=" + winner + ", Damage=" + damage + ", Duration=" + duration + "s";
    }
}