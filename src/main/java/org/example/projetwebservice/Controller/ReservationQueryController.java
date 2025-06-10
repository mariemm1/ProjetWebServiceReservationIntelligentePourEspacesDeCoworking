package org.example.projetwebservice.Controller;

import org.example.projetwebservice.JWT.SecurityUtil;
import org.example.projetwebservice.Model.Enum.Role;
import org.example.projetwebservice.Model.Reservation;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Repository.ReservationRepository;
import org.example.projetwebservice.Repository.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReservationQueryController {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReservationQueryController(ReservationRepository reservationRepository,
                                      UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié"));
    }

    @QueryMapping
    public List<Reservation> getReservations() {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Accès refusé : seul un ADMIN peut voir toutes les réservations.");
        }
        return reservationRepository.findAll();
    }

    @QueryMapping
    public List<Reservation> getMyReservations() {
        User current = getCurrentUser();
        return reservationRepository.findByUserId(current.getId());
    }

    @QueryMapping
    public Reservation getReservationById(@Argument Long id) {
        User current = getCurrentUser();
        return reservationRepository.findById(id)
                .filter(res -> res.getUser().getId().equals(current.getId()) || current.getRole() == Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable ou accès non autorisé."));
    }
}
