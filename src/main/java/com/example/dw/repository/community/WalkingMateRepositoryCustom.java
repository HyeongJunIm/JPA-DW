package com.example.dw.repository.community;

import com.example.dw.domain.dto.admin.AdminWalkMateDetailDto;
import com.example.dw.domain.dto.community.*;
import com.example.dw.domain.dto.community.IndexWalkMateDto;
import com.example.dw.domain.dto.community.WalkMateDetailDto;
import com.example.dw.domain.dto.community.WalkMateListDto;

import com.example.dw.domain.form.SearchCateLocationForm;
import com.example.dw.domain.form.SearchLocationForm;
import com.example.dw.domain.form.SearchRecruitmentForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WalkingMateRepositoryCustom {

    Page<WalkMateListDto> findAllWalkMate(Pageable pageable, SearchLocationForm searchLocationForm);

    Page<WalkMateListDto> findAllWalkMate(Pageable pageable, SearchCateLocationForm searchCateLocationForm);

    Page<WalkMateMyDetailListDto> findAllWalkMateAndUserId(Pageable pageable, SearchRecruitmentForm searchRecruitmentForm, Long userId);

    Page<WalkMateMyApplicationListDto> findAllWalkMateStateAndUserId(Pageable pageable,SearchRecruitmentForm searchRecruitmentForm ,Long userId);

    Optional<WalkMateDetailDto> walkMateDetail(Long walkBoardId);

//    List<WalkDetailStateDto> applierPetsInfo(Long walkMateId);

    //관리자 페이지 산책글 상세
    AdminWalkMateDetailDto adminWalkMateDetail(Long walkMateId);


    //메인페이지 산책글 리스트
    List<IndexWalkMateDto> IndexWalkMateList();



}
