package at.technikum.application.mctg.services;

import at.technikum.application.mctg.repositories.*;

public class CleanService {
    private final UserRepository userRepository;
    private final PackageRepository packageRepository;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final TradeRepository tradeRepository;
    private final StatsRepository statsRepository;

    public CleanService(UserRepository userRepository, PackageRepository packageRepository, CardRepository cardRepository, DeckRepository deckRepository, TradeRepository tradeRepository, StatsRepository statsRepository) {
        this.deckRepository = deckRepository;
        this.tradeRepository = tradeRepository;
        this.statsRepository = statsRepository;
        this.packageRepository = packageRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public void clean() {
        this.tradeRepository.deleteAll();
        this.cardRepository.deleteAll();
        this.packageRepository.deleteAll();
        this.userRepository.deleteAll();
        this.deckRepository.deleteAll();
        this.statsRepository.deleteAll();
    }
}
