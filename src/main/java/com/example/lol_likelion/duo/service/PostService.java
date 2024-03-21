package com.example.lol_likelion.duo.service;

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
}
