package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.ConflictException;
import at.technikum.application.mctg.exceptions.NotFoundException;
import at.technikum.application.mctg.repositories.UserRepository;

import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User newUser) throws ConflictException {
        if (userRepository.findByUsername(newUser.getUsername()).isPresent())
            throw new ConflictException("Username already exists");

        newUser.setId(UUID.randomUUID());
        newUser.setCoins(20);
        return userRepository.save(newUser);
    }

    public User update(User user, User body) throws NotFoundException {
        user.setName(body.getName());
        user.setBio(body.getBio());
        user.setImage(body.getImage());

        return userRepository.save(user);
    }

    public User get(String username) throws NotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Username does not exist"));
    }
}
