# SpringBoot-JPA-Project-산책갈개
스프링부트 JPA 프로젝트
<br>


## 🖥️ 프로젝트 소개
강아지 산책 소셜메이트, 쇼핑 플랫폼
<br>


## 🕰️ 개발 기간
* 2023.11 - 2024.01

### 🧑‍🤝‍🧑 맴버구성
 - 팀장: 노의진 : 쇼핑페이지, 장바구니, 주문서 및 결제 페이지, 자유게시판, 공지사항 게시판
 - 팀원1: 복영헌 : 메인페이지(산책글 목록, 주간인기글, 쇼핑 등), 로그인 및 회원가입 등 계정관리 페이지, 산책 메이트 게시판, 관리자페이지
 - 팀원2: 임형준 : 마이페이지, QnA게시판


### ⚙️ 개발 환경
- **IDE** : IntelliJ IDEA
- **Framework** : Springboot(3.x)
- **Database** : Oracle DB(11xe)
- **ORM** : JPA

## 🧷페이지 흐름도
<details open>
<summary>페이지 흐름도</summary>
<img src='https://github.com/NohEuijin/JPA-DW/assets/141835418/2840096d-a01d-4065-8635-74585a91c089' border='0'>
<img src='https://github.com/NohEuijin/JPA-DW/assets/141835418/a0b9f8a9-d5e3-48a8-9afa-8a12c4837ee0' border='0'>
</details>

## 🧷ERD구성

<details open>
<summary>ERD</summary>
  
<a href='https://github.com/NohEuijin/JPA-DW/assets/141835418/c46f7e89-697f-4b4b-bda2-7ec5481f3248' target='_blank'>
<img src='https://github.com/NohEuijin/JPA-DW/assets/141835418/c46f7e89-697f-4b4b-bda2-7ec5481f3248' border='0'>
</a>

</details>

### 포트폴리오 

