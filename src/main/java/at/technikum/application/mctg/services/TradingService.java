package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.TradingDeal;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.exceptions.NotFoundException;
import at.technikum.application.mctg.repositories.CardRepository;
import at.technikum.application.mctg.repositories.DeckRepository;
import at.technikum.application.mctg.repositories.TradeRepository;

import java.util.ArrayList;
import java.util.UUID;

public class TradingService {
    private final TradeRepository tradeRepository;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public TradingService(TradeRepository tradeRepository, CardRepository cardRepository, DeckRepository deckRepository) {
        this.tradeRepository = tradeRepository;
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public void addTrade(User user, TradingDeal deal) {
        // check that card belongs to user
        Card card = this.cardRepository.getById(deal.getCardToTrade());
        if (!card.getUserId().equals(user.getId())) {
            throw new BadRequestException("Card does not belong to user");
        }

        // check that card is not in deck
        ArrayList<UUID> deck = this.deckRepository.getByUser(user);
        if (deck.contains(deal.getCardToTrade())) {
            throw new BadRequestException("Card is in deck");
        }

        // Add trade
        this.tradeRepository.createTrade(deal);
    }

    public ArrayList<TradingDeal> getAllTrades() {
        return this.tradeRepository.listAll();
    }

    public void carryOutTrade(User user, UUID tradeId, UUID cardId) {
        // get trade
        ArrayList<TradingDeal> deals = this.tradeRepository.listAll();
        TradingDeal deal = deals.stream().filter(d -> d.getId().equals(tradeId)).findFirst().orElseThrow(() -> new NotFoundException("Trade not found"));

        // get cards
        Card offeredCard = this.cardRepository.getById(cardId);
        Card tradeCard = this.cardRepository.getById(deal.getCardToTrade());

        // check that card belongs to user
        if (!offeredCard.getUserId().equals(user.getId())) {
            throw new BadRequestException("Card does not belong to user");
        }


        // check that card is of correct type
        if (!offeredCard.getType().equals(deal.getType())) {
            throw new BadRequestException("Card type does not match trade");
        }

        // check that card has minimum damage
        if (offeredCard.getDamage() < deal.getMinimumDamage()) {
            throw new BadRequestException("Card does not meet minimum damage requirement");
        }

        // delete trade
        this.tradeRepository.deleteById(deal.getId());

        // update cards
        this.cardRepository.switchOwner(offeredCard, tradeCard);
    }

    public void deleteTrade(User user, UUID tradeId) {
        // get trade
        TradingDeal deal = this.tradeRepository.getById(tradeId);

        // check that card belongs to user
        Card tradeCard = this.cardRepository.getById(deal.getCardToTrade());
        if (!tradeCard.getUserId().equals(user.getId())) {
            throw new BadRequestException("Card does not belong to user");
        }

        // delete trade
        this.tradeRepository.deleteById(deal.getId());
    }
}
