package org.example.projetwebservice.GraphQL.Resolver;

import org.example.projetwebservice.GraphQL.Input.ReservationInput;
import org.example.projetwebservice.Model.Reservation;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.ReservationRepository;
import org.example.projetwebservice.Repository.UserRepository;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class ReservationMutationResolver {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    public ReservationMutationResolver(ReservationRepository reservationRepository,
                                       UserRepository userRepository,
                                       WorkspaceRepository workspaceRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @MutationMapping
    public Reservation createReservation(@Argument ReservationInput input) {
        List<Reservation> existing = reservationRepository.findByWorkspaceIdAndDate(input.getWorkspaceId(), input.getDate());

        for (Reservation res : existing) {
            boolean overlap = input.getStartTime().isBefore(res.getEndTime())
                    && input.getEndTime().isAfter(res.getStartTime());
            if (overlap) {
                throw new RuntimeException("Conflit de réservation : créneau horaire déjà réservé.");
            }
        }

        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Workspace workspace = workspaceRepository.findById(input.getWorkspaceId())
                .orElseThrow(() -> new RuntimeException("Espace introuvable"));

        Reservation reservation = new Reservation();
        reservation.setDate(input.getDate());
        reservation.setStartTime(input.getStartTime());
        reservation.setEndTime(input.getEndTime());
        reservation.setUser(user);
        reservation.setWorkspace(workspace);

        return reservationRepository.save(reservation);
    }

    @MutationMapping
    public Reservation updateReservation(@Argument Long id, @Argument ReservationInput input) {
        List<Reservation> existing = reservationRepository.findByWorkspaceIdAndDate(input.getWorkspaceId(), input.getDate());

        for (Reservation res : existing) {
            if (res.getId().equals(id)) continue; // Ignorer la réservation actuelle lors de la vérification de conflit
            boolean overlap = input.getStartTime().isBefore(res.getEndTime())
                    && input.getEndTime().isAfter(res.getStartTime());
            if (overlap) {
                throw new RuntimeException("Conflit de réservation : créneau horaire déjà réservé.");
            }
        }

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Workspace workspace = workspaceRepository.findById(input.getWorkspaceId())
                .orElseThrow(() -> new RuntimeException("Espace introuvable"));

        reservation.setDate(input.getDate());
        reservation.setStartTime(input.getStartTime());
        reservation.setEndTime(input.getEndTime());
        reservation.setUser(user);
        reservation.setWorkspace(workspace);

        return reservationRepository.save(reservation);
    }


    @MutationMapping
    public Boolean cancelReservation(@Argument Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        }
        return false;
    }



}
