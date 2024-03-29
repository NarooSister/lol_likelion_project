package com.example.lol_likelion.user;


import com.example.lol_likelion.auth.entity.UserEntity;
import com.example.lol_likelion.user.entity.BadgeState;
import com.example.lol_likelion.user.repository.BadgeRepository;
import com.example.lol_likelion.user.repository.UserBadgeRepository;
import com.example.lol_likelion.auth.repository.UserRepository;
import com.example.lol_likelion.user.entity.Badge;
import com.example.lol_likelion.user.entity.UserBadge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    //user-page에서 업데이트 버튼을 누르면

    // 최근 전적 가져와서 사용자 정보 업데이트 하면서 뱃지 검증 시작
    // 획득 조건 달성 확인 후 저장하기
    //EX) 칭찬 뱃지



    //update를 누르면 사용자 정보에서 뱃지 구성에 필요한 정보 가져와서 저장하기
    public void saveQuestToUser(UserEntity user){
        //

    }








    //배지 부여하여 저장하는 과정 메소드로 추출
    public void awardBadgeToUser(UserEntity user, Badge badge, BadgeState state){
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setState(state);
        userBadgeRepository.save(userBadge);
    }
    public void trustBadge(Long userId){
        // -뱃지 Id 부분 하드코딩 어떻게 고칠지 고민..
        //아이디 존재하는지 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        Badge niceBadge = badgeRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("뱃지를 찾을 수 없습니다."));
        Badge badBadge = badgeRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("뱃지를 찾을 수 없습니다."));

        //뱃지를 이미 획득했는지 확인
        boolean niceBadgeAwarded = userBadgeRepository.existsByUserIdAndBadgeId(userId, 1L);
        boolean badBadgeAwarded = userBadgeRepository.existsByUserIdAndBadgeId(userId, 2L);

        // 칭찬 점수 가져오기
        Integer trustScore = user.getTrustScore();
        System.out.println("칭찬 점수: " + trustScore);
        
        if(trustScore>= 20 && niceBadgeAwarded ){
            // 20점이 넘는 경우 친절한 뱃지 부여
            awardBadgeToUser(user, niceBadge, BadgeState.ACQUIRED);
        } else if (trustScore <= -10 && badBadgeAwarded) {
            // -10점 이하인 경우 나쁜 뱃지 부여
            awardBadgeToUser(user, badBadge, BadgeState.ACQUIRED);
        }
    }






    // READ ALL - 모든 뱃지 보여주기

    //대표 뱃지 보여주기

    //대표 뱃지 설정하기

    //신뢰 점수에 따른 칭찬 뱃지 설정하기

    //뱃지 갯수에 따라 level update 하기







}
