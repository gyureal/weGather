# weGather (소모임 서비스)

## 개요
- 지역과 관심사가 비슷한 사람 끼리 모임을 만들 수 있는 서비스


## 기능 목록
- 회원
    - 사용자, 관리자 구분을 둔다
    - 회원가입
        - id는 유일하고, pw는 4자 이상이어야한다.
    - 로그인
        - id와 pw가 일치해야한다.
        - Spring Security,  Basic Auth 방식을 사용한다.
- 관심사
    - 예시) 게임, 운동, 등..
    - 관심사 마스터 정보를 관리한다.
    - 관리자만 마스터 정보를 추가, 수정, 삭제 할 수 있다.
- 소모임
    - 로그인한 인원은 소모임 생성할 수 있다.
        - 소모임 제목, 설명, 카테고리(관심사), 동네를 입력받는다.
    - 소모임 정보 수정은 소모임장, 관리자만 가능하다.
    - 소모임 조회
        - 단건 조회
        - 목록 조회
            - 필터 : 카테고리
            - 정렬 : 거리순**, 인기순
            - 목록 조회에는 엘라스틱 서치를 이용해본다.
    - 소모임 가입
        - 인원 수 제한 (Redis 이용**)
    - 소모임 추천
        - 회원이 정한 관심사로 필터, 거리순으로 내림차순, 인기순으로 내림차순 하여 TOP N 추천
- 정모
    - 장소, 시간, 참가비 등 명시 (금액은 단지 명시 용)
    - 추가, 조회, 수정, 삭제
        - 추가, 수정, 삭제 : 소모임장, 관리자
    - 정모 참여
        - 해당 소모임 회원만 참여 가능
        - 인원수 제한은 따로 두지 않음 (추가 기능+)
    - 정모 참여 취소
        - 모임 한시간 전에는 취소할 수 없음
    - 정모 알림
        - 정모 하루 전 알림 (카카오톡 알림**)
        - 스프링 배치를 이용하여 구현한다.


추가 기능

- 인증, 인가 방식을 JWT or OAuth 방식으로 변경한다.
- 소모임간 커뮤니케이션
    - 게시판형 or 실시간 채팅
- 정모 인원 수 제한 두기


---

## ERD
https://www.erdcloud.com/d/J3yFTcYwZDscXNmSY
![weGather](https://github.com/gyureal/weGather/assets/78974381/f59b336a-1c7d-42a5-bb23-1bc9bd2d9b0a)

---
## Trouble Shooting

### 테스트에서 다른 타입 비교 시, 주의할점
두 타입 사이에 필드 수가 다른 경우, 아래와 같이 테스트가 fail이 된다.<br/>
아래의 코드는 회원추가 시, 입력값으로 사용한 joinMemberRequest 와 회원추가 후 결과로 리턴된 memberDto를 recusiveComparison() 을 사용해서 비교하는 코드이다.<br/>
<img width="486" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/94ef7080-1864-4e3d-b636-c56154d412b9"><br/>
타입은 비교하지 않고, 두 타입의 필드의 이름을 기준으로 값을 비교하지만, 필드의 수가 다른 경우에 아래 fail이 된다.<br/>
<img width="855" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/d0be9732-3385-46b0-aadf-3c50659a2caa"><br/>
해결책은 두 타입에서 일치하지 않는 필드는 ignoringFields() 에 명시해서 비교에서 제외시키거나, null 인 컬럼은 비교하지 않는 ignoringActualNullFields()를 추가해 주어야한다.<br/>
<img width="476" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/10504258-e76a-4241-b55f-18c4e06a6d80">




