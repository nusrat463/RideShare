package com.example.RiderService.repository;

import com.example.RiderService.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, String> {
    @Query(value = "SELECT * FROM rider WHERE available = 1", nativeQuery = true)
    List<Rider> finAvailableRiders();


    Optional<Rider> findFirstByAvailableTrue();
}

