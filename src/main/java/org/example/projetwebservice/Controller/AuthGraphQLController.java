package org.example.projetwebservice.Controller;

import org.example.projetwebservice.DTO.AuthRequest;
import org.example.projetwebservice.DTO.AuthResponse;
import org.example.projetwebservice.DTO.RegisterRequest;
import org.example.projetwebservice.Services.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthGraphQLController {

    private final AuthService authService;

    public AuthGraphQLController(AuthService authService) {
        this.authService = authService;
    }

    @MutationMapping
    public AuthResponse register(@Argument RegisterRequest input) {
        return authService.register(input);
    }

    @MutationMapping
    public AuthResponse login(@Argument AuthRequest input) {
        return authService.authenticate(input);
    }

    @MutationMapping
    public Boolean logout() {
        // Simulation du logout côté client (le frontend supprime le token JWT)
        return true;
    }
}
