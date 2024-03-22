package com.example.lol_likelion.auth.jwt;

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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("try jwt filter");

       /* //헤더에서 jwt 가져오는 방법
        String authHeader
                = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split(" ")[1];
            if (jwtTokenUtils.validate(token)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                String username = jwtTokenUtils
                        .parseClaims(token)
                        .getSubject();

                log.info("!!!");

                UserDetails userDetails = service.loadUserByUsername(username);

                log.info(userDetails.toString());

                for (GrantedAuthority authority :userDetails.getAuthorities()) {
                    log.info("authority: {}", authority.getAuthority());
                }

                AbstractAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                token,
                                userDetails.getAuthorities()
                        );

                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                log.info("set security context with jwt");
            }
            else {
                log.warn("jwt validation failed");
            }
        }
        filterChain.doFilter(request, response);*/


       /* // 쿠키에서 토큰을 추출
        if (token != null && jwtTokenUtils.validate(token)) {
            String username = jwtTokenUtils.parseClaims(token).getSubject();
            UserDetails userDetails = service.loadUserByUsername(username);

            AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } else {
            log.warn("jwt validation failed");}

        filterChain.doFilter(request, response);*/


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
    }


    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

}
