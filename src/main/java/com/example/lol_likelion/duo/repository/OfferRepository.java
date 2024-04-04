package com.example.lol_likelion.duo.repository;

import com.example.lol_likelion.duo.entity.Offer;
import com.example.lol_likelion.duo.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    void deleteByPost (Post post);

    List<Offer> findByPost(Post post);

    Optional<Offer> findByPostAndApplyUserId(Post post, Long userId);

    void deleteOfferByPostAndApplyUserId(Post post, Long userId);

    @Transactional
    void deleteByIdNot(Long offerId);
}
