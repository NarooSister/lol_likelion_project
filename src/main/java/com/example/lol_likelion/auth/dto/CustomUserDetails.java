package com.example.lol_likelion.auth.dto;

import com.example.lol_likelion.auth.entity.UserEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;

    @Getter
    private UserEntity entity;

    public static CustomUserDetails fromEntity(UserEntity entity)
    {
        return CustomUserDetails.builder()
                .entity(entity)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(entity.getRoles().split(","))
                .map(role -> (GrantedAuthority) () -> role)
                .toList();
    }

    @Override
    public String getPassword() {
        return this.entity.getPassword();
    }

    @Override
    public String getUsername() {
        return this.entity.getUsername();
    }

    public Long getId() {
        return this.entity.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
