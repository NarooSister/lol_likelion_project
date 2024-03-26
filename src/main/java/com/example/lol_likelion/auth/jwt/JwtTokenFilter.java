package com.example.lol_likelion.auth.jwt;

import com.example.lol_likelion.auth.dto.CustomUserDetails;
import com.example.lol_likelion.auth.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService service;

    public JwtTokenFilter(
            JwtTokenUtils jwtTokenUtils,
            UserDetailsService service
    ) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.service = service;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtTokenFilter: Filtering request for path: {}", request.getRequestURI());
        String jwtToken = null;

        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
        } else if (request.getCookies() != null) { // 쿠키에서 토큰 추출
            Cookie jwtTokenCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("token"))
                    .findFirst()
                    .orElse(null);
            if (jwtTokenCookie != null) {
                jwtToken = jwtTokenCookie.getValue();
            }
        }

        // 토큰 검증 및 인증 정보 설정
        if (jwtToken != null && jwtTokenUtils.validate(jwtToken)) {
            String username = jwtTokenUtils.parseClaims(jwtToken).getSubject();
            log.info("username from parseClaims.getsubject :"+username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = service.loadUserByUsername(username);
                log.info("username from loadUserByUsername:"+userDetails);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                log.info("getAuthorities :" +userDetails.getAuthorities().toString());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            log.warn("JWT Token is null or invalid");
        }

        filterChain.doFilter(request, response);
    }

/*  @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("try jwt filter");

        //두 경우 다 보는 로직
        String token = null;

        // 헤더에서 토큰을 추출
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring("Bearer ".length());
        }

        // 쿠키에서 토큰을 추출 (헤더에서 토큰을 찾지 못한 경우)
        if (token == null) {
            token = extractTokenFromCookies(request);
        }

        // 토큰 검증 및 인증 정보 설정
        if (token != null && jwtTokenUtils.validate(token)) {
            String username = jwtTokenUtils.parseClaims(token).getSubject();
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = service.loadUserByUsername(username);
                AbstractAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            log.warn("jwt validation failed");
        }

        filterChain.doFilter(request, response);
    }*/


  /*  private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }*/

}
