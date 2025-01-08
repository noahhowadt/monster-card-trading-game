package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.MethodNotAllowedException;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.CardService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.ArrayList;

public class CardController extends Controller {
    private final AuthService authService;
    private final CardService cardService;

    public CardController(CardService cardService, AuthService authService) {
        this.cardService = cardService;
        this.authService = authService;
    }

    public Response handle(Request request) {
        if (!request.getMethod().equals(Method.GET)) throw new MethodNotAllowedException("Method not allowed");

        User user = authService.authenticate(request);
        ArrayList<Card> cards = cardService.getByUser(user);

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setBody(super.stringifyObject(cards));
        return response;
    }
}
