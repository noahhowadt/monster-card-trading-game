package at.technikum.application.mctg.exceptions;

import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.function.Supplier;

public class ExceptionHandler {
    public Response wrap(Supplier<Response> func) {
        try {
            return func.get();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return this.createErrorResponse(e, Status.BAD_REQUEST);
        } catch (NotFoundException e) {
            return this.createErrorResponse(e, Status.NOT_FOUND);
        } catch (ConflictException e) {
            return this.createErrorResponse(e, Status.CONFLICT);
        } catch (MethodNotAllowedException e) {
            return this.createErrorResponse(e, Status.METHOD_NOT_ALLOWED);
        } catch (UnauthorizedException e) {
            return this.createErrorResponse(e, Status.UNAUTHORIZED);
        } catch (ForbiddenException e) {
            return this.createErrorResponse(e, Status.FORBIDDEN);
        } catch (RuntimeException e) {
            return this.createErrorResponse(e, Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response createErrorResponse(RuntimeException e, Status status) {
        System.out.println(e.getMessage());
        Response res = new Response();
        res.setStatus(status);
        return res;
    }
}
