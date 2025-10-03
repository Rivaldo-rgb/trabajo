public class GamePlayer {
    private final String name;
    private final String character;
    private final int maxHp;
    private int hp;
    private final int damage;

    public GamePlayer(String name, String character) {
        this.name = name;
        this.character = character.toLowerCase();

        switch (this.character) {
            case "caballero" -> { this.maxHp = 150; this.damage = 20; }
            case "mago"      -> { this.maxHp = 100; this.damage = 10; }
            case "vampiro"   -> { this.maxHp = 120; this.damage = 15; }
            default          -> { this.maxHp = 100; this.damage = 10; }
        }
        this.hp = this.maxHp;
    }

    public String getName() { return name; }
    public String getCharacter() { return character; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }

    public void heal(int amount) { hp = Math.min(maxHp, hp + amount); }
    public void takeDamage(int amount) { hp -= amount; }
    public boolean isAlive() { return hp > 0; }
}