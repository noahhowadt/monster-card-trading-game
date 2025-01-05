package at.technikum.application.mctg.services;

import at.technikum.application.mctg.repositories.CardRepository;
import at.technikum.application.mctg.repositories.PackageRepository;
import at.technikum.application.mctg.repositories.UserRepository;

public class CleanService {
    private UserRepository userRepository;
    private PackageRepository packageRepository;
    private CardRepository cardRepository;

    public CleanService(UserRepository userRepository, PackageRepository packageRepository, CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.packageRepository = packageRepository;
        this.cardRepository = cardRepository;
    }

    public void clean() {
        this.cardRepository.deleteAll();
        this.packageRepository.deleteAll();
        this.userRepository.deleteAll();
    }
}
