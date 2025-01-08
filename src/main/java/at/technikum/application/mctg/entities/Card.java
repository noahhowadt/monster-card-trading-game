package at.technikum.application.mctg.entities;

import java.util.UUID;

public class Card {
    UUID id;
    String name;
    float damage;
    UUID packageId;
    UUID userId;

    public Card() {
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public CardType getType() {
        if (this.name.endsWith("Spell")) {
            return CardType.SPELL;
        } else {
            return CardType.MONSTER;
        }
    }

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public CardElement getElement() {
        // check if there are 2 uppercase letters in the name
        int upperCaseCount = 0;
        for (int i = 0; i < this.name.length(); i++) {
            if (Character.isUpperCase(this.name.charAt(i))) {
                upperCaseCount++;
            }
        }

        if (upperCaseCount != 2) {
            return CardElement.NORMAL;
        } else {
            String element = this.name.substring(0, 1);
            // find second uppercase letter
            for (int i = 1; i < this.name.length(); i++) {
                if (Character.isUpperCase(this.name.charAt(i))) break;
                element += this.name.charAt(i);
            }

            return CardElement.fromString(element);
        }
    }

    public CardMonster getMonster() {
        if (this.getType() != CardType.MONSTER) return null;

        String monsterStr = "";
        for (int i = this.name.length() - 1; i >= 0; i--) {
            if (Character.isUpperCase(this.name.charAt(i))) break;
            monsterStr = this.name.charAt(i) + monsterStr;
        }

        return CardMonster.fromString(monsterStr);
    }
}
