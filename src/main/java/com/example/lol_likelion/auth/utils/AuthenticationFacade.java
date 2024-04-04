package com.example.lol_likelion.auth.utils;



import com.example.lol_likelion.auth.dto.CustomUserDetails;
import com.example.lol_likelion.auth.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationFacade {
    //사용자 인증 여부 확인하여 인증 객체 반환
    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    //사용자 정보 추출하여 사용자 엔티티 반환
    public UserEntity extractUser() {
        CustomUserDetails userDetails = (CustomUserDetails) getAuth().getPrincipal();
        return userDetails.getEntity();
    }

    public Optional<UserEntity> extractOptionalUser() {
        Authentication auth = getAuth();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails.getEntity());
        }
        return Optional.empty();
    }
}
