package at.technikum.application.mctg.repositories;

import at.technikum.application.mctg.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final List<User> users;

    public UserRepository() {
        this.users = new ArrayList<User>();
    }

    public User save(User user) {
        user.setId(this.users.size() + 1);
        this.users.add(user);
        System.out.println(this.users);
        return user;
    }

    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId() == Integer.parseInt(id))
                .findFirst();
    }

    public Optional<User> findByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }
}
