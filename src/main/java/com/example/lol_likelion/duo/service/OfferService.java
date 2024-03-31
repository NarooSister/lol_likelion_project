package com.example.lol_likelion.duo.service;

import com.example.lol_likelion.duo.dto.OfferDto;
import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.entity.Offer;
import com.example.lol_likelion.duo.entity.Post;
import com.example.lol_likelion.duo.repository.OfferRepository;
import com.example.lol_likelion.duo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final PostRepository postRepository;
    public OfferDto createDuo(Long postId, OfferDto offerDto){
        Offer offer = new Offer();
        Post post = postRepository.findById(postId).orElseThrow();

        offerDto.setStatus("신청함");
        offerDto.setApplyUserId(offerDto.getApplyUserId());
        offerDto.setPost(post);
        offer.setStatus(offerDto.getStatus());
        offer.setApplyUserId(offerDto.getApplyUserId());
        offer.setPost(offerDto.getPost());
        offer.setApplyUserId(offerDto.getApplyUserId());

        return OfferDto.fromEntity(offerRepository.save(offer));
    }

    public void deleteOffer(Long postId, Long userId){
        Post post = postRepository.findById(postId).orElseThrow();
        offerRepository.deleteOfferByPostAndApplyUserId(post, userId);
    }
}
