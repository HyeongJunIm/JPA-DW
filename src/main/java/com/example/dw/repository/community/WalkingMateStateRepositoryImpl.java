package com.example.dw.repository.community;


import com.example.dw.domain.dto.community.QWalkMateMyApplicationListDto;
import com.example.dw.domain.dto.community.WalkMateMyApplicationListDto;
import com.example.dw.domain.form.SearchRecruitmentForm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.dw.domain.entity.walkingMate.QWalkingMateState.walkingMateState;
import static com.example.dw.domain.entity.walkingMate.QWalkingMate.walkingMate;
import static com.example.dw.domain.entity.user.QUsers.users;
import static com.example.dw.domain.entity.user.QPet.pet;
import static com.example.dw.domain.entity.user.QPetImg.petImg;

@Repository
@RequiredArgsConstructor
public class WalkingMateStateRepositoryImpl implements WalkingMateStateRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;


}
