package com.example.lol_likelion.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

     private String password;
     private String gameName;
     private String tagLine;
     private String email;
     private String phone;

}
