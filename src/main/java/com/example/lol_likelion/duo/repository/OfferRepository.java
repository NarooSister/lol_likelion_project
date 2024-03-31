package com.example.lol_likelion.duo.repository;

import com.example.lol_likelion.duo.entity.Offer;
import com.example.lol_likelion.duo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    void deleteByPost (Post post);
}