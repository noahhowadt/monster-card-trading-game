package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.ConflictException;
import at.technikum.application.mctg.exceptions.NotFoundException;
import at.technikum.application.mctg.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    void testCreate_UserAlreadyExists_ThrowsConflictException() {
        User newUser = new User();
        newUser.setUsername("existingUser");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThrows(ConflictException.class, () -> userService.create(newUser));

        verify(userRepository, times(1)).findByUsername("existingUser");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreate_NewUser_SuccessfullyCreated() {
        User newUser = new User();
        newUser.setUsername("newUser");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.create(newUser);

        assertNotNull(createdUser.getId());
        assertEquals(20, createdUser.getCoins());
        assertEquals("newUser", createdUser.getUsername());

        verify(userRepository, times(1)).findByUsername("newUser");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testUpdate_UserUpdatedSuccessfully() {
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setUsername("existingUser");

        User body = new User();
        body.setName("Updated Name");
        body.setBio("Updated Bio");
        body.setImage("updated_image.jpg");

        when(userRepository.save(existingUser)).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.update(existingUser, body);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("Updated Bio", updatedUser.getBio());
        assertEquals("updated_image.jpg", updatedUser.getImage());

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testGet_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.get("nonexistentUser"));

        verify(userRepository, times(1)).findByUsername("nonexistentUser");
    }

    @Test
    void testGet_UserFound_SuccessfullyRetrieved() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("existingUser");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        User retrievedUser = userService.get("existingUser");

        assertEquals("existingUser", retrievedUser.getUsername());

        verify(userRepository, times(1)).findByUsername("existingUser");
    }
}
