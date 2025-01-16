package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.CardType;
import at.technikum.application.mctg.entities.TradingDeal;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {
    private CardService cardService;
    private PackageRepository packageRepository;
    private CardRepository cardRepository;
    private UserRepository userRepository;
    private DeckRepository deckRepository;
    private TradeRepository tradeRepository;

    @BeforeEach
    void setup() {
        packageRepository = mock(PackageRepository.class);
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        deckRepository = mock(DeckRepository.class);
        tradeRepository = mock(TradeRepository.class);

        cardService = new CardService(packageRepository, cardRepository, userRepository, deckRepository, tradeRepository);
    }

    @Test
    void testAddPackage_Valid() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(UUID.randomUUID(), "Card1"));
        cards.add(new Card(UUID.randomUUID(), "Card2"));
        cards.add(new Card(UUID.randomUUID(), "Card3"));
        cards.add(new Card(UUID.randomUUID(), "Card4"));
        cards.add(new Card(UUID.randomUUID(), "Card5"));

        cardService.addPackage(cards);

        // Verify if package creation and card saving methods were called
        verify(packageRepository, times(1)).createPackage(any(UUID.class));
        verify(cardRepository, times(1)).save(eq(cards), any(UUID.class));
    }

    @Test
    void testAddPackage_Invalid() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(UUID.randomUUID(), "Card1"));
        cards.add(new Card(UUID.randomUUID(), "Card2"));
        cards.add(new Card(UUID.randomUUID(), "Card3"));
        cards.add(new Card(UUID.randomUUID(), "Card4"));

        // Expect BadRequestException when package does not contain exactly 5 cards
        assertThrows(BadRequestException.class, () -> cardService.addPackage(cards));
    }

    @Test
    void testAcquirePackage_EnoughCoins() {
        User user = new User("user1", "password123");
        user.setCoins(10);

        UUID packageId = UUID.randomUUID();
        when(packageRepository.getRandomPackage()).thenReturn(packageId);

        cardService.acquirePackage(user);

        // Verify that the package was acquired, the user's coins were deducted, and the package was deleted
        verify(packageRepository, times(1)).getRandomPackage();
        verify(cardRepository, times(1)).acquireFromPackage(user, packageId);
        verify(packageRepository, times(1)).deletePackage(packageId);
        verify(userRepository, times(1)).save(user);
        assertEquals(5, user.getCoins());
    }

    @Test
    void testAcquirePackage_NotEnoughCoins() {
        User user = new User("user1", "password123");
        user.setCoins(4);

        // Expect BadRequestException when user has insufficient coins
        assertThrows(BadRequestException.class, () -> cardService.acquirePackage(user));
    }

    @Test
    void testGetByUser() {
        User user = new User("user1", "password123");
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(UUID.randomUUID(), "Card1"));
        when(cardRepository.getByUser(user)).thenReturn(cards);

        ArrayList<Card> result = cardService.getByUser(user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Card1", result.get(0).getName());
    }

    @Test
    void testUpdateDeck_Valid() {
        User user = new User("user1", "password123");
        ArrayList<UUID> deck = new ArrayList<>();
        UUID cardId1 = UUID.randomUUID();
        UUID cardId2 = UUID.randomUUID();
        UUID cardId3 = UUID.randomUUID();
        UUID cardId4 = UUID.randomUUID();
        deck.add(cardId1);
        deck.add(cardId2);
        deck.add(cardId3);
        deck.add(cardId4);

        ArrayList<Card> userCards = new ArrayList<>();
        userCards.add(new Card(cardId1, "Card1"));
        userCards.add(new Card(cardId2, "Card2"));
        userCards.add(new Card(cardId3, "Card3"));
        userCards.add(new Card(cardId4, "Card4"));

        when(cardRepository.getByUser(user)).thenReturn(userCards);

        cardService.updateDeck(user, deck);

        verify(deckRepository, times(1)).clearForUser(user.getId());
        verify(deckRepository, times(1)).addCardsToDeck(user.getId(), deck);
    }

    @Test
    void testUpdateDeck_InvalidCard() {
        User user = new User("user1", "password123");
        ArrayList<UUID> deck = new ArrayList<>();
        UUID cardId1 = UUID.randomUUID();
        UUID cardId2 = UUID.randomUUID();
        UUID cardId3 = UUID.randomUUID();
        UUID cardId4 = UUID.randomUUID();
        deck.add(cardId1);
        deck.add(cardId2);
        deck.add(cardId3);
        deck.add(cardId4);

        ArrayList<Card> userCards = new ArrayList<>();
        userCards.add(new Card(cardId1, "Card1"));
        userCards.add(new Card(cardId2, "Card2"));
        userCards.add(new Card(cardId3, "Card3"));

        when(cardRepository.getByUser(user)).thenReturn(userCards);

        // Expect BadRequestException when deck contains a card not owned by user
        assertThrows(BadRequestException.class, () -> cardService.updateDeck(user, deck));

        verify(deckRepository, never()).clearForUser(user.getId());
        verify(deckRepository, never()).addCardsToDeck(user.getId(), deck);
    }

    @Test
    void testUpdateDeck_CardInTrade() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "user1", "password123");
        ArrayList<UUID> deck = new ArrayList<>();
        UUID cardId1 = UUID.randomUUID();
        UUID cardId2 = UUID.randomUUID();
        UUID cardId3 = UUID.randomUUID();
        UUID cardId4 = UUID.randomUUID();

        ArrayList<Card> userCards = new ArrayList<>();
        userCards.add(new Card(cardId1, "Card1"));
        userCards.add(new Card(cardId2, "Card2"));
        userCards.add(new Card(cardId3, "Card3"));
        userCards.add(new Card(cardId4, "Card4"));

        when(cardRepository.getByUser(user)).thenReturn(userCards);

        ArrayList<TradingDeal> deals = new ArrayList<>();
        deals.add(new TradingDeal(UUID.randomUUID(), cardId4, CardType.MONSTER, 10, user.getId()));
        when(tradeRepository.listAll()).thenReturn(deals);

        deck.add(cardId1);
        deck.add(cardId2);
        deck.add(cardId3);
        deck.add(cardId4);

        // Expect BadRequestException when deck contains a card that is in trade
        assertThrows(BadRequestException.class, () -> cardService.updateDeck(user, deck));
    }

    @Test
    void testGetDeck() {
        User user = new User("user1", "password123");
        ArrayList<UUID> deck = new ArrayList<>();
        deck.add(UUID.randomUUID());
        when(deckRepository.getByUser(user)).thenReturn(deck);

        ArrayList<UUID> result = cardService.getDeck(user);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
