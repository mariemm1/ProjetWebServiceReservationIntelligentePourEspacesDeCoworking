package org.example.projetwebservice.GraphQL.Resolver;

import org.example.projetwebservice.GraphQL.Input.ReservationInput;
import org.example.projetwebservice.Model.Reservation;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.ReservationRepository;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.example.projetwebservice.Services.AuthenticatedUserService;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SecureReservationMutationResolver {

    private final ReservationRepository reservationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public SecureReservationMutationResolver(ReservationRepository reservationRepository,
                                             WorkspaceRepository workspaceRepository,
                                             AuthenticatedUserService authenticatedUserService) {
        this.reservationRepository = reservationRepository;
        this.workspaceRepository = workspaceRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @MutationMapping
    public Reservation createMyReservation(@Argument ReservationInput input) {
        User currentUser = authenticatedUserService.getAuthenticatedUser();

        List<Reservation> existing = reservationRepository.findByWorkspaceIdAndDate(input.getWorkspaceId(), input.getDate());

        for (Reservation res : existing) {
            boolean overlap = input.getStartTime().isBefore(res.getEndTime())
                    && input.getEndTime().isAfter(res.getStartTime());
            if (overlap) {
                throw new RuntimeException("Conflit de réservation : créneau horaire déjà réservé.");
            }
        }

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
}