[포폴1차.pdf](https://github.com/bokkaa/JPA-DW/files/14395778/1.pdf)

## 📌 내가 맡은 기능  <a href="https://github.com/HyeongJunIm/JPA-DW/wiki" >상세보기 - WIKI 이동</a>
#### 마이페이지
- 회원정보수정 및 회원탈퇴  
- 회원 펫 추가,수정,삭제 
- 나의 작성글(QnA, 자유게시판) 목록
- 나의 메이트 찾기 목록(승인 및 모집처리)
- 나의 메이트 신청 목록 
- 주문 내역 리뷰 목록
- 주문 내역 목록
#### QnA게시판
- 게시판 목록  
- 게시판 상세 
- 게시판 수정, 삭제 
- 게시판 댓글 작성
- 게시판 댓글 수정, 삭제 

<hr>

## 코드 수정 

<details><summary>1.N+1문제</summary>

-QuestionRepositoryImpl.java
```java
@Override
    public Page<QuestionListDto> findQnaListBySearch(Pageable pageable, SearchForm searchForm) {
        //검색어
        BooleanExpression keywordTitle = qnatitleEq(searchForm.getKeyword());

 

        //페이징 및 검색조건을 적용하여 question 엔티티 조회
        List<QuestionDto> content = jpaQueryFactory
                .select(new QQuestionDto(
                        question.id,
                        question.questionTitle,
                        question.questionContent,
                        question.questionRd,
                        question.questionMd,
                        question.users.id,
                        question.users.userName
                ))
                .from(question)
                .where( keywordTitle )
                .orderBy(
                        getDynamicSort(searchForm)
                    )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징을 위한 전체 데이터 수 조회
        Long count = getCount(searchForm.getKeyword());


        List<QuestionListDto> contents =
                content.stream().map(questionDto -> {
                    Long commentCount = jpaQueryFactory
                            .select(questionComment.id.count())
                            .from(questionComment)
                            .where(questionComment.question.id.eq(questionDto.getId()))
                            .fetchOne();
                    System.out.println(commentCount+"댓글수 입니다.");
                    List<QuestionImgDto> questionImgDto = jpaQueryFactory
                            .select(new QQuestionImgDto(
                                    questionImg.id,
                                    questionImg.questionImgRoute,
                                    questionImg.questionImgName,
                                    questionImg.questionImgUuid,
                                    question.id
                            ))
                            .from(questionImg)
                            .leftJoin(questionImg.question, question)
                            .where(question.id.eq(questionDto.getId()))
                            .fetch();
                    List<QuestionImgDto> imgDto = questionImgDto.stream()
                            .map(imgDtos -> new QuestionImgDto(
                                    imgDtos.getId(),
                                    imgDtos.getQuestionImgRoute(),
                                    imgDtos.getQuestionImgName(),
                                    imgDtos.getQuestionImgUuid(),
                                    imgDtos.getQuestionId()
                            ))
                            .collect(toList());
                    return new QuestionListDto(
                            questionDto.getId(),
                            questionDto.getQuestionTitle(),
                            questionDto.getQuestionContent(),
                            questionDto.getQuestionRd(),
                            questionDto.getQuestionMd(),
                            questionDto.getUserId(),
                            imgDto
                    }

        return new PageImpl<>(contents,pageable,count);
}

```

-FreeBoardRepositoryImpl.java
```java
@Override
    public Page<MyFreeBoardResultListDto> findAllById(Pageable pageable,Long userId) {

        List<MyFreeBoardDto> contents = jpaQueryFactory
                .select(new QMyFreeBoardDto(
                        freeBoard.id,
                        freeBoard.freeBoardTitle,
                        freeBoard.freeBoardContent,
                        users.id,
                        users.userAccount,
                        users.userNickName,
                        userFile.id,
                        userFile.route,
                        userFile.name,
                        userFile.uuid
                ))
                .from(freeBoard)
                .leftJoin(freeBoard.users,users)
                .leftJoin(users.userFile,userFile)
                .where(users.id.eq(userId))
                .orderBy(freeBoard.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long counts = jpaQueryFactory
                .select(freeBoard.count())
                .from(freeBoard)
                .where(users.id.eq(userId))
                .fetchOne();

        List<MyFreeBoardResultListDto> result = contents.stream().map(r ->{

            Long commentCounts = jpaQueryFactory
                    .select(freeBoardComment.id.count())
                    .from(freeBoardComment)
                    .where(freeBoard.id.eq(r.getId()))
                    .fetchOne();

            System.out.println("[댓글 수 ] : "+ commentCounts);

            List<FreeBoardImgDto> freeBoardImgDtos = jpaQueryFactory
                    .select(new QFreeBoardImgDto(
                            freeBoardImg.id,
                            freeBoardImg.freeBoardImgRoute,
                            freeBoardImg.freeBoardImgName,
                            freeBoardImg.freeBoardImgUuid,
                            freeBoard.id
                    ))
                    .from(freeBoardImg)
                    .leftJoin(freeBoardImg.freeBoard,freeBoard)
                    .where(freeBoard.id.eq(r.getId()))
                    .fetch();

            List<FreeBoardImgDto> freeBoardImgDto = freeBoardImgDtos.stream().
                    map(freeBoardImg -> new FreeBoardImgDto(
                            freeBoardImg.getId(),
                            freeBoardImg.getFreeBoardImgRoute(),
                            freeBoardImg.getFreeBoardImgName(),
                            freeBoardImg.getFreeBoardImgUuid(),
                            freeBoardImg.getFreeBoarId()
                    )).collect(toList());

                return new MyFreeBoardResultListDto(
                        r.getId(),
                        r.getFreeBoardTitle(),
                        r.getFreeBoardContent(),
                        r.getUserId(),
                        r.getUserAccount(),
                        r.getUserNickName(),
                        r.getUserFileId(),
                        r.getRoute(),
                        r.getName(),
                        r.getUuid(),
                        commentCounts,
                        freeBoardImgDto
                );
        }).collect(toList());

        System.out.println(result.toString()+" 내가 작성한 자유게시판 입니다.");

    return new PageImpl<>(result,pageable,counts);
    }


``` 
- 유저가 작성 및 신청, 주문한 내용들이 주로 담겨져 있는 페이지를 담당하다 보니 유저가 작성, 신청한 내용들을 조회하는 쿼리문을 많이 사용하게되었다.  
- 위에 코드처럼 작성하여 조회를 하니 유저가 작성한 내용을 조회가 가능했다.
- 문제점음 조회시 유저가 작성한 만큼 쿼리가 사용하며 조회하게 되고 쿼리가 실행되는 시간이 많이 소요되었다.
- 문제를 해결하기 위하여 Tuple과 jpql을 활용하여 필요한 정보를 조회할수 있도록 코드를 수정 하였으며 이로 인해 최소한의 쿼리사용으로 조회가 가능해졌다. 



<details><summary>수정코드 </summary>

-QuestionRepositoryImpl.java 
```java
 @Override
    public Page<QuestionListDto> findQnaListBySearch(Pageable pageable, SearchForm searchForm) {
        //검색어
        System.out.println(getDynamicSort(searchForm) + "여기닷!");

        BooleanExpression keywordTitle = qnatitleEq(searchForm.getKeyword());


        Long count = getCount(searchForm.getKeyword());



        List<Tuple> contents = jpaQueryFactory
                .select(
                        question.id,
                        question.questionTitle,
                        question.questionContent,
                        question.questionRd,
                        question.questionMd,
                        users.id,
                        users.userAccount,
                        users.userNickName,
                        jpaQueryFactory.select(

                                questionComment.count()
                        )
                                .from(questionComment)
                                .where(questionComment.question.eq(question))

                        ,
                        userFile.id.coalesce(0L),
                        userFile.route.coalesce("0"),
                        userFile.name.coalesce("0"),
                        userFile.uuid.coalesce("0"),
                        questionImg.id,
                        questionImg.questionImgRoute,
                        questionImg.questionImgUuid,
                        questionImg.questionImgName

                )        .from(question)
                .leftJoin(question.users, users)
                .leftJoin(users.userFile,userFile)
                .leftJoin(question.questionImg,questionImg)
                .where(keywordTitle)
                .orderBy(
                        getDynamicSort(searchForm)
                )
                .fetch();




        List<QuestionListDto> result = new ArrayList<>();


        for(Tuple tuple : contents){
            Long queId = tuple.get(question.id);
            System.out.println(queId+"조건 번호");
            if(!result.stream().anyMatch(
                    dto -> dto.getId().equals(queId))){

                QuestionListDto questionListDto =new QuestionListDto(
                        tuple.get(question.id),
                        tuple.get(question.questionTitle),
                        tuple.get(question.questionContent),
                        tuple.get(question.questionRd),
                        tuple.get(question.questionMd),
                        tuple.get(users.id),
                        tuple.get(users.userAccount),
                        tuple.get(users.userNickName),
                        tuple.get(8, Long.class),
                        tuple.get(9,Long.class),
                        tuple.get(10,String.class),
                        tuple.get(11,String.class),
                        tuple.get(12,String.class),
                        tuple.get(questionImg.id),
                        tuple.get(questionImg.questionImgRoute),
                        tuple.get(questionImg.questionImgUuid),
                        tuple.get(questionImg.questionImgName)
                );
                result.add(questionListDto);

            }
            System.out.println(tuple.toString()+"  댓글수 입니다.");
        }

        System.out.println(result+"@@@@@@@@@@@@@");

        return new PageImpl<>(result, pageable, count);
    }

```
-FreeBoardRepositoryImpl.java
```java
  @Override
    public Page<MyFreeBoardResultDto> findByUserId(Pageable pageable, Long userId) {
        System.out.println(userId+ " 조회해야되는 아이디 입니다.");
        List<MyFreeBoardListDto> query = em.createQuery(
                "SELECT NEW com.example.dw.domain.dto.community.MyFreeBoardListDto(" +
                        "f.id, f.freeBoardTitle, f.freeBoardContent, u.id, u.userAccount, u.userNickName, " +
                        "uf.id, uf.route, uf.uuid, uf.name, " +
                        "(SELECT COUNT(fc) FROM FreeBoardComment fc WHERE fc.freeBoard.id = f.id) as commentCount, " +
                        "fi.id, fi.freeBoardImgRoute, fi.freeBoardImgName, fi.freeBoardImgUuid ) " +
                        "FROM FreeBoard f " +
                        "LEFT JOIN f.users u " +
                        "LEFT JOIN u.userFile uf " +
                        "LEFT JOIN f.freeBoardImg fi " +
                        "WHERE f.users.id = :userId and fi.id = (select Min(fi2.id) from FreeBoardImg fi2 where f.id = fi2.freeBoard.id) " +
                        "ORDER BY f.id DESC", MyFreeBoardListDto.class)
                .setParameter("userId", userId)
                .getResultList();

        System.out.println(query.toString()+"입니다");

        List<MyFreeBoardResultDto> result =  query.stream().map(
                o-> new MyFreeBoardResultDto(
                        o.getId(),
                        o.getFreeBoardTitle(),
                        o.getFreeBoardContent(),
                        o.getUserId(),
                        o.getUserAccount(),
                        o.getUserNickName(),
                        new UserFileDto(o.getUserFileId(),o.getRoute(),o.getName(),o.getUuid(),o.getUserId()),
                        o.getCommentCount(),
                        new FreeBoardImgDto(o.getFreeBoardImgId(),o.getFreeBoardImgRoute(),o.getFreeBoardImgName(),o.getFreeBoardImgUuid(),o.getId()))
        ).collect(toList());


        Long count = jpaQueryFactory
                .select(freeBoard.count())
                .from(freeBoard)
                .where(users.id.eq(userId))
                .fetchOne();


        return new PageImpl<>(result,pageable,count);
    }
```
</details>
</details>

<br>



</details>

<hr>

## 느낀점

- JPA 프로젝트를 진행하며 
