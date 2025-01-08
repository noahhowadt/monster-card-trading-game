package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.MethodNotAllowedException;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.BattleService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.ArrayList;

public class BattleController extends Controller {
    private final BattleService battleService;
    private final AuthService authService;

    public BattleController(BattleService battleService, AuthService authService) {
        this.battleService = battleService;
        this.authService = authService;
    }

    public Response handle(Request request) {
        if (request.getMethod() != Method.POST) {
            throw new MethodNotAllowedException("Method not allowed");
        }
        User user = authService.authenticate(request);
        ArrayList<String> battleLog = battleService.battle(user);

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setBody(super.stringifyObject(battleLog));
        return response;
    }
}
