package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.entities.UserCredentials;
import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.exceptions.UnauthorizedException;
import at.technikum.application.mctg.repositories.UserRepository;
import at.technikum.server.http.Request;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getToken(UserCredentials credentials) {
        User user = this.userRepository.findByUsername(credentials.getUsername()).orElseThrow(() -> new BadRequestException("User not found"));
        if (!user.getPassword().equals(credentials.getPassword())) throw new UnauthorizedException("Unauthorized");

        return user.getUsername() + "-mctgToken";
    }

    public User authenticate(Request request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) throw new UnauthorizedException("Unauthorized");
        String token = authHeader.replace("Bearer ", "");
        if (!token.endsWith("-mctgToken")) throw new UnauthorizedException("Unauthorized");

        return this.userRepository.findByUsername(token.split("-", 2)[0]).orElseThrow(() -> new BadRequestException("User not found"));
    }

    public boolean isAdmin(User user) {
        return user.getUsername().equals("admin");
    }
}
