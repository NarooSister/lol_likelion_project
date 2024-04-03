package com.example.lol_likelion.auth.config;

import com.example.lol_likelion.auth.jwt.JwtTokenFilter;
import com.example.lol_likelion.auth.jwt.JwtTokenUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService manager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(
                                        "/my-page",
                                        "/logout",
                                        "/users",
                                        "/users/password",
                                        "/users/game-name",
                                        "/duo/myDuo",
                                        "/duo/offer/*",
                                        "/duo/myDuo/{postId}",
                                        "/duo/offer/{postId}",
                                        "/duo/offer/accept/{offerId}",
                                        "/duo/offer/deny/{offerId}"

                                )
                                .authenticated()
                              //  .hasAnyAuthority("ROLE_USER")
                                .requestMatchers(
                                        "/",
                                        "/error",
                                        "/login",
                                        "/register",
                                        "/authentication-fail",
                                        "/authorization-fail",
                                        "/users/{gameName}/{tagLine}",
                                        "/users/search",
                                        "/api/users11",
                                        "/duo"

                                )
                                .permitAll()
                                .anyRequest()
                                .permitAll()
                )

//        .formLogin(
//                formLogin -> formLogin
//                        .loginPage("/users/login")
//                        .defaultSuccessUrl("/users/my-page")
//                        .failureUrl("/users/login")
//        )
//
//                .logout(
//                        logout -> logout
//                                .logoutUrl("/users/logout")
//                                .logoutSuccessUrl("/users/login")
//                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtTokenFilter(
                                jwtTokenUtils,
                                manager
                        ),
                        UsernamePasswordAuthenticationFilter.class
                        //AuthorizationFilter.class
                ).exceptionHandling(exceptionConfig ->
                        exceptionConfig
                                //인증 실패
                                .authenticationEntryPoint((request, response, authException) -> {
                                    //api 요청의 경우 실패하면 error 출력
                                    //화면 요청의 경우 실패하면 에러 페이지로 redirect
                                    if (!request.getRequestURI().contains("api")) {
                                        response.sendRedirect("/authentication-fail");
                                    }
                                })
                                //인가 실패
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    if (!request.getRequestURI().contains("api")) {
                                        response.sendRedirect("/authorization-fail");
                                    }
                                })
                );


        return http.build();
    }

}
