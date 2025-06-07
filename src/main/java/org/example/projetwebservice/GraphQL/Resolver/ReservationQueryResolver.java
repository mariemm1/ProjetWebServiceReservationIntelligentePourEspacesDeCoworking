package org.example.projetwebservice.GraphQL.Resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.example.projetwebservice.Model.Reservation;
import org.example.projetwebservice.Repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationQueryResolver implements GraphQLQueryResolver {

    private final ReservationRepository reservationRepository;

    public ReservationQueryResolver(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }
}