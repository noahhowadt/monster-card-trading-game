package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.TradingDeal;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.repositories.*;

import java.util.ArrayList;
import java.util.UUID;

public class CardService {
    private final PackageRepository packageRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final TradeRepository tradeRepository;

    public CardService(final PackageRepository packageRepository, final CardRepository cardRepository, final UserRepository userRepository, final DeckRepository deckRepository, final TradeRepository tradeRepository) {
        this.packageRepository = packageRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.tradeRepository = tradeRepository;
    }

    public void addPackage(ArrayList<Card> cards) {
        UUID packageId = UUID.randomUUID();
        packageRepository.createPackage(packageId);
        cardRepository.save(cards, packageId);
    }

    public void acquirePackage(User user) {
        if (user.getCoins() < 5) throw new BadRequestException("Not enough money to buy package");

        UUID packageId = packageRepository.getRandomPackage();
        cardRepository.acquireFromPackage(user, packageId);
        packageRepository.deletePackage(packageId);
        user.setCoins(user.getCoins() - 5);
        this.userRepository.save(user);
    }

    public ArrayList<Card> getByUser(User user) {
        return cardRepository.getByUser(user);
    }

    public void updateDeck(User user, ArrayList<UUID> deck) {
        if (deck.size() != 4) throw new BadRequestException("Deck must contain exactly 4 cards");
        ArrayList<Card> userCards = this.cardRepository.getByUser(user);
        for (UUID cardId : deck) {
            if (userCards.stream().noneMatch(card -> card.getId().equals(cardId))) {
                throw new BadRequestException("Card not owned by user");
            }
        }

        // check that card is not in trade
        ArrayList<TradingDeal> deals = this.tradeRepository.listAll();
        for (TradingDeal deal : deals) {
            if (deck.contains(deal.getCardToTrade())) {
                throw new BadRequestException("Card is in trade");
            }
        }

        this.deckRepository.clearForUser(user.getId());
        this.deckRepository.addCardsToDeck(user.getId(), deck);
    }

    public ArrayList<UUID> getDeck(User user) {
        return this.deckRepository.getByUser(user);
    }
}
