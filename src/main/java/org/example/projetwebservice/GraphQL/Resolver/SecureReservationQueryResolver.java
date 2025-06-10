package org.example.projetwebservice.GraphQL.Resolver;

import org.example.projetwebservice.Model.Reservation;
import org.example.projetwebservice.Repository.ReservationRepository;
import org.example.projetwebservice.Services.AuthenticatedUserService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SecureReservationQueryResolver {

    private final ReservationRepository reservationRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public SecureReservationQueryResolver(ReservationRepository reservationRepository,
                                          AuthenticatedUserService authenticatedUserService) {
        this.reservationRepository = reservationRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @QueryMapping
    public List<Reservation> getMyReservations() {
        return reservationRepository.findByUserId(authenticatedUserService.getAuthenticatedUser().getId());
    }
}

