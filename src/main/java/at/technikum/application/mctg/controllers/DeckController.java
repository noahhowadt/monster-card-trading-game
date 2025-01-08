package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.MethodNotAllowedException;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.CardService;
import at.technikum.application.mctg.services.UserService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.UUID;

public class DeckController extends Controller {
    private final AuthService authService;
    private final UserService userService;
    private final CardService cardService;

    public DeckController(AuthService authService, UserService userService, CardService cardService) {
        this.authService = authService;
        this.userService = userService;
        this.cardService = cardService;
    }

    public Response handle(Request request) {
        User user = this.authService.authenticate(request);
        switch (request.getMethod()) {
            case GET:
                return this.handleGet(request, user);
            case PUT:
                return this.handlePut(request, user);
            default:
                throw new MethodNotAllowedException("Method not allowed");
        }
    }

    private Response handlePut(Request request, User user) {
        ArrayList<UUID> deck = super.parseBody(request, new TypeReference<ArrayList<UUID>>() {
        });

        this.cardService.updateDeck(user, deck);

        Response response = new Response();
        response.setStatus(Status.OK);
        return response;
    }

    private Response handleGet(Request request, User user) {
        ArrayList<UUID> deck = this.cardService.getDeck(user);

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setBody(super.stringifyObject(deck));
        return response;
    }
}
