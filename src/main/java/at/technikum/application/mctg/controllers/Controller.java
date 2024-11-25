package at.technikum.application.mctg.controllers;

import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.exceptions.InternalException;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public abstract class Controller {
    public abstract Response handle(Request request);

    public <T> T parseBody(Request req, TypeReference<T> clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);

            // Deserialize JSON to object of the given class type
            return objectMapper.readValue(req.getBody(), clazz);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public <T> String stringifyObject(T object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new InternalException(e.getMessage());
        }
    }
}
