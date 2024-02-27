# 🗒️ [Side Project] 소모임 서비스
> 관심사가 비슷한 사람끼리 모임을 만들고 소통할 수 있는 서비스입니다.

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
<img width="797" alt="image" src="https://github.com/prebird/weGather/assets/78974381/060b500e-df8e-4a27-af6d-e3a3a5f5402f">



## 🔎 프로젝트 기능
### [ 회원 ]
- 회원가입
   - 회원가입 요청 시, 이메일로 `본인인증메일`이 전송됩니다.
   - 이메일로 전송된 링크를 클릭 시, 회원가입이 완료됩니다.
   - `본인인증메일 재전송`이 가능합니다.
- 로그인
   - 이메일 또는 username로 로그인 가능합니다. 
- 회원 정보 수정
   - 로그인 된 본인의 정보만 수정 가능합니다. 
   - 본인의 프로필 이미지를 수정합니다.
   - 본인의 패스워드를 변경합니다.
   - 본인의 알람 세팅을 변경합니다.
   - 본인의 관심사를 변경합니다.


### [ 관심사 ]
- 관심사 관리
   - 관리자 권한 회원만 가능합니다.
   - 관심사 추가, 삭제, 조회
   - 관심사 화이트 리스트(자동완성) 목록 조회  

### [ 소모임 ]
- 소모임 생성
   - 로그인한 회원은 소모임을 생성할 수 있습니다.
- 소모임 조회
   - 소모임 path 로 소모임의 정보를 조회합니다.
   - 소모임 정보, 배너 이미지 url, 썸네일 이미지 url, 모집여부, 상태, 가입 가능여부 등 반환
- 소모임 검색
   - 소모임 명으로 검색합니다.
   - 페이징 처리 하여 반환합니다. 
- 소모임 정보 수정
   - 소모임 배너 이미지 수정
   - 소모임 배너 사용 여부 변경
   - 소모임 관심사 추가, 삭제, 조회
- 소모임 상태 변경
   - 소모임을 외부에 공개합니다.
   - 소모임의 인원 모집을 오픈합니다.
   - 소모임을 종료합니다.


### [ 소모임 가입 ]
- 소모임 가입 요청
   - 소모임장이 아닌 회원은 `소모임에 가입 요청`을 보낼 수 있습니다.
   - 해당 소모임의 `최대회원수`를 `초과`하면 요청할 수 없습니다.
- 소모임 가입 요청 목록 조회
   - 해당 소모임 관리자만 조회 가능합니다.
   - 해당 소모임에 가입 요청된 리스트를 조회합니다.  
- 소모임 가입 요청 승인/거절
   - 해당 소모임의 관리자는 소모임 가입 요청을 `승인/거절` 할 수 있습니다.
   - 해당 소모임의 `최대회원수`를 `초과`하면 요청할 수 없습니다.
 

## 📖 ERD
https://www.erdcloud.com/d/J3yFTcYwZDscXNmSY

<img width="824" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/009ad5f4-7c24-46b8-b2f9-32c871d8160b">


---
## 📕 Trouble Shooting
[go to the trouble shooting section](./TROUBLE_SHOOTING.md)








