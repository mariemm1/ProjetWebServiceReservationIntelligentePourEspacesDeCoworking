package org.example.projetwebservice.GraphQL.Resolver;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.example.projetwebservice.GraphQL.Input.ReservationInput;
import org.example.projetwebservice.Model.Reservation;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.ReservationRepository;
import org.example.projetwebservice.Repository.UserRepository;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationMutationResolver implements GraphQLMutationResolver {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    public ReservationMutationResolver(ReservationRepository reservationRepository, UserRepository userRepository, WorkspaceRepository workspaceRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
    }

    public Reservation createReservation(ReservationInput input) {
        // Vérifier conflits
        List<Reservation> existing = reservationRepository.findByWorkspaceIdAndDate(input.getWorkspaceId(), input.getDate());

        for (Reservation res : existing) {
            boolean overlap = input.getStartTime().isBefore(res.getEndTime()) && input.getEndTime().isAfter(res.getStartTime());
            if (overlap) {
                throw new RuntimeException("Conflit de réservation : créneau horaire déjà réservé.");
            }
        }

        User user = userRepository.findById(input.getUserId()).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Workspace workspace = workspaceRepository.findById(input.getWorkspaceId()).orElseThrow(() -> new RuntimeException("Espace introuvable"));

        Reservation reservation = new Reservation();
        reservation.setDate(input.getDate());
        reservation.setStartTime(input.getStartTime());
        reservation.setEndTime(input.getEndTime());
        reservation.setUser(user);
        reservation.setWorkspace(workspace);

        return reservationRepository.save(reservation);
    }

    public Boolean cancelReservation(Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
