package com.example.lol_likelion.duo.controller;

import com.example.lol_likelion.duo.service.OfferService;
import com.example.lol_likelion.duo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("duo")
public class DuoController {
    private final OfferService offerService;
    private final PostService postService;


    @GetMapping("/")
    public String duoHomepage(Model model){
        model.addAttribute("posts", postService.readAll());
        return "duo";
    }
}
