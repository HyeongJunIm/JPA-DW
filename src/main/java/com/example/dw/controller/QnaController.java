package com.example.dw.controller;


import com.example.dw.domain.dto.community.QuestionDetailResultDto;
import com.example.dw.domain.dto.community.QuestionPopularityListDto;
import com.example.dw.domain.form.QuestionWritingForm;
import com.example.dw.repository.community.QuestionRepository;
import com.example.dw.repository.community.QuestionRepositoryCustom;
import com.example.dw.service.FileService;
import com.example.dw.service.QnaService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/qna/*")
public class QnaController {

    private final FileService fileService;
    private final QuestionRepositoryCustom questionRepositoryCustom;
    private final QnaService qnaService;

    @GetMapping("/qnaLists")
    public String page(Model model){
        List<QuestionPopularityListDto> result = questionRepositoryCustom.findAllByQuestion();
        model.addAttribute("result",result);
        return "/community/qnaList";
    }

    @GetMapping("/qnawrite")
    public String qnawrite(){
        return "/community/writingQna";
    }


    @PostMapping("/qnaregister")
    public RedirectView write(QuestionWritingForm questionWritingForm,
                              @RequestParam("questionImg")List<MultipartFile> files

    ) throws IOException
    {
        files.forEach(r-> System.out.println("[파일목록] : "+r.getOriginalFilename()));

        Long id = qnaService.register(questionWritingForm);

        fileService.registerquestionImg(files, id);
        return new RedirectView("/qna/qnaLists");
    }

    @GetMapping("/qnaDetail/{questionId}")
    public String detailPage(@PathVariable("questionId") Long questionId , Model model, HttpServletRequest req, HttpServletResponse res){

        // 조회수 업데이트 여부를 결정하는 변수를 선언
        boolean updateCount = true;

        // 쿠키 가져오기(현재 접속 쿠기 배열로 가져오기)
        Cookie[] cookies = req.getCookies();


        if(cookies != null){
            for(Cookie cookie : cookies){
                if("qna_view_count_cookie".equals(cookie.getName())){
                    String cookieValue = cookie.getValue();
                    String[] values =  cookieValue.split("_");
                    String qnaId = values[0];

                    long storedTimestamp = Long.parseLong(values[1]);
                    long currentTimestamp = new Date().getTime();

                    if(qnaId.equals(String.valueOf(questionId)) && (currentTimestamp - storedTimestamp) < (24 * 60 * 60 * 1000)){
                        updateCount =false;
                        break;
                    }

                }
            }
        }


        if(updateCount){

            Cookie newCookie = new Cookie("qna_view_count_cookie", questionId + "_" + new Date().getTime());


            newCookie.setMaxAge(24 * 60 * 60);

            res.addCookie(newCookie);

            //조회수 증가
            qnaService.updateViewCount(questionId);

        }
        List<QuestionDetailResultDto> detailresult = questionRepositoryCustom.findQnaById(questionId);
        model.addAttribute("detail", detailresult);


        return "/community/qnaDetail";
    }


    @GetMapping("/modify/{questionId}")
    public String modifyPage(@PathVariable("questionId") Long questionId,Model model){
        List<QuestionDetailResultDto> result = questionRepositoryCustom.findQnaById(questionId);
        model.addAttribute("question", result);
        return "/community/writingModifyQna";


    }

    @PutMapping("/modify/{questionId}/edit")
    public RedirectView questionModify(@PathVariable("questionId") Long questionId,
                                       QuestionWritingForm questionWritingForm,
                                       @RequestParam("questionImg") List<MultipartFile> files) throws IOException{
        questionWritingForm.setId(questionId);
        qnaService.modify(questionWritingForm,files);
        return new RedirectView("/qna/qnaLists");
    }

    @GetMapping("/delete/{questionId}")
    public RedirectView removeQuestion(@PathVariable("questionId") Long questionId){
        qnaService.delete(questionId);
        return new RedirectView("/qna/qnaLists");
    }

}
