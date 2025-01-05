package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.Card;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.ForbiddenException;
import at.technikum.application.mctg.exceptions.MethodNotAllowedException;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.CardService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;

public class PackageController extends Controller {
    private final AuthService authService;
    private final CardService cardService;

    public PackageController(final AuthService authService, final CardService cardService) {
        this.authService = authService;
        this.cardService = cardService;
    }

    public Response handle(final Request request) {
        if (request.getMethod() != Method.POST) throw new MethodNotAllowedException("Method not allowed");

        User user = this.authService.authenticate(request);
        if (!this.authService.isAdmin(user)) throw new ForbiddenException("User is not admin");

        ArrayList<Card> packageCards = super.parseBody(request, new TypeReference<ArrayList<Card>>() {
        });

        cardService.addPackage(packageCards);
        
        Response response = new Response();
        response.setStatus(Status.OK);
        return response;
    }
}
