# weGather (소모임 서비스)

## 개요
- 지역과 관심사가 비슷한 사람 끼리 모임을 만들 수 있는 서비스


## 기능 목록
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

## 사용 기술 (Tech Stack)
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
<img width="636" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/c11d1d84-0690-41f5-b050-fd0fa777db0a">
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

<br/> <br/> <br/>

## 꼭 RestAssuredMockMvc 를 써야할까?
>Spring Security 환경의 API 테스트 시, RestAssured를 사용하려면 꼭 RestAssuredMockMVC를 써야하는가?

RestAssured 는 API테스트에 BDD 스타일로 작성하기 때문에 가독성이 좋다. 그래서 MockMVC를 사용하기 보다는 RestAssured 사용을 고수하고 싶다. 하지만 SpringSecurity가 적용된 API의 경우 MockMVC 를 사용하는 것이 좀더 판한 것 같다. 인가를 Mock 하기위한 기능들을 많이 제공한다. 이를위해 RestAssured 쪽에서도 RestAssuredMockMvc 라는 라이브러리를 제공하고 있지만, 제한된 기능은 어쩔 수 없다.

### 불편했던점
RestAssuredMockMvc 사용시, RestAssured 로 작성한 코드가 완벽하게 호환되지 않는다.
1. PathVariable
2. MultiPartFile()
4. 자료가 많이 없다. 많이 쓰이지는 않는 것 같아서, 인터넷에 자료가 많이 없다는 점이 약점

### 결론
인증, 인가를 위해 꼭 RestAssuredMockMvc를 쓸 필요는 없다.
RestAssured 를 쓰고, auth().basic("username", "password") 메서드를 사용해서 인증정보를 직접 던져주어도 된다. <br/>
<img width="659" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/6ccd3077-2d1d-4550-a44d-6223bc69beb5">

이 편이 API 기능 전체를 테스트 하는 End To End 테스트의 개념에 더 맞기도 하고, 굳이 자료도 많이 없는 RestAssuredMvc 를 사용하겠다고 고생하지 않아도 된다.
단, 인증 방식이 바뀌면 테스트 코드의 위 부분도 변경되어야 한다는 단점이 있다.


<br/> <br/> <br/>

## 테스트 시, 서로 다른 두 객체의 동등성 비교하기
> create entity 기능을 테스트할 경우, 요청에 쓰이는 dto 객체와 생성되어 반환되는 dto의 타입이 다르다. 둘의 비교를 쉽게 하는 법은 무엇일까?

### 예시
- api : createGroup - 소그룹을 생성하는 api
  
- RequestDTO : <br/>
  <img width="292" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/bebdc868-ec19-4d96-8942-c57e8193b1ce">

- ResponseDTO : <br/>
  <img width="299" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/6f9a8257-3fd6-4b7b-905d-680a7b40f85c">


### 고려사항
위 두 객체를 비교함에 있어 고려해야할 점이 몇가지 있다.
1. 두 객체의 필드를 비교해야한다. (동등성 비교)
2. 두 객체의 필드가 서로 다르다. (특정 필드만 비교해야한다.)

### 해결
- 동등성 비교를 위해서는 assertj 에서 usingRecursiveComparison() 메서드를 제공한다.
   - equalTo() 를 override 하지 않아도, 두 객체를 필드로 비교해준다
- isEqualToComparingOnlyGivenFields 메서드
   - assertj 에서는 특정 필드만 명시하여 비교할 수 있도록 메서드를 제공해준다.
   - 하지만 이는 deprecated 되었다. - nested feild에 대해서는 비교를 못해주기 때문이라고한다. <br/>
     <img width="801" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/3eafd8b9-f2e5-44e7-a61d-a40bd7981b07">
- usingRecursiveComparison() + ignoringFields + isEqualTo 조합
   - 최종적으로 선택된 메서드 조합이다.
   - usingRecursiveComparison : 동등성 비교 명시
   - ignortingFileds : 특정 필드를 비교하지 않도록 명시
   - isEqualTo(객체) : 비교할 객체를 명시 <br/>
  <img width="657" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/74c7aebb-3dd3-4dd9-bf87-3d685b9be910">

<br/><br/><br/>

## Querydsl Test 시 주의할 점 (JpaQueryFactory 빈주입)
> Querydsl Repository 를 @DataJpaTest 로 테스트시 JpaQueryFactory를 빈주입 받지 못해 에러가 났다.
<img width="928" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/74ff804b-8b5a-463a-acfe-48acbd40dba8">
- JpaQueryFactory 가 영속성 계층이 아니라서 자동으로 빈주입을 시켜주지 못하는 것이다.

