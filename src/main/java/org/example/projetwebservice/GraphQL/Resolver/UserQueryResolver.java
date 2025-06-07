package org.example.projetwebservice.GraphQL.Resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserQueryResolver implements GraphQLQueryResolver {

    private final UserRepository userRepository;

    public UserQueryResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}