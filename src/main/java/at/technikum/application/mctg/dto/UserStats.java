package at.technikum.application.mctg.dto;

public class UserStats {
    private final String username;
    private final int elo;
    private final int wins;
    private final int losses;

    public UserStats(String username, int elo, int wins, int losses) {
        this.username = username;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
    }

    public String getUsername() {
        return username;
    }

    public int getElo() {
        return elo;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}