### 해결
- @TestConfiguration 을 이용하여 테스트 시에 해당 빈을 주입 받도록 설정해주었다. <br/>
<img width="371" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/c6d174fe-afcd-48e8-a5d0-df13011c2b48"> <br/>
<img width="378" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/f4dea017-722f-450d-9fe7-1e647b50342c">


<br/><br/><br/>

## querydsl의 fetchResults, fetchCount deprecated 
https://gyurrr.tistory.com/17
위 링크에 작성하였습니다.

<br/><br/><br/>

## Querydsl 조회 시, join 컬럼을 명시하지 않을 시, cross join 하는 증상
### 연관 테이블을 조회에 사용할 경우, cross join 발생
<img width="905" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/0a312963-ec6b-44a4-af04-83556c62a17b">
<img width="493" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/7abab65b-5eec-4b61-ba3d-79cdc6db9331">

### 원인
Querydsl-JPA에서 사용하는 JPQL의 내부 구현체 (Hibernate) 의 특성 때문.
Hibernate의 경우 암묵적인 조인은 Cross Join을 사용하는 경향이 있기 때문

참고> https://jojoldu.tistory.com/533

### 해결
Join을 명시적으로 함으로써, cross join 문제를 해결 <br/>
<img width="629" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/82c49b64-c453-4e3e-9e2f-db0f0c615d26">
<img width="384" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/39b93503-013b-4cbe-97ce-5e2497a61c17">

<br/><br/><br/>

## Entity 미사용 컬럼
<img width="760" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/0ab5237a-e0a0-48af-b088-824b4a87fca4">

### 에러
하이버네이트 Mapping 에러 발생 <br/>
<img width="1225" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/e68e870d-30e7-4539-9d02-83d7223b5517">


### 해결
Entity에 존재하지만, 실제 DB에 반영하고 싶지 않은 컬럼이 있는 경우 @Transient 어노테이션을 붙힌다. <br/>
<img width="386" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/339f4f8d-c408-4832-8fd5-c0f10860cf3d"> <br/>

<br/><br/><br/>

## java.lang.UnsupportedOperationException (Immutable Set)
### 에러
테스트를 작성할 때 준비 단계에서 아래와 같이 `Set.of()`를 사용하여 데이터를 만들었더니, Immutable Set(수정할 수 없는 Set타입)이라고 에러가 났다. <br/>
<img width="621" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/8f400b7e-e87c-4ecd-810f-a613d8a636a6"> <br/>
<img width="877" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/886bedd2-8c8d-4f88-aae2-4fff285a4bf0">

### 원인
`java.uilt`의 `Set` 타입에 존재하는 정적 팩토리 메서드인 `Set.of()`와 `Set.copyOf()` 는 ImmutableSet 을 반환하기 때문이었다. <br/>
<img width="608" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/eaca79ea-384d-491c-ad65-8945b3a3a55d"> <br/>
<img width="521" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/27381000-328e-4c41-9844-be4327630dba"> <br/>

## 해결
java.util.Set 을 이용하지 않고 Set을 초기화해 준다. <br/>
<img width="652" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/d2678002-20d2-47d5-9257-138dc0fc78a5">

<br/><br/><br/>

## JPA의 Dirty checking은 영속성으로 관리되는 속성을 대상으로만 한다.
VO를 Entity의 속성으로 사용하는 경우에, VO의 @Transient 로 제외된 속성이 있다면, 이는 더티체킹의 대상이 아니다.
즉, 제외된 컬럼이 변경이 일어나도, persist 나 update가 일어나지 않으므로, EntityListener의 @PrePersist, @PerUpdate 또한 동작하지 않는다.

### 에러 상황
<img width="879" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/ca7fb7f4-1da5-4bf1-b947-b453b1b095dd">
<img width="918" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/145a978c-3e3c-40fc-95ab-855a91619328">
<img width="350" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/fd881c1a-9183-45ab-aade-bd27e221c4bd">
<img width="872" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/2f3eeee1-4879-4af2-a55f-c9365f886a17">


### 해결
<img width="985" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/1d94e64d-d72e-4bb7-b2ed-ca385f4faddf">
@Transient 한 속성은 더티체킹의 대상이 아니다. 당연한 것이지만 다시 한번 상기할 수 있었다.

