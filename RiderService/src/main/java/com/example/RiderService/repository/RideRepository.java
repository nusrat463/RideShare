package com.example.RiderService.repository;

import com.example.RiderService.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride,Long> {
}
