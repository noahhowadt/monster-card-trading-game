package at.technikum.application.mctg;

import at.technikum.application.mctg.controllers.*;
import at.technikum.application.mctg.data.ConnectionPooler;
import at.technikum.application.mctg.exceptions.ExceptionHandler;
import at.technikum.application.mctg.repositories.*;
import at.technikum.application.mctg.routing.Router;
import at.technikum.application.mctg.services.*;
import at.technikum.server.Application;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class MonsterCardTradingGame implements Application {
    private final Router router;
    private final ExceptionHandler exceptionHandler;

    public MonsterCardTradingGame() {
        // init routing
        this.router = new Router();
        this.exceptionHandler = new ExceptionHandler();
        this.initRoutes();
    }

    private void initRoutes() {
        // init connection pooler
        ConnectionPooler connectionPooler = new ConnectionPooler();
        // init repositories
        UserRepository userRepository = new UserRepository(connectionPooler);
        PackageRepository packageRepository = new PackageRepository(connectionPooler);
        CardRepository cardRepository = new CardRepository(connectionPooler);
        DeckRepository deckRepository = new DeckRepository(connectionPooler);
        TradeRepository tradeRepository = new TradeRepository(connectionPooler);

        // init services
        UserService userService = new UserService(userRepository);
        AuthService authService = new AuthService(userRepository);
        CleanService cleanService = new CleanService(userRepository, packageRepository, cardRepository);
        CardService cardService = new CardService(packageRepository, cardRepository, userRepository, deckRepository, tradeRepository);
        TradingService tradingService = new TradingService(tradeRepository, cardRepository, deckRepository);
        BattleService battleService = new BattleService(cardService, cardRepository);

        // init routes
        this.router.addRoute("/users", new UserController(userService, authService));
        this.router.addRoute("/sessions", new SessionController(authService));
        this.router.addRoute("/packages", new PackageController(authService, cardService));
        this.router.addRoute("/transactions/packages", new TransactionController(authService, cardService));
        this.router.addRoute("/cards", new CardController(cardService, authService));
        this.router.addRoute("/deck", new DeckController(authService, userService, cardService));
        this.router.addRoute("/tradings", new TradingController(authService, tradingService));
        this.router.addRoute("/battles", new BattleController(battleService, authService));
        this.router.addRoute("/clean", new CleanController(cleanService));
    }

    @Override
    public Response handle(final Request request) {
        return this.exceptionHandler.wrap(() -> {
            final Controller controller = router.getController(request);
            return controller.handle(request);
        });
    }
}
