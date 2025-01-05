package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.CardService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class TransactionController extends Controller {
    private final AuthService authService;
    private final CardService cardService;

    public TransactionController(final AuthService authService, CardService cardService) {
        this.authService = authService;
        this.cardService = cardService;
    }

    public Response handle(final Request request) {
        User user = this.authService.authenticate(request);
        this.cardService.acquirePackage(user);

        Response response = new Response();
        response.setStatus(Status.OK);
        return response;
    }
}
