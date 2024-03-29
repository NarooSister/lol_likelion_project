package com.example.lol_likelion.auth.dto;


import java.util.Set;
//아직 작성 중
public class UserPageDto {
    //비회원, 회원 공통 요소
    private String gameName;
    private String tagLine;
    private Integer profileIconId;
    private String tier;
    private Integer kda;
    private boolean win;

    //회원만 보여주는 요소
    private Integer level;
    private Set<String> badges;       //대표 뱃지 3가지





}
