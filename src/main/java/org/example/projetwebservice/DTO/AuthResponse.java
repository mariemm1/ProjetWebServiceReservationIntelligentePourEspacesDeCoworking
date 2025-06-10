package org.example.projetwebservice.DTO;

public class AuthResponse {
    private String accessToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken() { return accessToken; }
}
