package at.technikum.application.mctg.services;

import at.technikum.application.mctg.dto.UserStats;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.repositories.StatsRepository;

import java.util.ArrayList;

public class StatsService {
    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public UserStats getStats(User user) {
        return this.statsRepository.getByUser(user);
    }

    public ArrayList<UserStats> getAllStats() {
        return this.statsRepository.getAll();
    }
}
