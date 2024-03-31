package com.example.lol_likelion.duo.service;

import com.example.lol_likelion.auth.dto.UserInfoDto;
import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.entity.Offer;
import com.example.lol_likelion.duo.entity.Post;
import com.example.lol_likelion.duo.repository.OfferRepository;
import com.example.lol_likelion.duo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final OfferRepository offerRepository;

    //전체 글 읽기
    public List<PostDto> readAll(){
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post: postRepository.findAll()) {
            postDtos.add(PostDto.fromEntity(post));
        }
        return postDtos;
    }

    //
//    public Long getUserId(){
//        UserInfoDto userInfoDto = new UserInfoDto();
//
//        return userInfoDto.getId();
//    }

    public PostDto createDuo(PostDto postDto){
        Post post = new Post();

        if (postRepository.existsByStatusAndUserId("구인중", postDto.getUserId())){
            System.out.println("이미 구인중");
            return null;
        }else {
            post.setMemo(postDto.getMemo());
            post.setMyPosition(postDto.getMyPosition());
            post.setFindPosition(postDto.getFindPosition());
            post.setStatus("구인중");
            post.setUserId(postDto.getUserId());
            return PostDto.fromEntity(postRepository.save(post));
        }
        //TODO 같은 유저가 매칭이 완료되지 않은 상태에서 글 작성하면 작성 불가 (자동으로 업데이트 되게)

    }

    public PostDto readPost(Long postId){
        return PostDto.fromEntity(postRepository.findById(postId).orElseThrow());
    }


    public void deletePost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow();

        postRepository.delete(post);
    }

    public UserInfoDto readUserInfo(Long postId){
        UserInfoDto userInfoDto = new UserInfoDto();
        //게시글 하나 선택
        Post post = postRepository.findById(postId).orElseThrow();
        //그 게시물의 offer 찾기
        List<Offer> offers = offerRepository.findByPost(post);

        //사용자 id를 저장할 목록
        List<Long> userIds = new ArrayList<>();

        //userid 추출
        for (Offer offer : offers){
            userIds.add(offer.getApplyUserId());
        }


        //TODO UserInfoDto?? userDto ??



        return userInfoDto;
    }

}
