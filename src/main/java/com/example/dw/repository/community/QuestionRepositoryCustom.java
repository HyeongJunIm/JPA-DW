package com.example.dw.repository.community;

import com.example.dw.domain.dto.community.*;
import com.example.dw.domain.entity.question.QuestionImg;
import com.example.dw.domain.form.SearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionRepositoryCustom {
    Page<QuestionListDto> findQnaListBySearch(Pageable pageable,SearchForm searchForm);

    List<QuestionDetailResultDto> findQnaById(Long id);

    List<QuestionImgDto> findAllByQuestionId(Long questionId);

    Page<QuestionListDto> findQnaListById(Pageable pageable,Long userId);

    List<QuestionPopularityListDto> findAllByQuestion();
}
