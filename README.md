# 🗒️ [Side Project] weGather (소모임 서비스)
> 지역과 관심사가 비슷한 사람 끼리 모임을 만들 수 있는 서비스


## 🔎 프로젝트 기능
- 회원
    - [x] 사용자, 관리자 구분을 둔다
    - [x] 회원가입
        - [x] id는 유일하고, pw는 4자 이상이어야한다.
    - 로그인
        - [x] id와 pw가 일치해야한다.
        - [x] Spring Security,  Basic Auth 방식을 사용한다.
- 관심사
    - [x] 예시) 게임, 운동, 등..
    - [x] 관심사 마스터 정보를 관리한다.
    - [x] 관리자만 마스터 정보를 추가, 수정, 삭제 할 수 있다.
- 소모임 (/smallGroup)
    - [x] 로그인한 인원은 소모임 생성할 수 있다. (POST)
        - [x] 소모임 제목, 설명, 카테고리(관심사), 동네를 입력받는다.
    - [x] 소모임 정보 수정 (PUT)
        - [x] 소모임장, 관리자만 가능하다.
    - 소모임 조회 (GET)
        - [x] 단건 조회 (GET /{id})
            - [ ] 소모임 정보, 가입자 수, 좋아요 수
        - 목록 조회 (GET)
            - [x] 필터 : 이름, 관심사, 모임장 아이디 
            - [x] 소모임 관심사로 조회
    - 소모임 가입 (/smallGroup/{id}/join)
        - [x] 소모임 가입 (POST)
            - [x] 이미 가입된 인원인지 체크
            - [x] 요청과 동시에 승인
            - [x] 인원 수 제한 (Redis 이용**)
        - [x] 가입 요청 목록 조회 (GET /smallGroup/{id}/join?status=?)
            - [x] 회원정보, 상태, 요청 날짜
            - [x] 모임장, 관리자만 조회 가능
            - [x] 해당 소모임의 요청 목록을 조회


추가 기능

- 인증, 인가 방식을 JWT or OAuth 방식으로 변경한다.

## 🔧 사용 기술 (Tech Stack)
- java 11
- spring
    - springboot 2.7.5
    - spring-security
    - spring-data-jpa
- querydsl 5.0.0
- test
    - junit5
    - assertj
    - rest-assured:4.4.0 
- infra
    - mysql
    - redis
    - s3

---

## 📖 ERD
https://www.erdcloud.com/d/J3yFTcYwZDscXNmSY
![weGather](https://github.com/gyureal/weGather/assets/78974381/f59b336a-1c7d-42a5-bb23-1bc9bd2d9b0a)

---
## 📕 Trouble Shooting
[go to the trouble shooting section](./TROUBLE_SHOOTING.md)








