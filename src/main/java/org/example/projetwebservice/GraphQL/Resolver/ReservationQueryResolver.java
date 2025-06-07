package org.example.projetwebservice.GraphQL.Resolver;

import org.example.projetwebservice.Model.Reservation;
import org.example.projetwebservice.Repository.ReservationRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReservationQueryResolver {

    private final ReservationRepository reservationRepository;

    public ReservationQueryResolver(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @QueryMapping
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    @QueryMapping
    public Reservation getReservationById(@Argument Long id) {
        return reservationRepository.findById(id).orElse(null);
    }
}
