package at.technikum.application.mctg.services;

import at.technikum.application.mctg.dto.BattleLog;
import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.CardType;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.repositories.CardRepository;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class BattleService {
    private final BlockingQueue<User> requestQueue = new LinkedBlockingQueue<>(); // Thread-safe request queue
    private final BlockingQueue<BattleLog> resultsQueue = new LinkedBlockingQueue<>(); // Thread-safe results queue
    private final CardService cardService;
    private final CardRepository cardRepository;

    public BattleService(CardService cardService, CardRepository cardRepository) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
    }

    public ArrayList<String> battle(User user) {
        // Check that user has a deck
        ArrayList<UUID> deck = this.cardService.getDeck(user);
        if (deck == null || deck.isEmpty()) {
            throw new BadRequestException("User has no deck");
        }

        try {
            // Check if there's already a user waiting for a battle
            User waitingUser = requestQueue.poll(); // Non-blocking check

            if (waitingUser == null) {
                // No user waiting, add current user to the queue and wait for a match
                System.out.println("User " + user.getUsername() + " is waiting for a battle...");
                requestQueue.put(user); // Add current user to the queue
                // save current timestamp
                long start = System.currentTimeMillis();

                // Wait for another user to join, timeout after 60 seconds
                while (System.currentTimeMillis() - start < 60000) {
                    BattleLog battleResult = resultsQueue.peek(); // Wait for a battle result
                    if (battleResult != null && battleResult.isParticipant(user)) {
                        // User is participant in the battle, return the log
                        return battleResult.getLog();
                    }
                }
            }

            // There is a user waiting, start the battle
            System.out.println("User " + user.getUsername() + " is battling against " + waitingUser.getUsername());
            return handleBattle(waitingUser, user);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private ArrayList<String> handleBattle(User user1, User user2) {
        try {
            // Battle logic
            System.out.println("Battle between " + user1.getUsername() + " and " + user2.getUsername());

            // get decks ids
            ArrayList<UUID> deck1Ids = this.cardService.getDeck(user1);
            ArrayList<UUID> deck2Ids = this.cardService.getDeck(user2);

            // get decks
            ArrayList<Card> deck1 = new ArrayList<>();
            ArrayList<Card> deck2 = new ArrayList<>();
            for (UUID cardId : deck1Ids) {
                deck1.add(this.cardRepository.getById(cardId));
            }
            for (UUID cardId : deck2Ids) {
                deck2.add(this.cardRepository.getById(cardId));
            }


            // Create battle log
            BattleLog battleLog = new BattleLog(user1, user2);
            battleLog.addLogEntry("Battle started between " + user1.getUsername() + " and " + user2.getUsername());

            int round = 1;
            while (round <= 100 && !deck1.isEmpty() && !deck2.isEmpty()) {
                battleLog.addLogEntry("Round " + round);
                UUID winner = handleRound(user1, deck1, user2, deck2, round);
                if (winner != null) {
                    // Remove the card from the deck
                    deck1.removeIf(card -> card.getUserId().equals(winner));
                    deck2.removeIf(card -> card.getUserId().equals(winner));
                    // TODO: Add the card to the winner's deck
                }
                round++;
            }

            resultsQueue.put(battleLog); // Add battle log to the results queue
            return battleLog.getLog();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private UUID handleRound(User user1, ArrayList<Card> deck1, User user2, ArrayList<Card> deck2, int round) {
        // get random cards from the deck
        Card card1 = deck1.get(ThreadLocalRandom.current().nextInt(deck1.size()));
        Card card2 = deck2.get(ThreadLocalRandom.current().nextInt(deck2.size()));
        boolean isPureMonsterFight = card1.getType().equals(CardType.MONSTER) && card2.getType().equals(CardType.MONSTER);

        // print element
        System.out.println("Element: " + card1.getElement() + " vs " + card2.getElement());
        // print monster
        System.out.println("Monster: " + card1.getMonster() + " vs " + card2.getMonster());

        UUID winner;
        if (true /* isPureMonsterFight */) {
            if (card1.getDamage() > card2.getDamage()) {
                winner = card1.getUserId();
            } else if (card1.getDamage() < card2.getDamage()) {
                winner = card2.getUserId();
            } else {
                // Draw
                winner = null;
            }
        } else {

        }

        String winnerName = winner == null ? "Draw" : winner.equals(user1.getId()) ? user1.getUsername() : user2.getUsername();
        if (winner != null) {
            System.out.println(winnerName + " won round " + round);
        } else {
            System.out.println("Round " + round + " was a draw");
        }

        // Battle logic
        return UUID.randomUUID();
    }

    /*private UUID handleSpecialRules(Card card1, Card card2) {
        // Goblins are too afraid of Dragons to attack
        if (card1.getMonster() == CardMonster.GOBLIN && card2.getMonster() == CardMonster.DRAGON) {
            return card2.getId();
        }

        if (card2.getMonster() == CardMonster.GOBLIN && card1.getMonster() == CardMonster.DRAGON) {
            return card1.getId();
        }

        // Wizzard can control Orks so they are not able to damage them.

        // The armor of Knights is so heavy that WaterSpells make them drown them instantly.

        // The Kraken is immune against spells.

        // The FireElves know Dragons since they were little and can evade their attacks.

    }*/
}
