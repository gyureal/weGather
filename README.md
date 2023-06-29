<img width="636" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/c11d1d84-0690-41f5-b050-fd0fa777db0a"># weGather (소모임 서비스)

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
> 두 타입 사이에 필드 수가 다른 경우, 아래와 같이 테스트가 fail이 된다.<br/>

아래의 코드는 회원추가 시, 입력값으로 사용한 joinMemberRequest 와 회원추가 후 결과로 리턴된 memberDto를 recusiveComparison() 을 사용해서 비교하는 코드이다.<br/>
<img width="486" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/94ef7080-1864-4e3d-b636-c56154d412b9"><br/>
타입은 비교하지 않고, 두 타입의 필드의 이름을 기준으로 값을 비교하지만, 필드의 수가 다른 경우에 아래 fail이 된다.<br/>
<img width="855" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/d0be9732-3385-46b0-aadf-3c50659a2caa"><br/>
해결책은 두 타입에서 일치하지 않는 필드는 ignoringFields() 에 명시해서 비교에서 제외시키거나, null 인 컬럼은 비교하지 않는 ignoringActualNullFields()를 추가해 주어야한다.<br/>
<img width="476" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/10504258-e76a-4241-b55f-18c4e06a6d80">

<br/> <br/> <br/>


## 통합테스트 시, 테스트 격리

dirtiesContext 사용시 문제점

- 성능 이슈
- 인메모리 DB가 아닌 DB의 인스턴스를 사용하는 경우 초기화 되지 않음 (컨텍스트 로딩 시 초기화 설정이 없는 경우)

위와 같은 이슈 때문에, 통합테스트 테스트 격리를 위해 truncate.sql을 @Sql에 명시해서 매 테스트마다 초기화 해주는데,

실수로 테이블을 명시하지 않으면 아래와 같이 테스트 데이터의 삭제가 이루어 지지 않으니, 꼭 명시해 주자.
<img width="705" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/1a06ede2-ab47-4121-be0a-82ccac4c4a42"> <br/>
매 테스트마다 3개의 회원 데이터를 추가해주는데, 전체 조회 기능 테스트 시 아래와 같이 6개로 나옴 <br/>
<img width="694" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/54774d81-cd06-4bed-81dd-078815aa8d2d"> <br/>

해결 : 회원 테이블 truncate.sql 에 추가 <br/>
<img width="473" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/62dac751-1cdc-484a-871c-a9727d10026f"> <br/>

<br/> <br/> <br/>
## Page 반환타입 테스트 
<img width="707" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/f67eea65-75db-4bb9-aff8-bd8f9483e465"> <br/>
as() 는 RestAssured에서 json으로 넘어온 결과값을 deserialize해서 명시한 객체로 매핑해주는 ObjectMapping 기능을 수행하는 메서드입니다.
Page.class 객체에 매핑시켜서 값 검증을 하려고 했지만, 매핑되지 않았습니다.

Page가 인터페이스여서 되지 않았나 싶어서 PageImpl 인 구현체로도 해보았지만, 아래와 같이 실패했습니다. <br/>
<img width="703" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/80e0bd05-b29a-4878-82e5-c6a5bfb34b0a"> <br/>

