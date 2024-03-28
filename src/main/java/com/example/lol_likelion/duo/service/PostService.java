package com.example.lol_likelion.duo.service;

import com.example.lol_likelion.auth.dto.UserInfoDto;
import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.entity.Post;
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

    //전체 글 읽기
    public List<PostDto> readAll(){
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post: postRepository.findAll()) {
            postDtos.add(PostDto.fromEntity(post));
        }
        return postDtos;
    }

    //
    public Long getUserId(){
        UserInfoDto userInfoDto = new UserInfoDto();

        return userInfoDto.getId();
    }

    public PostDto createDuo(PostDto postDto){
        Post post = new Post();
        post.setMemo(postDto.getMemo());
        post.setMyPosition(postDto.getMyPosition());
        post.setFindPosition(postDto.getFindPosition());

        return PostDto.fromEntity(postRepository.save(post));
    }

    public PostDto readPost(Long postId){
        return PostDto.fromEntity(postRepository.findById(postId).orElseThrow());
    }

    public void deletePost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow();

        postRepository.delete(post);
    }
}
