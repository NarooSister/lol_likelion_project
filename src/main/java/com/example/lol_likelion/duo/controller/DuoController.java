package com.example.lol_likelion.duo.controller;

import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.auth.service.UserService;
import com.example.lol_likelion.duo.dto.OfferDto;
import com.example.lol_likelion.duo.dto.PostDto;
import com.example.lol_likelion.duo.entity.Offer;
import com.example.lol_likelion.duo.entity.Post;
import com.example.lol_likelion.duo.service.EvaluationService;
import com.example.lol_likelion.duo.service.OfferService;
import com.example.lol_likelion.duo.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import reactor.netty.http.server.HttpServerRequest;

import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@RequestMapping("duo")
public class DuoController {
    private final OfferService offerService;
    private final PostService postService;
    private final UserService userService;
    private final EvaluationService evaluationService;


    @GetMapping("")
    public String duoHomepage(Model model, Authentication authentication){
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userName = userDetails.getUsername();
            System.out.println("userName = " + userName);
            UserEntity userEntity = userService.getUserByUsername(userName);
            Long userId = userEntity.getId();

            model.addAttribute("userId", userId);
        }
//        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");

        model.addAttribute("posts", postService.readAll());


//        System.out.println("model = " + model);



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
//        System.out.println("userName = " + userName);
        UserEntity userEntity = userService.getUserByUsername(userName);
        Long userId = userEntity.getId();

//        System.out.println("memo: "+memo);
//        System.out.println("my_position = " + my_position);
//        System.out.println("find_position = " + find_position);

//        UserEntity loggedInUser = (UserEntity) session.getAttribute("loggedInUser");



        PostDto postDto = new PostDto();
        postDto.setMemo(memo);
        postDto.setMyPosition(my_position);
        postDto.setFindPosition(find_position);
        postDto.setUserId(userId);
//        System.out.println("userEntity = " + userEntity.toString());
        postDto.setUserEntity(userEntity);

        if (postService.createDuo(postDto) == null){
            redirectAttributes.addFlashAttribute("message",
                    "구인중 혹은 매칭중에는 새 글을 작성할 수 없습니다!");
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
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    )
    {
        PostDto postDto = postService.readPost(postId);
        Long postUserId = postDto.getUserEntity().getId();
        // postId 게시글 하나의 정보
        model.addAttribute("posts",postDto);
        if (authentication != null){
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userName = userDetails.getUsername();
            UserEntity userEntity = userService.getUserByUsername(userName);
            Long enterUserId = userEntity.getId();

            if (!postUserId.equals(enterUserId)){
                redirectAttributes.addFlashAttribute("message",
                        "본인 글이 아닌경우에는 접근 불가 합니다");
                return "redirect:/duo";
            }
        }

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
//        System.out.println("userId = " + userId);
        offerService.deleteOffer(postId, userId);
        return "redirect:/duo";


    }

    @PostMapping("/offer/accept/{offerId}")
    public String acceptOffer(
            @PathVariable("offerId")
            Long offerId,
            Model model
    ){
        offerService.updateStatus(offerId, "수락");
        Offer offer = offerService.readOfferOne(offerId);
        Post post = offer.getPost();
        Long postId = post.getId();
        postService.updateStatus(postId, "매칭중");
        //수락시 다른 요청은 자동으로 삭제
        offerService.deleteAnotherOffer(offerId);

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

    @GetMapping("/offer/result/{postUserId}/{offerUserId}/{postId}/{offerId}")
    public String duoOfferResult(
            @PathVariable("postUserId")
            Long postUserId,
            @PathVariable("offerUserId")
            Long offerUserId,
            @PathVariable("postId")
            Long postId,
            @PathVariable("offerId")
            Long offerId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ){
//        System.out.println("postUserId = " + postUserId);
//        System.out.println("offerUserId = " + offerUserId);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        UserEntity userEntity = userService.getUserByUsername(userName);
        Long enterUserId = userEntity.getId();
        if (!enterUserId.equals(postUserId)){
            redirectAttributes.addFlashAttribute("message",
                    "본인 글이 아닌경우에는 접근 불가 합니다");
            return "redirect:/duo";
        }
        evaluationService.createEvaluation(postUserId, offerUserId, postId);
        postService.updateStatus(postId, "완료");
        offerService.updateStatus(offerId,"완료");

        return "redirect:/duo";
    }
}
