# 🗒️ [Side Project] weGather (소모임 서비스)
> 지역과 관심사가 비슷한 사람 끼리 모임을 만들 수 있는 서비스 입니다.

## ⚙ 개발 환경
```
운영체제 :  Mac OS
통합개발환경(IDE) : IntelliJ
JDK 버전 : JDK 11
데이터 베이스 : Mysql
빌드 툴 : Gradle-7.6.1
관리 툴 : GitHub
```

## 🔧 사용 기술 (Tech Stack)
### Development
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=Java&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">  <img src="https://img.shields.io/badge/spring data jpa-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/query dsl-007DB8?style=for-the-badge&logo=spring jpa&logoColor=white">

### Test
<img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white"> <img src="https://img.shields.io/badge/assertj-A5915F?style=for-the-badge&logo=assertj&logoColor=white"> <img src="https://img.shields.io/badge/rest assured-006272?style=for-the-badge&logo=rest-assured&logoColor=white">
    
### DB
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/amazon s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">

### DevOps
<img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=mamazonec2&logoColor=white"> <img src="https://img.shields.io/badge/amazon rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> <img src="https://img.shields.io/badge/github actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/amazon s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> <img src="https://img.shields.io/badge/amazone CodeDeploy-072240?style=for-the-badge&logo=&logoColor=white">

## 📌 Backend Architecture
<img width="876" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/7934ff9e-20f5-4f94-a445-42066cd4c8d9">


## 🔎 프로젝트 기능
### [ 회원 ]
- 회원가입
   - 회원가입 요청 시, 이메일로 `본인인증메일`이 전송됩니다.
   - 이메일로 전송된 링크를 클릭 시, 회원가입이 완료됩니다.
   - `본인인증메일 재전송`이 가능합니다.
- 회원 정보 수정
   - 로그인 된 본인의 정보만 수정 가능합니다. 
   - 회원의 정보를 수정합니다.
- 회원 관심사 수정


### [ 관심사 ]
- 관심사를 관리(추가, 수정, 삭제)합니다.
   - 관리자만 가능합니다.
- 관심사 검색
   - 검색어를 입력하여 관심사를 조회합니다.
- 관심사 자동완성
   - 관심사 입력 시, 자동완성 목록을 조회합니다. 

### [ 소모임 ]
- 소모임 생성
   - 회원은 소모임을 생성할 수 있습니다.
- 소모임 정보 수정
   - 소모임장은 소모임의 정보를 수정할 수 있습니다.
- 소모임 삭제
   - 소모임장은 소모임을 삭제할 수 있습니다.
- 소모임 관심사 수정
   - 소모임장은 소모임의 관심사를 수정할 수 있습니다.
- 소모임 좋아요
   - 모든 회원은 소모임에 좋아요를 표시할 수 있습니다.
   - 한 회원은 하나의 소모임에만 좋아요를 표시할 수 있습니다.

### [ 소모임 가입 ]
- 소모임 가입 요청
   - 소모임장이 아닌 회원은 `소모임에 가입 요청`을 보낼 수 있습니다.
   - 해당 소모임의 `최대회원수`를 `초과`하면 요청할 수 없습니다.
- 소모임 가입 요청 관리
   - 소모임장은 소모임 가입 요청을 `승인/반려` 할 수 있습니다.
   - 해당 소모임의 `최대회원수`를 `초과`하면 요청할 수 없습니다.
 
### [ POST ]
- 글 작성
   - 소모임 회원은 해당 소모임에만 공유되는 포스트를 작성할 수 있습니다.
   - 글에 댓글이 달릴 시, 알림이 동작합니다.
- 댓글 작성
   - 소모임 회원은 해당 글에 대해 댓글을 작성할 수 있습니다.
   - 소모임장의 댓글은 모임장의 댓글임이 표시됩니다.

### [ 알림 ]
- 메일 알림
   - 알림을 메일로 발송합니다. 
- 문자 알림
   - 사용자의 문자로 알림을 발송합니다.
- 알림 내역 관리
   - 알림 발송 내역을 관리합니다. 


## 🎓 API 명세
https://great-lizard-b83.notion.site/API-d1ca66eb2a554be68e47284d727a59e3?pvs=4

## 📖 ERD
https://www.erdcloud.com/d/J3yFTcYwZDscXNmSY

<img width="824" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/009ad5f4-7c24-46b8-b2f9-32c871d8160b">


---
## 📕 Trouble Shooting
[go to the trouble shooting section](./TROUBLE_SHOOTING.md)