Json 타입을 객체로 올바르게 매핑시킬 수 없는 것 같았습니다. 조사한해결책 아래와 같습니다.
- 적절한 deserializer를 구현(**[Spring Boot Page Deserialization - PageImpl No constructor](https://stackoverflow.com/questions/52490399/spring-boot-page-deserialization-pageimpl-no-constructor)**),
- 해당 json 리턴 값에 맞는 대응하는 생성자를 가진 클래스 생성([**[Spring Boot Page Deserialization - PageImpl No constructor](https://stackoverflow.com/questions/52490399/spring-boot-page-deserialization-pageimpl-no-constructor)**](https://stackoverflow.com/questions/52490399/spring-boot-page-deserialization-pageimpl-no-constructor)).

저는 굳이 위 과정을 통해 Page객체로 받아 검증할 필요는 없을 것 같아서, jsonPath를 이용해 검증하기로 했습니다.
<img width="704" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/5349198d-d88d-4e46-acff-b4b6edb5ce21">
- jsonPath().getList() 를 사용하면 해당 path에 list<> 객체를 가져올 수 있습니다. 이걸로 조회해 온 회원목록을 검증합니다.
- path(”경로”)를 사용하면 해당  path의 값을 Object 타입으로 반환받을 수 있습니다. 이걸로 페이징 관련 리턴값을 검증하였습니다.
- JsonPath().get(”경로”)를 사용할 수 도 있지만, 그렇게되면 json타입에 의존하게 되기 때문에 path() 메서드를 사용하였습니다.


<br/> <br/> <br/>

## 이미지 업로드 기능 테스트
이미지 업로드 시, MultipartFile 타입의 input을 사용한다. 이를 어떻게 테스트할지 고민해 보았습니다.

해당 프로젝트는 통합테스트로 RestAssured 모듈을 사용하고 있었으므로, RestAssured의 docs를 우선 찾아보았습니다. 역시 관련해서 기능을 제공해주고 있었습니다.

https://github.com/rest-assured/rest-assured/wiki/Usage#multi-part-form-data <br/>

<img width="695" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/8ac00d1b-e329-4cfd-b2b4-96b8948dc80a"><br/>

우선 가장 기본적인 기능은 multipart()에 파라메터로 File 인스턴스를 주는 것입니다. 하지만 이것은 실제 파일이 해당 경로에 존재하는 경우입니다. 경로가 임의의 경로인 경우 아래와 같은 에러가 표출됩니다. <br/>

<img width="696" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/5c970890-6cd7-4898-aa89-9e25be188058"> <br/>
임의의 파일과 경로로 테스트 하고싶은 경우 아래 방식을 이용합니다. <br/>

MultipartSpecBuilder를 이용해 가상의 요청을 만듭니다. <br/>
<img width="699" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/2cecd078-bf53-4a50-9cca-3403f260e98b"> <br/>
https://itecnote.com/tecnote/java-how-to-send-a-multipart-request-with-restassured/

<br/><br/><br/>
## RestAssured -> RestAssuredMockMvc
- RestAssured로 통합테스트를 하는 환경에서 Spring Security가 적용된 api 테스트를 위해 RestAssuredMockMvc 를 도입하였습니다.

<br/><br/><br/>

### RestAssured NoClassDefFoundError
<img width="636" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/5b4dc6fd-4c47-40d4-ad7c-53686597737b"> <br/>
- RestAssuredMockMvc를 사용하기 위해서 restAssured - spring-mock-mvc 3.0.0 을 import 한 후, RestAssuredMockMvc를 사용하였더니 아래 에러가 발생하였습니다. <br/>
<img width="925" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/9ad298ca-0c87-4bd1-9ca4-896dde983537"> <br/>
- 문제의 원인
   RestAssured의 낮은 버전에서는 해당 에러가 발생하였던 것 같다. 프로젝트에서 사용하는 RestAssured의 버전인 4.4.0으로 업그레이드 하니, 에러가 발생하지 않았습니다. <br/>
  <img width="641" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/124f5c5f-00ca-4eb7-b61d-10a387d0cbd2"> <br/>
- 참고
  https://github.com/rest-assured/rest-assured/issues/1220

<br/><br/><br/>

### RestAssured -> RestAssuredMockMvc 
RestAssured -> RestAssuredMockMvc 로 교체
<img width="502" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/5112c731-1d12-40d1-92c2-d454d1d725cd"> <br/>
- extract() 시, ExtractableResponse<Response> 가 아닌 ExtractableResponse<MockMvcResponse> 를 반환합니다.
- static import 를 하면 매번 test 문에 restAssured인지 RestAssruedMockMvc 인지 명시해 주지 않아도 되어서 교체 해주기 편했습니다.
<img width="1015" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/d7cae66e-3040-4723-bacd-ce76d4298168">
참고
https://www.baeldung.com/spring-mock-mvc-rest-assured

<br/><br/><br/>

### RestAssuredMockMvc 에서 pathParam() cannot resolve 문제
<img width="581" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/0ea49248-f341-4e08-847c-4c01d2a63d3d"> <br/>
- RestAssured 에서 PathVariable을 테스트하기 위해 사용한 pathParam() 이 RestssuredMockMvc에는 존재하지 않았습니다.

- RestssuredMockMvc에는 체이닝 메서드 들의 반환타입인 MockMvcRequestSpecification 을 찾아보니, get(), post() 등 요청을 url을 명시할 때 pathVariable 또한 같이 명시함을 알 수 있었습니다.
<img width="899" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/9b694e56-1184-4c8a-8504-d85fa1113e76">


<br/> <br/><br/>

### Multipart() 파라메터 오류
MultipartFile 을 파라메터로 받는 api 테스트 시, RestAssured 사용시에는 가상의 요청을 MultiPartSpecification 인스턴스로 만들어서 multipart()의 파라메터로 던져주었으나, RestAssuredMockMvc 사용시에는 에러가 났다.

왜 이렇게 다를까? MockMvc 기준인것 같다


