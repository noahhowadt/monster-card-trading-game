package at.technikum.application.mctg.exceptions;

import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.function.Supplier;

public class ExceptionHandler {
    public Response wrap(Supplier<Response> func) {
        try {
            return func.get();
        } catch (BadRequestException e) {
            return this.createErrorResponse(Status.BAD_REQUEST);
        } catch (NotFoundException e) {
            return this.createErrorResponse(Status.NOT_FOUND);
        } catch (ConflictException e) {
            return this.createErrorResponse(Status.CONFLICT);
        } catch (MethodNotAllowedException e) {
            return this.createErrorResponse(Status.METHOD_NOT_ALLOWED);
        } catch (UnauthorizedException e) {
            return this.createErrorResponse(Status.UNAUTHORIZED);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return this.createErrorResponse(Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response createErrorResponse(Status status) {
        Response res = new Response();
        res.setStatus(status);
        return res;
    }
}
