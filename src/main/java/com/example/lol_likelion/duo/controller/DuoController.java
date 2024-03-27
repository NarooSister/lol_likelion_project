package com.example.lol_likelion.duo.controller;

import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.service.OfferService;
import com.example.lol_likelion.duo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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


}