### 개선점
사실, Set -> 문자열 변환이 DB 반영 시점에 한번만 이루어지게 하기 위해서 EntityListener를 사용한 것인데, 관심사를 추가할 때마다 매번 변경해주면 의미가 없다.
다른 방법을 찾아보아야 겠다.
[더티체킹로직 커스터마이징](https://vladmihalcea.com/how-to-customize-hibernate-dirty-checking-mechanism/)

<br/><br/><br/>

## Builder 사용시 입력값이 null 일경우 에러
<img width="932" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/30e1d61c-8ec9-422b-9024-89106f925ae0">
<br/>

### 원인
롬복의 @AllArgsConstructor 와  @Builder 를 명시할 시, 모든 컬럼으로 빌더를 만들어 주는데, null 값이 들어올 경우 null 값을 그대로 넣어준다.
참고 > [Lombok-Builder.Default](https://velog.io/@hsbang_thom/Lombok-Builder.Default)

### 해결
- @Builder.Default 를 컬럼에 명시하여 기본값을 넣어주거나, 생성자를 따로 만든 후 그 위애 @Builder를 명시한다. <br/>
<img width="457" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/436b2c5a-cb44-41ab-928e-31a0301f4d15"> <br/>
- List인 경우 @Builder.Default 명시하고, = new ArrayList(); 로 초기화 해 주어야한다.


<br/><br/><br/>

## N + 1 문제 (querydsl 사용시)
ManyToOne(다대일) 연관관계 조회 시, LazyLoading 설정을 해두면, N+1 쿼리가 날라간다.
<img width="424" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/4b8537ad-7593-42a1-a48f-47b73a3bc7ae"> <br/>
위 쿼리 실행 후, member 를 조회하는 쿼리가 2개 더 실행됨

### 해결
Lazy loading이란 처음 조회시에는 프록시 객체를 담아두었다가, 해당 객체를 사용시점에 조회하는 것을 말한다.
객체로 연관관계가 맺어져 있는 JPA의 경우, Eager Loading 을 사용하면, 객체에 객체에 객체.. 연관되어 있는 객체를 다끌어올 수 있기 때문에 가급적 LazyLoading 을 기본값으로 설정해 두는 것이 권장된다. 

하지만 그렇다고, lazyloading 을 설정해 두면 성능문제가 발생할 수 있다.
바로, N+1 문제이다. ManyToOne 객체의 조회 시, 연관된 리스트의 size 만큼 쿼리가 추가로 실행될 수 있는 것이다.
N+1 문제를 해결하기 위해서는 JPA 에서는 fetch join을 써야한다. querydsl 또한 마찬가지이다.

fetch join을 사용하면, Eager loading 처럼 처음 조회 시, join 하여 한번에 연관된 객체 까지 가져온다. 또한, 개발자가 성능을 위해 의도적으로 사용하는 것이므로, Eager loading 설정해 두는 것 보다 Side Effect가 적다. <br/>

<img width="641" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/5d14256f-d8c6-42a6-b2cb-2610b8b3eff6"><br/>
join 절 뒤에 fetchJoin() 을 추가해 주면 된다. <br/>

<br/>
<img width="419" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/bf06aba2-bad2-49dc-80b6-482ac38a7cc8">

<br/><br/><br/>

## Redis에 읽고 쓰는 연산을 트랜잭션으로 묶어주기
웹은 멀티 스레드 환경이어서 동시에 여러명이 접근할 수 있습니다. DB를 사용하는 경우, @Transaction 어노테이션을 이용하여 하나의 작업에 대해 원자성을 보장해 줄 수 있습니다.
Redis를 사용할 때에도 작업별로 원자성을 보장해 주기 위한 트랜잭션 처리가 필요합니다.

### 문제
소모임 가입 기능 구현 시, Redis를 사용하여 소모임 가입자와 가입 회원수를 관리하는 기능을 추가했는데, 트랜잭션 처리가 되지 않아서 문제가 발행할 수 있었습니다. <br/>
<img width="605" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/c7e3c5bf-a72a-42cc-93a5-8df7d0db21ba">

### 해결
하나의 작업단위를 트랜잭션으로 묶어주어 원자성을 보장할 수 있도록 하여야 합니다. 하지만, 아래와 같이 Transaction 내에서 redis 의 get 연산을 실행하면 null이 반환됩니다.
(이는 redis 의 트랜잭션 특성상, 연산을 queue에 담아뒀다가 exec 시에 동시에 실행하기 때문에, 트랜잭션 중에는 get연산을 해도 바로 실행해서 값을 가져올 수 없기 때문입니다.)

그래서 결국, 소모임 가입 회원 명단은 db에서 관리하고, 가입 여부는 미리 validation check를 하고, redis에서는 가입 회원수만 관리하기로 했습니다. 
가입 요청 시, 회원수를 바로 한명 증가 시키고, 증가한 회원 수가 최대 회원수보다 많으면 가입을 취소하고 회원수를 다시 한명 감소시키는 방식으로 구현했습니다. <br/>

<img width="555" alt="image" src="https://github.com/gyureal/weGather/assets/78974381/7bc3312b-ef19-4a19-b0f8-aab307e13741">

<br/><br/><br/>   









