package com.example.dw.service;


import com.example.dw.domain.dto.community.QuestionDetailReplyDto;
import com.example.dw.domain.entity.question.Question;
import com.example.dw.domain.form.QuestionCommentForm;
import com.example.dw.domain.form.QuestionWritingForm;
import com.example.dw.repository.community.QuestionCommentRepository;
import com.example.dw.repository.community.QuestionCommentRepositoryCustom;
import com.example.dw.repository.community.QuestionRepository;
import com.example.dw.repository.user.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QnaService {

    private final FileService fileService;
    private final QuestionRepository questionRepository;
    private final UsersRepository usersRepository;

    private final QuestionCommentRepository questionCommentRepository;
    private final QuestionCommentRepositoryCustom questionCommentRepositoryCustom;
    // 글 작성, 사진 파일 저장
    @Transactional
    public Long register(QuestionWritingForm questionWritingForm) throws IOException {
        System.out.println(questionWritingForm.toString()+"WW");
        System.out.println(questionWritingForm.toEntity()+"11");
        Question question = questionRepository.save(questionWritingForm.toEntity());



        System.out.println(question.getUsers().getId()+"유저 아이디");
        System.out.println(question.getUsers().getUserName()+"유저 이름");
        return question.getId();

    }

    //질의 사항 전체 조회
    @Transactional
    public List<Question> qnaList(){
        List<Question> questionList = questionRepository.findAll();

        return questionList;
    }

    //작성 글 수정
    @Transactional
    public Question modify(QuestionWritingForm questionWritingForm, List<MultipartFile> files)
    throws IOException{

        if(!files.get(0).isEmpty()){
            fileService.removequeDetailImgs(questionWritingForm.getId());

            fileService.registerquestionImg(files,questionWritingForm.getId());
        }

        Question question = questionRepository.findById(questionWritingForm.getId()).get();

        //qna 기본내용 업데이트
        question.update(questionWritingForm);
        return Optional.ofNullable(question).orElseThrow(()->{
            throw new IllegalArgumentException("조회 정보 없음");});
    }

    //클릭시 해당 게시물 viewCount 변경
    @Transactional
    public Question updateViewCount(Long questionId){


        Question question = questionRepository.findById(questionId).get();
        question.increaseViewCount();


        return  questionRepository.save(question);
    }

    //게시판 삭제
    @Transactional
    public void delete(Long questionId){

        if(questionId == null){
            throw new IllegalArgumentException("없는 게시판 입니다");
        }
        fileService.removequeDetailImgs(questionId);
        questionRepository.deleteById(questionId);
    }


    @Transactional
    public void register(QuestionCommentForm questionCommentForm){

        questionCommentRepository.save(questionCommentForm.toEntity());


    }

    @Transactional
    public List<QuestionDetailReplyDto> getList(Long questionId){

        return questionCommentRepositoryCustom.findReplyByQuestionBoardId(questionId);

    }

    @Transactional
    public void deleteReply(Long questioncommentid ){

        questionCommentRepository.deleteById(questioncommentid);

    }


    @Transactional
    public void modifyQuestionComment(QuestionCommentForm questionCommentForm){

        questionCommentRepository.updateQuestionComment(questionCommentForm.getQuestionCommentContent(),questionCommentForm.getId());

    }


}
