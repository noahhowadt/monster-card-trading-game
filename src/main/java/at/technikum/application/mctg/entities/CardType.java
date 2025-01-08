package at.technikum.application.mctg.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CardType {
    MONSTER("monster"),
    SPELL("spell");

    private final String type;

    CardType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static CardType fromValue(String value) {
        for (CardType cardType : CardType.values()) {
            if (cardType.type.equalsIgnoreCase(value)) {
                return cardType;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
