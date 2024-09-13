# 🎮 게임 전적 사이트 '롤쟁이'

![메인 화면](https://github.com/NarooSister/lol_likelion_project/assets/150361471/1b994566-ef42-4263-b6d2-d004b2971b86)

멋쟁이 사자처럼 백엔드 스쿨 8기에서 진행한 1차 종합 프로젝트 입니다.

<br>

## 👋 프로젝트 소개
나만의 칭호와 뱃지를 얻어 공유하며 마음이 맞는 친구를 만나는 게임 전적 사이트입니다.
리그오브레전드 전적 확인 및 커뮤니티 기능을 가지고 있습니다.

<br>

## 📆 일정


**기간: 2024.03.13 ~ 2024.04.05**

<br>


## 1. 개발 환경
- Back-end : Java 17, Spring Boot 3.2.3, Spring Data JPA, Spring Security, JWT, Riot API
- Front-end : Javascript, HTML/CSS, Thymeleaf, BootStrap
- Database : MySQL, H2
- tool : Notion, google docs
- design : [피그마](https://www.figma.com/board/5y6GrzCfvc6UuglPOcZFig/%ED%99%94%EB%A9%B4-%EA%B8%B0%ED%9A%8D?node-id=0-1&t=2J3C6Hd1lPEKipvN-0)

<br>

## 2. 주요 기능
### 사용자 인증 및 권한 처리

- **인증 방식**
  - JWT 토큰을 활용한 인증
  
- **로그인**
  - 아이디, 비밀번호를 사용하여 로그인

- **회원가입 시 소환사 닉네임 인증**
  - 라이엇 로그인 연동 API가 라이엇 승인 문제로 불가능하기 때문에 라이엇 API로 회원가입을 진행
  - 따라서 라이엇 API로 Puuid(암호화 된 긴 고유 아이디)를 받아서 중복을 체크함
  - 이 방식은 회원가입을 시도하는 사용자와 라이엇아이디의 실제 소유자가 일치하는지 검증할 수 없다는 문제가 있음
  - 이후 라이엇 로그인 연동 API가 서비스화 된다면 변경 가능

### 유저 검색 기능
- Riot Api를 이용해 모든 LoL 유저의 게임 전적을 확인
- 실제 롤 인게임 프로필 이미지, 티어, 플레이 한 챔피언, 게임 전적을 가져옴
  

### 듀오 게시판 기능

- 듀오 구하기 게시글 작성 및 검색
- 듀오 제안, 듀오 매칭
- 게임이 끝난 뒤 신뢰 점수 평가
- 작성자의 선호 챔피언, 티어, 포지션 등 가져옴

### 팔로우, 뱃지 기능
- 회원가입 된 유저 간의 Follower, Following 기능
- 도전과제 업적 달성으로 얻은 뱃지를 유저 페이지에서 공유 가능

<br>

## 3. 주요 기능 시연
### ✅아이디 검색
![아이디 검색](https://github.com/NarooSister/lol_likelion_project/assets/150361471/0a31d647-b2d2-4286-90ef-2eef7acf7620)

- Riot API를 이용해 모든 유저의 게임 전적을 확인할 수 있습니다.
- 유저 페이지에서는 최근 게임 전적과 유저가 설정한 대표 뱃지를 확인할 수 있습니다.



### ✅회원가입과 로그인
![회원가입, 로그인](https://github.com/NarooSister/lol_likelion_project/assets/150361471/42d37ba4-9a05-4372-8c3c-ccbc4cb9bc75)

- 유저 정보를 입력하고 회원가입을 할 수 있습니다.
- Riot API를 통해 입력 받은 유저 닉네임이 리그오브레전드의 실제 유저 아이디인지 확인합니다.



### ✅마이페이지 - 팔로잉, 팔로워 목록 확인
![팔로잉목록](https://github.com/NarooSister/lol_likelion_project/assets/150361471/40fefd06-915a-4eef-9311-892266f6930d)

- 마이 페이지에서 팔로인, 팔로워 목록 확인이 가능합니다.
- 편의성을 위해 팝업으로 구현하였습니다.



### ✅마이 페이지 - 정보 수정
![비밀번호 변경](https://github.com/NarooSister/lol_likelion_project/assets/150361471/6e45a5e3-5192-4dd9-8cd9-a2dfb3a83df9)

- 이메일, 전화 번호 수정이 가능합니다.
- 비밀번호 수정 페이지로 이동하여 비밀번호 수정이 가능합니다.



### ✅마이 페이지 - 소환사 닉네임 변경
![아이디변경](https://github.com/NarooSister/lol_likelion_project/assets/150361471/c61bbe86-63e6-4fec-8d0e-75c411d96fce)

- 연동되는 소환사 닉네임 변경이 가능합니다.
- 실제 존재하는 리그오브레전드의 아이디인지 검증합니다. 



### ✅마이 페이지 - 대표 뱃지 설정
![대표 뱃지](https://github.com/NarooSister/lol_likelion_project/assets/150361471/f7f4129f-ca0d-4236-8063-2d3cafac6605)

- 유저 페이지에 띄우는 대표 뱃지 두 가지를 설정합니다.
- 뱃지는 다양한 도전과제를 통해 획득할 수 있습니다.
- 각각의 도전과제는 유저의 게임 전적 데이터에서 가져오고, 전적 업데이트 시 달성을 체크합니다.


### ✅듀오게시판 - 듀오 검색
![듀오검색](https://github.com/NarooSister/lol_likelion_project/assets/150361471/38f93850-9038-410d-a68d-99e02a620423)

- 듀오 게시판에서 듀오를 검색합니다.
- 티어, 포지션, 구인 상태 필터를 통해 검색할 수 있습니다.
- 게시글에는 티어, 포지션, 최근 사용한 챔피언, 구인 메모 등이 표시됩니다.


### ✅듀오게시판 - 글 작성 실패
![글작성실패](https://github.com/NarooSister/lol_likelion_project/assets/150361471/ecb0b9b8-a0c6-4259-801f-8303648cfea7)

- 듀오 게시판에서 새 글 작성을 시도합니다.
- 현재 구인 중인 글이 있을 때에는 글을 작성할 수 없습니다.


### ✅듀오게시판 - 끌어올리기, 글 삭제, 새 글 작성
![끌올,새글작성](https://github.com/NarooSister/lol_likelion_project/assets/150361471/aebd8a30-429a-4963-936e-d173d2704711)

- 끌어올리기 기능은 작성된 글을 최신 게시글로 업데이트 하여 맨 위로 올려줍니다.
- 작성한 게시글을 삭제할 수 있습니다.(작성자만 가능)
- 현재 구인중인 글이 없을 때에는 새 글 작성이 가능합니다.
- 포지션과 구인 메모를 입력한 뒤 새 글을 작성합니다.


### ✅듀오 게시판 - 매칭 수락 및 거절
![매칭성공](https://github.com/NarooSister/lol_likelion_project/assets/150361471/1b3b177e-cb79-4274-8fb8-ca70b2a31a25)

- 내 게시글에 달린 제안을 확인한 뒤 수락을 누르면 매칭이 이루어집니다.
- 게임을 마친 뒤 매칭 상대를 평가할 수 있습니다.


### ✅매칭 평가와 칭찬 뱃지
![칭찬뱃지](https://github.com/NarooSister/lol_likelion_project/assets/150361471/7735dea4-04c0-4f1b-be73-5f2445d49be3)

- 게임을 마친 뒤 평가하기에서 상대에게 신뢰 점수를 줄 수 있습니다.
- 신뢰 점수에 따라 칭찬 뱃지 또는 비매너 뱃지를 얻게되며, 유저 페이지의 첫번째 뱃지에 등록됩니다.
- 영상에서는 신뢰 점수를 얻는 과정을 볼 수 있습니다. 신뢰 점수 19점에서 20점으로 바뀌었을 때 업데이트 시 뱃지의 종류가 변경됩니다.




## 4. 프로젝트 진행 과정
### 💡 Riot Api 문서화 작업
<img src="https://github.com/NarooSister/lol_likelion_project/assets/150361471/e936d5f7-5ba7-4471-ae94-d5b57ec89be6" width="350" height="400"/>
<img src="https://github.com/NarooSister/lol_likelion_project/assets/150361471/e50b126b-d2e6-478c-be9d-6b96577922b5" width="350 height="400"/>

- Riot Api를 Notion으로 문서화하여 사용하였습니다.
- 프로젝트 진행 시 필요한 데이터를 미리 확인하고 엔드포인트, 출력값 등을 정리했습니다.



### 💡 뱃지 기능
<img src="https://github.com/NarooSister/lol_likelion_project/assets/150361471/f1ec2b49-c5ad-473f-9c7e-12c38789a12b" width="350" height="400"/>

- API를 통해 게임 전적 데이터를 DB에 저장하고, 뱃지 조건을 검증하여 자동으로 뱃지를 수여하는 시스템을 구현하였습니다.
- 유저가 게임 전적 업데이트 버튼을 누를 때마다 뱃지 수여 조건을 확인하고, 연속 게임 플레이, 승리, 킬 수와 같은 조건에 맞춰 뱃지를 자동으로 부여합니다.
- 대표 뱃지를 선택하고 설정하는 기능을 통해 유저가 특정 뱃지를 프로필에 대표로 설정할 수 있도록 구현하였습니다.
- 다음은 뱃지 기능 구현 중 작성한 주석의 일부입니다. 업데이트 과정을 설명하였습니다.(user/service/BadgeService.java)
  
  ```
    // ==== user-page에서 업데이트 버튼을 누르면 일어나는 과정 ====
    // userEntity의 최근 업데이트 된 시간 체크해서, 그 이후의 최근 전적부터 quest 가져와서 숫자 ++
    // 뱃지 조건 달성 확인한 뒤 새로 획득한 뱃지가 있으면 뱃지 상태 저장
    // userEntity안의 tier, dailyGameCount, profileIconId 변경 사항 수정
    // updatedAt 현재 시간으로 변경
    // redirect로 최근 전적 다시 가져옴.
    // 최근 전적 가져와서 사용자 정보 업데이트 하면서 뱃지 검증 시작
    // 획득 조건 달성 확인 후 저장하기 ```



### 💡 ERD
<img src="https://github.com/NarooSister/lol_likelion_project/assets/150361471/3987ea7d-e26d-4943-9387-72843cfeb6d5" width="600" height="600"/>


### 💡 WBS

<img src="https://github.com/NarooSister/lol_likelion_project/assets/150361471/d72a3c3d-772d-4b53-b736-4b91461884bd" width="600" height="400"/>

