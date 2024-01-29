package com.example.dw.api;


import com.example.dw.domain.dto.community.QuestionDetailReplyDto;
import com.example.dw.domain.dto.community.QuestionListDto;
import com.example.dw.domain.dto.community.WalkMateDetailReplyDto;
import com.example.dw.domain.form.QuestionCommentForm;
import com.example.dw.domain.form.SearchForm;
import com.example.dw.repository.community.QuestionCommentRepository;
import com.example.dw.repository.community.QuestionRepositoryCustom;
import com.example.dw.service.QnaService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qnar/*")
public class QnaListApiController {
    private final QuestionRepositoryCustom questionRepositoryCustom;
    private final QnaService qnaService;
    @Value("${file.que}")
    private String filequeImg;

    @GetMapping("/queImg")
    public byte[] getEmpImg(String fileFullPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(filequeImg, fileFullPath));
    }

    @Value("${file.user}")
    private String userImg;

    //산책메이트 상세보기 댓글 유저 사진
    @GetMapping("/qnaUserImg")
    public byte[] getUserImg(String userImgPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(userImg, userImgPath));
    }

    @GetMapping("/qnalist/{page}")
    public Page<QuestionListDto> findQnAList(
            @PathVariable("page") int page, SearchForm searchForm){
        Pageable pageable = PageRequest.of(page,5);
        Page<QuestionListDto> result = questionRepositoryCustom.findQnaListBySearch(pageable,searchForm);

        return result;

    }

    @PostMapping("/questionReply")
    public void registerQuestionReply(QuestionCommentForm questionCommentForm){
        qnaService.register(questionCommentForm);
    }

    @GetMapping("/showQuestionReplyList/{questionId}")
    public List<QuestionDetailReplyDto> showReplyList(
            @PathVariable("questionId") Long questionId
    ){
        return  qnaService.getList(questionId);
    }

 // 댓글 삭제
    @DeleteMapping("/questionCommentReplyDelete/{questioncommentid}")
    public void removeReply(@PathVariable("questioncommentid")Long questioncommentid){

        qnaService.deleteReply(questioncommentid);
    }

//댓글수정
    @PatchMapping("/questionReply")
    public void modifyQuestionReply(QuestionCommentForm questionCommentForm){


        qnaService.modifyQuestionComment(questionCommentForm);


    }


}
