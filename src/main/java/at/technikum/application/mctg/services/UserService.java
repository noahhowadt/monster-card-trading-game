package at.technikum.application.mctg.services;

import at.technikum.application.mctg.entities.User;
import at.technikum.application.mctg.exceptions.ConflictException;
import at.technikum.application.mctg.repositories.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User newUser) throws ConflictException {
        if (userRepository.findByUsername(newUser.getUsername()).isPresent())
            throw new ConflictException("Username already exists");

        return userRepository.save(newUser);
    }
}
