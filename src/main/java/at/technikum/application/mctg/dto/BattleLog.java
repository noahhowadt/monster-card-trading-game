package at.technikum.application.mctg.dto;

import at.technikum.application.mctg.entities.User;

import java.util.ArrayList;

public class BattleLog {
    private final User player1;
    private final User player2;
    private ArrayList<String> log;

    public BattleLog(User player1, User player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.log = new ArrayList<>();
    }

    public boolean isParticipant(User user) {
        return user.equals(player1) || user.equals(player2);
    }

    public void addLogEntry(String entry) {
        log.add(entry);
    }

    public void setLog(ArrayList<String> log) {
        this.log = log;
    }

    public ArrayList<String> getLog() {
        return log;
    }
}
