package com.example.lol_likelion.duo.repository;

import com.example.lol_likelion.duo.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}
