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
}
