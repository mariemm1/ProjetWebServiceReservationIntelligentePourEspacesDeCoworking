package org.example.projetwebservice.Controller;

import org.example.projetwebservice.JWT.SecurityUtil;
import org.example.projetwebservice.Model.Enum.Role;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Repository.UserRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserQueryController {

    private final UserRepository userRepository;

    public UserQueryController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @QueryMapping
    public List<User> getUsers() {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access Denied: Only ADMIN can access the list of users.");
        }
        return userRepository.findAll();
    }

    @QueryMapping
    public User getUserById(@Argument Long id) {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access Denied: Only ADMIN can access user details.");
        }
        return userRepository.findById(id).orElse(null);
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié"));
    }
}
