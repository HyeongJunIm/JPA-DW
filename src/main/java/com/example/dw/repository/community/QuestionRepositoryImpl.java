package com.example.dw.repository.community;

import com.example.dw.domain.dto.community.*;
import com.example.dw.domain.entity.question.QQuestion;
import com.example.dw.domain.entity.question.QQuestionComment;
import com.example.dw.domain.form.SearchForm;
import com.example.dw.domain.form.SearchLocationForm;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.dw.domain.entity.question.QQuestion.question;
import static com.example.dw.domain.entity.question.QQuestionComment.questionComment;
import static com.example.dw.domain.entity.question.QQuestionImg.questionImg;
import static com.example.dw.domain.entity.user.QUserFile.userFile;
import static com.example.dw.domain.entity.user.QUsers.users;
import static com.example.dw.domain.entity.walkingMate.QWalkingMate.walkingMate;
import static java.util.stream.Collectors.*;


@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    // qna 리스트 확인
    @Override
    public Page<QuestionListDto> findQnaListBySearch(Pageable pageable, SearchForm searchForm) {


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

        }


        return new PageImpl<>(result, pageable, count);
    }

//
//    // 전체 페이지 조회 및 검색어 확인
    private Long getCount(String keyword){

        Long count = jpaQueryFactory
                .select(question.count())
                .from(question)
                .where(qnatitleEq(keyword))
                .fetchOne();

        return count;

    }
//
//
    private BooleanExpression qnatitleEq(String keyword){
        return StringUtils.hasText(keyword) ? question.questionTitle.containsIgnoreCase(keyword) : null;
    }

    @Override
    public List<QuestionDetailResultDto> findQnaById(Long id) {
        List<QuestionDetailDto> list = getQueDetail(id);


            List<QuestionDetailResultDto> result = list.stream().collect(groupingBy(r -> new QuestionDetailResultDto(
                    r.getId(),r.getQuestionTitle(),r.getQuestionContent(),r.getQuestionRd(),r.getUserId(),r.getUserName()),mapping(r->new QuestionImgDto(
                            r.getQuestionImgId(),r.getQuestionImgRoute(),r.getQuestionImgName(),r.getQuestionImgUuid(),r.getId()),toList())
            )).entrySet().stream().map(e -> new QuestionDetailResultDto(
                    e.getKey().getId(), e.getKey().getQuestionTitle(),e.getKey().getQuestionContent(),e.getKey().getQuestionRd(),e.getKey().getUserId(), e.getKey().getUserName(),
                    e.getValue()))
                    .collect(Collectors.toList());




        System.out.println(result.toString()+"나와라!!");

        return result;
    }

    @Override
    public List<QuestionImgDto> findAllByQuestionId(Long questionId) {
        return jpaQueryFactory.select(new QQuestionImgDto(
                questionImg.id,
                questionImg.questionImgRoute,
                questionImg.questionImgName,
                questionImg.questionImgUuid,
                questionImg.question.id
        )).from(questionImg)
                .where(questionImg.question.id.eq(questionId))
                .fetch();
    }




//    // 게시판 번호를 보내주면 데이터가 뽑히는지 확인

    private List<QuestionDetailDto> getQueDetail(Long id){
        System.out.println(id + "의 아이디 조회입니다.");
        List<QuestionDetailDto> questionDetailDtos = jpaQueryFactory
                .select(new QQuestionDetailDto(
                        question.id,
                        question.questionTitle,
                        question.questionContent,
                        question.questionRd,
                        questionImg.id,
                        questionImg.questionImgRoute,
                        questionImg.questionImgUuid,
                        questionImg.questionImgName,
                        question.users.id,
                        question.users.userName
                ))
                .from(question)
                .leftJoin(question.questionImg,questionImg)

                .where(question.id.eq(id))
                .fetch();

        questionDetailDtos.forEach(r-> System.out.println(r.toString()+"좋았어"));

        return questionDetailDtos;

    }


    /**
     * 내가 작성한 QnA글 조회 쿼리
     * @param pageable
     * @param userId 특정 유저 정보 필드
     * @return
     */
    @Override
    public Page<QuestionListDto> findQnaListById(Pageable pageable,Long userId) {

        Long count = getMypageCount(userId);

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

                )
                .from(question)
                .leftJoin(question.users, users)
                .leftJoin(users.userFile,userFile)
                .leftJoin(question.questionImg,questionImg)
                .where( users.id.eq(userId))
                .orderBy(
                        question.id.desc()
                )
                .fetch();

        List<QuestionListDto> result = new ArrayList<>();


        for(Tuple tuple : contents){
            Long queId = tuple.get(question.id);
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

        }


        return new PageImpl<>(result, pageable, count);



    }


    /**
     * 페이징 처리를 위한 유저가 작성한 글 전체 갯수 조회
     * @param userId
     * @return
     */
    private Long getMypageCount(Long userId){

        Long count = jpaQueryFactory
                .select(question.count())
                .from(question)
                .where(question.users.id.eq(userId))
                .fetchOne();
        System.out.println(count);

        return count;

    }


    /**
     * 페이지 라이오 버튼에 따라 동적으로 해당 내용 조회
     * @param searchForm
     * @return
     */
    private OrderSpecifier<?> getDynamicSort(SearchForm searchForm) {


        switch (searchForm.getCate()) {
            case "questionRd":
                return question.questionRd.desc();
            case "commentCount":
                return question.questionComment.size().desc();
            case "questionViewCount":
                return question.questionViewCount.desc();
            default:
                return question.questionRd.desc();
        }
    }


    @Override
    public List<QuestionPopularityListDto> findAllByQuestion() {



        List<QuestionPopularityListDto> result = jpaQueryFactory
                .select(new QQuestionPopularityListDto(
                        question.id,
                        question.questionTitle,
                        question.questionContent,
                        question.questionRd,
                        question.questionMd,
                        question.questionViewCount,
                        questionComment.count()

                ))
                .from(question)
                .leftJoin(question.questionComment, questionComment)
                .groupBy(
                        question.id,
                        question.questionTitle,
                        question.questionContent,
                        question.questionRd,
                        question.questionMd,
                        question.questionViewCount
                )
                .orderBy(question.questionViewCount.desc())
                .limit(3)
                .fetch();

        return result;
    }


}


