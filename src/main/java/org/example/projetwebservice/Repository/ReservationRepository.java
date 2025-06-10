package org.example.projetwebservice.Repository;

import org.example.projetwebservice.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByWorkspaceIdAndDate(Long workspaceId, LocalDate date);

    List<Reservation> findByUserId(Long id);
}
