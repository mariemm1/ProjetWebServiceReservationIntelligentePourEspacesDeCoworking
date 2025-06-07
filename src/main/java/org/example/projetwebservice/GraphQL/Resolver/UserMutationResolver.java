package org.example.projetwebservice.GraphQL.Resolver;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.example.projetwebservice.GraphQL.Input.UserInput;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMutationResolver implements GraphQLMutationResolver {

    private final UserRepository userRepository;

    public UserMutationResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserInput input) {
        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setRole(input.getRole());
        return userRepository.save(user);
    }
    public Boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
