package com.example.lol_likelion.auth.config;

import com.example.lol_likelion.auth.jwt.CustomAuthenticationEntryPoint;
import com.example.lol_likelion.auth.jwt.JwtTokenFilter;
import com.example.lol_likelion.auth.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService manager;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                auth -> auth
                        .requestMatchers("/users/my-page")
                        .authenticated()

                        .requestMatchers("/error", "https://asia.api.riotgames.com/**","/users11","/users/update")
                        .permitAll()
                        .requestMatchers(
                                "/users/login",
                                "/users/register"
                        )
                        .anonymous()
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
                        AuthorizationFilter.class
                );



        return http.build();
    }

}
