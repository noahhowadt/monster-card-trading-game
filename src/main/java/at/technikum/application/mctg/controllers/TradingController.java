package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.TradingDeal;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.MethodNotAllowedException;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.TradingService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.UUID;

public class TradingController extends Controller {
    private final AuthService authService;
    private final TradingService tradingService;

    public TradingController(AuthService authService, TradingService tradingService) {
        this.authService = authService;
        this.tradingService = tradingService;
    }

    public Response handle(Request request) {
        User user = authService.authenticate(request);

        if (request.getMethod() == Method.POST && request.getPath().equals("/tradings")) {
            // Add trade
            return this.addTrade(request, user);
        } else if (request.getMethod() == Method.POST) {
            // Carry out trade
            return this.carryOutTrade(request, user);
        } else if (request.getMethod() == Method.GET) {
            // get all trades
            return this.getAllTrades(request, user);
        } else if (request.getMethod() == Method.DELETE) {
            // delete trade
            return this.deleteTrade(request, user);
        } else {
            throw new MethodNotAllowedException("Method not allowed");
        }
    }

    private Response addTrade(Request request, User user) {
        // Add trade
        TradingDeal deal = super.parseBody(request, new TypeReference<TradingDeal>() {
        });

        this.tradingService.addTrade(user, deal);

        Response response = new Response();
        response.setStatus(Status.CREATED);
        return response;
    }

    private Response carryOutTrade(Request request, User user) {
        // get tradeId from url
        String[] parts = request.getPath().split("/");
        if (parts.length != 3) {
            throw new MethodNotAllowedException("Method not allowed");
        }
        UUID tradeId = UUID.fromString(parts[2]);
        System.out.println(tradeId);

        // get cardId from body
        System.out.println(request.getBody());
        UUID offeredCardId = UUID.fromString(request.getBody().replace("\"", ""));
        System.out.println(offeredCardId);

        // Carry out trade
        this.tradingService.carryOutTrade(user, tradeId, offeredCardId);

        Response response = new Response();
        response.setStatus(Status.OK);
        return response;
    }

    private Response getAllTrades(Request request, User user) {
        // get all trades
        ArrayList<TradingDeal> trades = this.tradingService.getAllTrades();

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setBody(super.stringifyObject(trades));
        return response;
    }

    private Response deleteTrade(Request request, User user) {
        // delete trade
        String[] parts = request.getPath().split("/");
        if (parts.length != 3) {
            throw new MethodNotAllowedException("Method not allowed");
        }
        UUID tradeId = UUID.fromString(parts[2]);
        this.tradingService.deleteTrade(user, tradeId);

        Response response = new Response();
        response.setStatus(Status.OK);
        return response;
    }
}
