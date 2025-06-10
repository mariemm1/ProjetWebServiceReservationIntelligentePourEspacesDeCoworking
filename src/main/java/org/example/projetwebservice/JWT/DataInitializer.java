package org.example.projetwebservice.JWT;

import org.example.projetwebservice.Model.Enum.Role;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "superadmin@superadmin.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setName("Super Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("superadmin123")); // 🔐 Mot de passe chiffré
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("✅ Admin created: " + adminEmail);
            }
        };
    }
}
