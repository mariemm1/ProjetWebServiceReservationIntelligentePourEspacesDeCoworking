package org.example.projetwebservice.GraphQL.Resolver;

import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Repository.UserRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserQueryResolver {

    private final UserRepository userRepository;

    public UserQueryResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @QueryMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @QueryMapping
    public User getUserById(@Argument Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
