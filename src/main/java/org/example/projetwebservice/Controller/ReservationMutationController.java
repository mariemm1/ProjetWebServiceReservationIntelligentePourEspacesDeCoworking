package org.example.projetwebservice.Controller;

import org.example.projetwebservice.DTO.ReservationRequest;
import org.example.projetwebservice.JWT.SecurityUtil;
import org.example.projetwebservice.Model.Enum.Role;
import org.example.projetwebservice.Model.Reservation;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.ReservationRepository;
import org.example.projetwebservice.Repository.UserRepository;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReservationMutationController {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    public ReservationMutationController(ReservationRepository reservationRepository,
                                         UserRepository userRepository,
                                         WorkspaceRepository workspaceRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié"));
    }

    @MutationMapping
    public Reservation createReservation(@Argument ReservationRequest input) {
        User current = getCurrentUser();

        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Accès refusé : seul un ADMIN peut créer une réservation pour un autre utilisateur.");
        }

        validateSlotAvailability(input.getWorkspaceId(), input.getDate(), input.getStartTime(), input.getEndTime(), null);

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
    public Reservation createMyReservation(@Argument ReservationRequest input) {
        User currentUser = getCurrentUser();

        validateSlotAvailability(input.getWorkspaceId(), input.getDate(), input.getStartTime(), input.getEndTime(), null);

        Workspace workspace = workspaceRepository.findById(input.getWorkspaceId())
                .orElseThrow(() -> new RuntimeException("Espace introuvable"));

        Reservation reservation = new Reservation();
        reservation.setDate(input.getDate());
        reservation.setStartTime(input.getStartTime());
        reservation.setEndTime(input.getEndTime());
        reservation.setUser(currentUser);
        reservation.setWorkspace(workspace);

        return reservationRepository.save(reservation);
    }

    @MutationMapping
    public Reservation updateReservation(@Argument Long id, @Argument ReservationRequest input) {
        User currentUser = getCurrentUser();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (currentUser.getRole() != Role.ADMIN && !reservation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Accès refusé : vous ne pouvez modifier que vos propres réservations.");
        }

        validateSlotAvailability(input.getWorkspaceId(), input.getDate(), input.getStartTime(), input.getEndTime(), id);

        Workspace workspace = workspaceRepository.findById(input.getWorkspaceId())
                .orElseThrow(() -> new RuntimeException("Espace introuvable"));

        reservation.setDate(input.getDate());
        reservation.setStartTime(input.getStartTime());
        reservation.setEndTime(input.getEndTime());
        reservation.setWorkspace(workspace);

        return reservationRepository.save(reservation);
    }

    @MutationMapping
    public Boolean cancelReservation(@Argument Long id) {
        User currentUser = getCurrentUser();

        return reservationRepository.findById(id).map(reservation -> {
            if (currentUser.getRole() != Role.ADMIN && !reservation.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Accès refusé : vous ne pouvez annuler que vos propres réservations.");
            }
            reservationRepository.deleteById(id);
            return true;
        }).orElse(false);
    }

    private void validateSlotAvailability(Long workspaceId, java.time.LocalDate date,
                                          java.time.LocalTime start, java.time.LocalTime end, Long excludeId) {
        List<Reservation> existing = reservationRepository.findByWorkspaceIdAndDate(workspaceId, date);
        for (Reservation res : existing) {
            if (excludeId != null && res.getId().equals(excludeId)) continue;
            boolean overlap = start.isBefore(res.getEndTime()) && end.isAfter(res.getStartTime());
            if (overlap) {
                throw new RuntimeException("Conflit de réservation : créneau horaire déjà réservé.");
            }
        }
    }
}
