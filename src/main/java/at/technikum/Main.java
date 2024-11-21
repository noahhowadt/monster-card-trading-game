package at.technikum;

import at.technikum.application.mctg.MonsterCardTradingGame;
import at.technikum.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(new MonsterCardTradingGame());
        server.start();
    }
}