package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.repositories.CardRepository;
import at.technikum.application.mctg.repositories.PackageRepository;
import at.technikum.application.mctg.repositories.UserRepository;

import java.util.ArrayList;
import java.util.UUID;

public class CardService {
    private final PackageRepository packageRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardService(final PackageRepository packageRepository, final CardRepository cardRepository, final UserRepository userRepository) {
        this.packageRepository = packageRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
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
}
