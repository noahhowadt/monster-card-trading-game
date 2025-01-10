package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.dto.UserStats;
import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.services.AuthService;
import at.technikum.application.mctg.services.StatsService;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class StatsController extends Controller {
    private final AuthService authService;
    private final StatsService statsService;

    public StatsController(AuthService authService, StatsService statsService) {
        this.authService = authService;
        this.statsService = statsService;
    }

    public Response handle(Request request) {
        User user = authService.authenticate(request);
        UserStats stats = statsService.getStats(user);

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setBody(super.stringifyObject(stats));
        return response;
    }
}
