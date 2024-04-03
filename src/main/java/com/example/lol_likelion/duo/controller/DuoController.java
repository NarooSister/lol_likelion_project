package com.example.lol_likelion.duo.controller;

import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.utils.service.UserService;
import com.example.lol_likelion.duo.dto.OfferDto;
import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.entity.Offer;
import com.example.lol_likelion.duo.entity.Post;
import com.example.lol_likelion.duo.service.OfferService;
import com.example.lol_likelion.duo.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("duo")
public class DuoController {
    private final OfferService offerService;
    private final PostService postService;
    private final UserService userService;


    @GetMapping("")
    public String duoHomepage(Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        System.out.println("userName = " + userName);
        UserEntity userEntity = userService.getUserByUsername(userName);
        Long userId = userEntity.getId();

//        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");
        model.addAttribute("userId", userId);

        model.addAttribute("posts", postService.readAll());


        System.out.println("model = " + model);



        return "duo";

    }
    @GetMapping("/myDuo")
    public String duoWritePage(Model model){
//        model.addAttribute("user_id","userId");
        return "duoWrite";
    }

    @PostMapping("/myDuo")
    public String createDuo(
            @RequestParam("memo")
            String memo,
            @RequestParam("my_position")
            String my_position,
            @RequestParam("find_position")
            String find_position,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        System.out.println("userName = " + userName);
        UserEntity userEntity = userService.getUserByUsername(userName);
        Long userId = userEntity.getId();

//        System.out.println("memo: "+memo);
//        System.out.println("my_position = " + my_position);
//        System.out.println("find_position = " + find_position);
;
//        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");



        PostDto postDto = new PostDto();
        postDto.setMemo(memo);
        postDto.setMyPosition(my_position);
        postDto.setFindPosition(find_position);
        postDto.setUserId(userId);
        System.out.println("userEntity = " + userEntity.toString());
        postDto.setUserEntity(userEntity);

        if (postService.createDuo(postDto) == null){
            redirectAttributes.addFlashAttribute("message", "이미 구인중 입니다");
        }
        postService.createDuo(postDto);

        return "redirect:/duo";

    }

    @PostMapping("/offer/{postId}")
    public String createOffer(
        // user 정보 추후 작성해야함
        @PathVariable("postId")
        Long postId,
        Authentication authentication
    ){
//        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        System.out.println("userName = " + userName);
        UserEntity userEntity = userService.getUserByUsername(userName);
        Long userId = userEntity.getId();


        OfferDto offerDto = new OfferDto();
        offerDto.setApplyUserId(userId);
        offerDto.setUserEntity(userEntity);

        System.out.println(postId);
        offerService.createDuo(postId, offerDto);

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

//        System.out.println(postId);
//        System.out.println(postService.readPost(postId));


//        model.addAttribute("userInfo",postService.readUserInfo(postId));
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
        postService.deletePost(postId);
        return "redirect:/duo";

    }

    @DeleteMapping("/offer/{postId}")
    @Transactional // 해당 어노테이션 추가
    public String deleteOffer(
            @PathVariable("postId")
            Long postId,
            @RequestParam("userId")
            Long userId
    ){
        System.out.println("userId = " + userId);
        offerService.deleteOffer(postId, userId);
        return "redirect:/duo";


    }

    @PostMapping("/offer/accept/{offerId}")
    public String acceptOffer(
            @PathVariable("offerId")
            Long offerId,
            Model model
    ){
        offerService.updateStatus(offerId);
        Offer offer = offerService.readOfferOne(offerId);
        Post post = offer.getPost();
        Long postId = post.getId();

        model.addAttribute("posts", post);
        model.addAttribute("offers", offer);


        return "offerResult";
    }

    @DeleteMapping("/offer/deny/{offerId}")
    @Transactional
    public String denyOffer(
            @PathVariable("offerId")
            Long offerId
    ){
        Offer offer = offerService.readOfferOne(offerId);
        offerService.deleteOffer(offerId);
        Post post = offer.getPost();
        Long postId = post.getId();

        return String.format("redirect:/duo/myDuo/%d",postId);
    }
}
