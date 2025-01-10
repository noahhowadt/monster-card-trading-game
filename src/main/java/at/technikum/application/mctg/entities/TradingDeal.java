package at.technikum.application.mctg.entities;

import java.util.UUID;

public class TradingDeal {
    private UUID id;
    private UUID cardToTrade;
    private CardType type;
    private float minimumDamage;
    private UUID userId;

    public TradingDeal() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCardToTrade() {
        return cardToTrade;
    }

    public void setCardToTrade(UUID cardToTrade) {
        this.cardToTrade = cardToTrade;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public float getMinimumDamage() {
        return minimumDamage;
    }

    public void setMinimumDamage(float minimumDamage) {
        this.minimumDamage = minimumDamage;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
