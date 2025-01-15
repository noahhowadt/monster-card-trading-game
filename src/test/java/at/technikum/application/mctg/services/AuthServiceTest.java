package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.entities.UserCredentials;
import at.technikum.application.mctg.exceptions.BadRequestException;
import at.technikum.application.mctg.exceptions.UnauthorizedException;
import at.technikum.application.mctg.repositories.UserRepository;
import at.technikum.server.http.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    private AuthService authService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthService(userRepository);
    }

    @Test
    void testGetToken_Success() {
        UserCredentials credentials = new UserCredentials("user1", "password123");
        User user = new User("user1", "password123");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        String token = authService.getToken(credentials);

        assertEquals("user1-mctgToken", token);
        verify(userRepository, times(1)).findByUsername("user1");
    }

    @Test
    void testGetToken_UserNotFound() {
        UserCredentials credentials = new UserCredentials("nonexistent", "password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.getToken(credentials));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testGetToken_WrongPassword() {
        UserCredentials credentials = new UserCredentials("user1", "wrongpassword");
        User user = new User("user1", "password123");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> authService.getToken(credentials));
        verify(userRepository, times(1)).findByUsername("user1");
    }

    @Test
    void testAuthenticate_Success() {
        Request request = mock(Request.class);
        User user = new User("user1", "password123");

        when(request.getHeader("Authorization")).thenReturn("Bearer user1-mctgToken");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        User authenticatedUser = authService.authenticate(request);

        assertEquals(user, authenticatedUser);
        verify(request, times(1)).getHeader("Authorization");
        verify(userRepository, times(1)).findByUsername("user1");
    }

    @Test
    void testAuthenticate_MissingAuthorizationHeader() {
        Request request = mock(Request.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> authService.authenticate(request));
        verify(request, times(1)).getHeader("Authorization");
        verifyNoInteractions(userRepository);
    }

    @Test
    void testAuthenticate_InvalidTokenFormat() {
        Request request = mock(Request.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

        assertThrows(UnauthorizedException.class, () -> authService.authenticate(request));
        verify(request, times(1)).getHeader("Authorization");
        verifyNoInteractions(userRepository);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        Request request = mock(Request.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer user1-mctgToken");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.authenticate(request));
        verify(request, times(1)).getHeader("Authorization");
        verify(userRepository, times(1)).findByUsername("user1");
    }

    @Test
    void testIsAdmin_True() {
        User admin = new User("admin", "password123");

        assertTrue(authService.isAdmin(admin));
    }

    @Test
    void testIsAdmin_False() {
        User user = new User("user1", "password123");

        assertFalse(authService.isAdmin(user));
    }
}
