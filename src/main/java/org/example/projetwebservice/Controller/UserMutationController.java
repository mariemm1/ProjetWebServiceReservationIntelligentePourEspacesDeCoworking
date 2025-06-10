package org.example.projetwebservice.Controller;

import org.example.projetwebservice.DTO.UserRequest;
import org.example.projetwebservice.JWT.SecurityUtil;
import org.example.projetwebservice.Model.Enum.Role;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Repository.UserRepository;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

@Controller
public class UserMutationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserMutationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @MutationMapping
    public User createUser(@Argument("input") UserRequest input) {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access Denied: Only ADMIN can create users");
        }

        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setRole(input.getRole());

        // 🔐 Encode the password before saving
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument("input") UserRequest input) {
        User current = getCurrentUser();

        return userRepository.findById(id)
                .map(user -> {
                    // ADMIN peut tout modifier
                    if (current.getRole() == Role.ADMIN) {
                        user.setName(input.getName());
                        user.setEmail(input.getEmail());
                        user.setRole(input.getRole());
                        if (input.getPassword() != null && !input.getPassword().isBlank()) {
                            user.setPassword(passwordEncoder.encode(input.getPassword()));
                        }
                        return userRepository.save(user);
                    }

                    // CLIENT ne peut modifier que son propre compte (nom + mot de passe)
                    if (!current.getId().equals(user.getId())) {
                        throw new RuntimeException("Access Denied: You can only update your own account");
                    }

                    user.setName(input.getName());
                    if (input.getPassword() != null && !input.getPassword().isBlank()) {
                        user.setPassword(passwordEncoder.encode(input.getPassword()));
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access Denied: Only ADMIN can delete users");
        }

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié"));
    }
}