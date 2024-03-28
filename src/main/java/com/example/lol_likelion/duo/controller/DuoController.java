package com.example.lol_likelion.duo.controller;

import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.service.OfferService;
import com.example.lol_likelion.duo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("duo")
public class DuoController {
    private final OfferService offerService;
    private final PostService postService;


    @GetMapping("")
    public String duoHomepage(Model model){
        model.addAttribute("posts", postService.readAll());
        return "duo";

    }
    @GetMapping("/myDuo")
    public String duoWritePage(Model model){
        model.addAttribute("user_id",postService.getUserId());
        return "duoWrite";
    }

    @PostMapping("/myDuo")
    public String createDuo(
            @RequestParam("memo")
            String memo,
            @RequestParam("my_position")
            String my_position,
            @RequestParam("find_position")
            String find_position
    ){
//        System.out.println("memo: "+memo);
//        System.out.println("my_position = " + my_position);
//        System.out.println("find_position = " + find_position);
;

        PostDto postDto = new PostDto();
        postDto.setMemo(memo);
        postDto.setMyPosition(my_position);
        postDto.setFindPosition(find_position);

        postService.createDuo(postDto);

        return "redirect:/duo";

    }

    @PostMapping("/offer/{postId}")
    public String createOffer(
        // user 정보 추후 작성해야함
        @PathVariable("postId")
        Long postId
    ){

        System.out.println(postId);
        offerService.createDuo(postId);

        return "redirect:/duo";
    }

    // 내 게시글 단일 조회 - GET
    @GetMapping("/myDuo/{postId}")
    public String checkPost(
            @PathVariable("postId")
            Long postId,
            Model model
    )
    {
        // postId 게시글 하나의 정보
        model.addAttribute("posts",postService.readPost(postId));

        System.out.println(postId);
        System.out.println(postService.readPost(postId));
        return "/duoDetail";
    }

    // 게시글 삭제
    // ERROR : 수정중...0328
    @DeleteMapping("/myDuo/{postId}")
    public String deletePost(
        @PathVariable("postId")
        Long postId
    ){
//        offerService.deleteOfferInPost(postId);
//        postService.deletePost(postId);
        return "redirect:/duo";

    }
}
