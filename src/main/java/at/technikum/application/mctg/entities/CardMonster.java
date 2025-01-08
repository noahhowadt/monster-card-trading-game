package at.technikum.application.mctg.entities;

public enum CardMonster {
    DRAGON,
    GOBLIN,
    ORK,
    WIZZARD,
    KNIGHT,
    KRAKEN,
    ELF;

    public static CardMonster fromString(String monster) {
        return switch (monster.toLowerCase()) {
            case "dragon" -> DRAGON;
            case "goblin" -> GOBLIN;
            case "ork" -> ORK;
            case "wizzard" -> WIZZARD;
            case "knight" -> KNIGHT;
            case "kraken" -> KRAKEN;
            case "elf" -> ELF;
            default -> null;
        };
    }
}
