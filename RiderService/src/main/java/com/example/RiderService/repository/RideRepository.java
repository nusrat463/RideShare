package com.example.RiderService.repository;


import com.example.RiderService.model.Ride;
import com.example.RiderService.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride,Long> {
    @Query("SELECT COALESCE(MAX(r.id), 0) FROM Ride r")
    Long findMaxId();
}
