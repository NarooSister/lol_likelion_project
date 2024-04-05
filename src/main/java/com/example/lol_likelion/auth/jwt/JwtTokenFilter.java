package com.example.lol_likelion.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
}
