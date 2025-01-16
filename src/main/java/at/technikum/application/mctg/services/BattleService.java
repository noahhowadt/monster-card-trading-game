package at.technikum.application.mctg.services;

import at.technikum.application.mctg.dto.BattleLog;
import at.technikum.application.mctg.entities.*;
import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.repositories.CardRepository;
import at.technikum.application.mctg.repositories.StatsRepository;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BattleService {
    private final BlockingQueue<User> requestQueue = new LinkedBlockingQueue<>(); // Thread-safe request queue
    private final BlockingQueue<BattleLog> resultsQueue = new LinkedBlockingQueue<>(); // Thread-safe results queue
    private final CardService cardService;
    private final CardRepository cardRepository;
    private final ArrayList<ArrayList<Predicate<Card>>> specialRules = new ArrayList<ArrayList<Predicate<Card>>>();
    private final ArrayList<String> specialRulesText = new ArrayList<String>();
    private final StatsRepository statsRepository;

    public BattleService(CardService cardService, CardRepository cardRepository, StatsRepository statsRepository) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
        this.statsRepository = statsRepository;
        this.initRules();
    }

    private void initRules() {
        // Goblins are too afraid of Dragons to attack
        ArrayList<Predicate<Card>> rule1 = new ArrayList<Predicate<Card>>();
        rule1.add((Card c) -> c.getMonster() == CardMonster.DRAGON);
        rule1.add((Card c) -> c.getMonster() == CardMonster.GOBLIN);
        specialRulesText.add("Goblins are too afraid of Dragons to attack");
        specialRules.add(rule1);

        // Wizzard can control Orks so they are not able to damage them.
        ArrayList<Predicate<Card>> rule2 = new ArrayList<Predicate<Card>>();
        rule2.add((Card c) -> c.getMonster() == CardMonster.WIZZARD);
        rule2.add((Card c) -> c.getMonster() == CardMonster.ORK);
        specialRulesText.add("Wizzard can control Orks so they are not able to damage them");
        specialRules.add(rule2);

        // The armor of Knights is so heavy that WaterSpells make them drown them instantly.
        ArrayList<Predicate<Card>> rule3 = new ArrayList<Predicate<Card>>();
        rule3.add((Card c) -> c.getElement() == CardElement.WATER && c.getType() == CardType.SPELL);
        rule3.add((Card c) -> c.getMonster() == CardMonster.KNIGHT);
        specialRulesText.add("The armor of Knights is so heavy that WaterSpells make them drown them instantly");
        specialRules.add(rule3);

        // The Kraken is immune against spells.
        ArrayList<Predicate<Card>> rule4 = new ArrayList<Predicate<Card>>();
        rule4.add((Card c) -> c.getMonster() == CardMonster.KRAKEN);
        rule4.add((Card c) -> c.getType() == CardType.SPELL);
        specialRulesText.add("The Kraken is immune against spells");
        specialRules.add(rule4);

        // The FireElves know Dragons since they were little and can evade their attacks.
        ArrayList<Predicate<Card>> rule5 = new ArrayList<Predicate<Card>>();
        rule5.add((Card c) -> c.getElement() == CardElement.FIRE && c.getMonster() == CardMonster.ELF);
        rule5.add((Card c) -> c.getMonster() == CardMonster.DRAGON);
        specialRulesText.add("The FireElves know Dragons since they were little and can evade their attacks");
        specialRules.add(rule5);
    }

    public ArrayList<String> battle(User user) {
        // Check that user has a deck
        ArrayList<UUID> deck = this.cardService.getDeck(user);
        if (deck == null || deck.isEmpty()) {
            throw new BadRequestException("User has no deck");
        }

        try {
            User waitingUser = null;
            synchronized (requestQueue) {
                waitingUser = requestQueue.poll(); // Non-blocking check
                if (waitingUser == null) {
                    requestQueue.put(user); // Add current user to the queue
                }
            }

            if (waitingUser == null) {
                // No user waiting, add current user to the queue and wait for a match
                System.out.println("User " + user.getUsername() + " is waiting for a battle...");
                // save current timestamp
                long start = System.currentTimeMillis();

                // Wait for another thread to run the battle and push the result
                while (System.currentTimeMillis() - start < 60000) {
                    Thread.sleep(1000); // Sleep for 1 second
                    BattleLog battleResult = resultsQueue.peek(); // Wait for a battle result
                    if (battleResult != null && battleResult.isParticipant(user)) {
                        // User is participant in the battle, return the log
                        resultsQueue.take(); // Remove the result from the queue
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
                battleLog.addLogEntry("Round " + round + " started");
                battleLog.addLogEntry(user1.getUsername() + " has " + deck1.size() + " cards left");
                battleLog.addLogEntry(user2.getUsername() + " has " + deck2.size() + " cards left");

                // get random cards from the deck
                Card card1 = deck1.get(ThreadLocalRandom.current().nextInt(deck1.size()));
                Card card2 = deck2.get(ThreadLocalRandom.current().nextInt(deck2.size()));

                // write cards to battle log
                battleLog.addLogEntry("Round " + round + ": " + user1.getUsername() + " played " + card1.getName() + " (" + card1.getDamage() + " damage)");
                battleLog.addLogEntry("Round " + round + ": " + user2.getUsername() + " played " + card2.getName() + " (" + card2.getDamage() + " damage)");

                // random card from deck 1 or deck 2
                Card luckyCard = Stream.concat(deck1.stream(), deck2.stream()).skip(ThreadLocalRandom.current().nextInt((deck1.size() + deck2.size()) * 5)).findFirst().orElse(null);
                if (luckyCard != null) {
                    battleLog.addLogEntry("The lucky card is " + luckyCard.getName());
                } else {
                    battleLog.addLogEntry("There is no lucky card");
                }

                // handle round
                UUID winnerId = handleRound(card1, card2, battleLog, luckyCard);

                // get winner
                User winner = winnerId == null ? null : winnerId.equals(user1.getId()) ? user1 : user2;
                if (winner != null) {
                    battleLog.addLogEntry(winner.getUsername() + " won round " + round);
                } else {
                    battleLog.addLogEntry("Round " + round + " was a draw");
                }

                // move losing card from loser deck to winner deck
                if (winner != null) {
                    ArrayList<Card> winnerDeck = winner.equals(user1) ? deck1 : deck2;
                    ArrayList<Card> loserDeck = winner.equals(user1) ? deck2 : deck1;
                    Card losingCard = winner.equals(user1) ? card2 : card1;
                    loserDeck.remove(losingCard);
                    winnerDeck.add(losingCard);
                    battleLog.addLogEntry(losingCard.getName() + " was moved to " + winner.getUsername() + "'s deck");
                }
                round++;
            }

            // check if there is a winner
            User winner = null;
            if (!deck1.isEmpty() && !deck2.isEmpty()) {
                battleLog.addLogEntry("The battle ended in a draw");
            } else if (deck1.isEmpty()) {
                winner = user2;
                battleLog.addLogEntry(user2.getUsername() + " won the battle after " + round + " rounds");
            } else {
                winner = user1;
                battleLog.addLogEntry(user1.getUsername() + " won the battle after " + round + " rounds");
            }

            // add win and loss to stats
            if (winner != null) {
                this.statsRepository.addWin(winner.getId());
                this.statsRepository.addLoss(winner.equals(user1) ? user2.getId() : user1.getId());
            }

            resultsQueue.put(battleLog); // Add battle log to the results queue
            return battleLog.getLog();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private UUID handleRound(Card card1, Card card2, BattleLog battleLog, Card luckyCard) {
        // TESTING: return random winner
        /* if (ThreadLocalRandom.current().nextBoolean()) {
            return card1.getUserId();
        } else {
            return card2.getUserId();
        }*/

        // check if lucky card is in the fight
        if (luckyCard != null) {
            if (card1.getId().equals(luckyCard.getId())) {
                battleLog.addLogEntry("Lucky card " + luckyCard.getName() + " is in the fight. " + card1.getName() + " wins the round");
                return card1.getUserId();
            } else if (card2.getId().equals(luckyCard.getId())) {
                battleLog.addLogEntry("Lucky card " + luckyCard.getName() + " is in the fight. " + card2.getName() + " wins the round");
                return card2.getUserId();
            }
        }

        // get random cards from the deck
        boolean isPureMonsterFight = card1.getType().equals(CardType.MONSTER) && card2.getType().equals(CardType.MONSTER);
        if (isPureMonsterFight) battleLog.addLogEntry("This is a pure monster fight. Let's brawl!");

        // If a special rule applies, return the winner
        UUID winnerId = matchSpecialRules(card1, card2, battleLog);
        if (winnerId != null) {
            return winnerId;
        }

        float card1Damage = isPureMonsterFight ? card1.getDamage() : card1.getElement().getMultiplierAgainst(card2.getElement()) * card1.getDamage();
        float card2Damage = isPureMonsterFight ? card2.getDamage() : card2.getElement().getMultiplierAgainst(card1.getElement()) * card2.getDamage();

        if (card1Damage > card2Damage) {
            winnerId = card1.getUserId();
        } else if (card2Damage > card1Damage) {
            winnerId = card2.getUserId();
        }

        return winnerId;
    }

    private UUID matchSpecialRules(Card card1, Card card2, BattleLog battleLog) {
        for (int i = 0; i < specialRules.size(); i++) {
            ArrayList<Predicate<Card>> rule = specialRules.get(i);
            if (rule.get(0).test(card1) && rule.get(1).test(card2)) {
                battleLog.addLogEntry(specialRulesText.get(i));
                return card1.getUserId();
            }

            if (rule.get(0).test(card2) && rule.get(1).test(card1)) {
                battleLog.addLogEntry(specialRulesText.get(i));
                return card2.getUserId();
            }
        }

        return null;
    }
}
