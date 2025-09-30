public class GamePlayer {
    private final String name;
    private final String character;
    private final int maxHp;
    private final int hp;
    private final int damage;

    public GamePlayer(String name, String character) {
        this.name = name;
        this.character = character;
        if (character.equalsIgnoreCase("caballero")) {
            this.maxHp = 150;
            this.damage = 20;
        } else if (character.equalsIgnoreCase("mago")) {
            this.maxHp = 100;
            this.damage = 10;
        } else if (character.equalsIgnoreCase("vampiro")) {
            this.maxHp = 120;
            this.damage = 15;
        } else {
            this.maxHp = 100;
            this.damage = 10;
        }
        this.hp = this.maxHp;
    }

    private GamePlayer(String name, String character, int maxHp, int hp, int damage) {
        this.name = name;
        this.character = character;
        this.maxHp = maxHp;
        this.hp = hp;
        this.damage = damage;
    }

    public String getName() { return name; }
    public String getCharacter() { return character; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }

    // Devuelve un nuevo GamePlayer con la vida curada
    public GamePlayer heal(int amount) {
        int newHp = Math.min(maxHp, hp + amount);
        return new GamePlayer(name, character, maxHp, newHp, damage);
    }

    // Devuelve un nuevo GamePlayer con la vida reducida
    public GamePlayer takeDamage(int amount) {
        int newHp = Math.max(0, hp - amount);
        return new GamePlayer(name, character, maxHp, newHp, damage);
    }

    public boolean isAlive() {
        return hp > 0;
    }
}