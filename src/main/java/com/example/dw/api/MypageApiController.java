package com.example.dw.api;

import com.example.dw.domain.dto.community.QuestionListDto;
import com.example.dw.domain.dto.community.WalkMateMyApplicationListDto;
import com.example.dw.domain.dto.community.WalkMateMyDetailListDto;
import com.example.dw.domain.dto.community.*;
import com.example.dw.domain.dto.order.OrderItemDto;
import com.example.dw.domain.dto.order.OrderItemReviewListDto;
import com.example.dw.domain.dto.order.OrderListResultDto;
import com.example.dw.domain.dto.order.OrderResultList;
import com.example.dw.domain.form.QuestionCommentForm;
import com.example.dw.domain.form.SearchRecruitmentForm;
import com.example.dw.repository.community.QuestionRepositoryCustom;
import com.example.dw.repository.community.WalkingMateRepositoryCustom;
import com.example.dw.repository.community.WalkingMateStateRepository;
import com.example.dw.repository.community.WalkingMateStateRepositoryCustom;
import com.example.dw.repository.freeBoard.FreeBoardRepositoryCustom;
import com.example.dw.repository.order.OrderItemRepositoryCustom;
import com.example.dw.repository.order.OrderListRepositoryCustom;
import com.example.dw.repository.order.OrderRepositoryCustom;
import com.example.dw.repository.order.OrderReviewRepositoryCustom;
import com.example.dw.service.FileService;
import com.example.dw.service.FreeBoardService;
import com.example.dw.service.MypageService;
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
@RequestMapping()
public class MypageApiController {

    private final MypageService mypageService;
    private final FileService fileService;

    private final QuestionRepositoryCustom questionRepositoryCustom;

    private final FreeBoardRepositoryCustom freeBoardRepositoryCustom;
    private final FreeBoardService freeBoardService;

    private final WalkingMateRepositoryCustom walkingMateRepositoryCustom;
    private final WalkingMateStateRepositoryCustom walkingMateStateRepositoryCustom;
    private final WalkingMateStateRepository walkingMateStateRepository;

    private final OrderItemRepositoryCustom orderItemRepositoryCustom;
    private final OrderListRepositoryCustom orderListRepositoryCustom;
    private final OrderRepositoryCustom orderRepositoryCustom;
    private final OrderReviewRepositoryCustom orderReviewRepositoryCustom;



    @Value("${file.pet}")
    private String filepetImg;

    @GetMapping("/mypgs/petImg")
    public byte[] getEmpImg(String fileFullPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(filepetImg, fileFullPath));
    }

    @Value("${file.dir}")
    private String filegoods;

    @GetMapping("/mypgs/goods")
    public byte[] getGoodsImg(String fileFullPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(filegoods, fileFullPath));
    }

    @Value("${file.review}")
    private String reviewImg;

    @GetMapping("/mypgs/reviews")
    public byte[] getreviewImg(String fileFullPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(reviewImg, fileFullPath));
    }

    @Value("${file.user}")
    private  String userImg;

    @GetMapping("/mypgs/userImg")
    public byte[] getuserImg(String fileFullPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(userImg, fileFullPath));
    }


    @Value("${file.free}")
    private String freeImg;

    @GetMapping("/mypgs/freeImg")
    public byte[] getfreeImg(String fileFullPath) throws IOException {
        return FileCopyUtils.copyToByteArray(new File(freeImg, fileFullPath));
    }


    @PostMapping("/mypgs/phone/check")
    public boolean checkPhoneDuplication(@RequestParam("userPhone") String userPhone) {
        if (userPhone == null) {
            throw new IllegalArgumentException("폰 번혼 누락");

        }
        if (mypageService.existsByUserPhone(userPhone) == true) {
            return false;
        } else {
            return true;
        }
    }

    //닉네임 검사
    @PostMapping("/mypgs/nickName/check")
    public boolean checkUserNickName(@RequestParam("userNickName") String userNickName) {
        if (userNickName == null) {
            throw new IllegalArgumentException("폰 번혼 누락");

        }
        if (mypageService.existsByUserNickName(userNickName) == true) {
            return false;
        } else {
            return true;
        }
    }



    @PostMapping("/mypgs/name/check")
    public boolean checkPetNameDuplication(@RequestParam("name") String name,
                                           @RequestParam("userId") Long userId) {
        if (name == null || userId == null) {
            throw new IllegalArgumentException("누락");

        }

        if (mypageService.existsByPetName(name, userId) == true) {
            return false;
        } else {
            return true;
        }
    }


    @PostMapping("/mypgs/remove/{petId}")
    public void petInfoDelete(@PathVariable("petId") Long petId) {
        mypageService.removePet(petId);

    }

    @GetMapping("/mypgs/myWriteList/{page}/{userId}")
    public Page<QuestionListDto> findQnAList(
            @PathVariable("page") int page, @PathVariable("userId") Long userId) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<QuestionListDto> result = questionRepositoryCustom.findQnaListById(pageable, userId);


        return result;

    }



    @GetMapping("/mypgs/myregisterwalkmatewrite/{page}/{userId}")
    public Page<WalkMateMyDetailListDto> findmyregisterwalkmatewriteList(
            @PathVariable("page") int page, @PathVariable("userId") Long userId, SearchRecruitmentForm searchRecruitmentForm
    ) {
        System.out.println(page);

        Pageable pageable = PageRequest.of(page, 4);
        Page<WalkMateMyDetailListDto> result = walkingMateRepositoryCustom.findAllWalkMateAndUserId(pageable,searchRecruitmentForm, userId);
        return result;
    }

    @GetMapping("/mypgs/applicationwalkmate/{page}/{userId}")
    public Page<WalkMateMyApplicationListDto> findapplicationwalkmateList(
            @PathVariable("page") int page, @PathVariable("userId") Long userId,SearchRecruitmentForm searchRecruitmentForm
    ) {

        Pageable pageable = PageRequest.of(page, 4);
        Page<WalkMateMyApplicationListDto> result = walkingMateRepositoryCustom.findAllWalkMateStateAndUserId(pageable,searchRecruitmentForm ,userId);
        return result;
    }

    @PatchMapping("/mypgs/walkmatestateupdate/{walkmatestateId}")
    public Integer findapplicationwalkmateupdate(
            @PathVariable("walkmatestateId") Long walkmatestateId
    ) {
        Integer state = mypageService.walkmatestateupdate(walkmatestateId);
        return  state;
    }

    @PatchMapping("/mypgs/walkmatestatedown/{walkmatestateId}")
    public Integer findapplicationwalkmatedown(
            @PathVariable("walkmatestateId") Long walkmatestateId
    ) {
        Integer state = mypageService.walkmatestatedown(walkmatestateId);
        return  state;
    }

    @PatchMapping("/mypgs/walkingmatestateupdate/{walkingmateId}")
    public Long walkingmateupdate(
            @PathVariable("walkingmateId") Long walkingmateId
    ){
        Long walkingmatestate = mypageService.walkingmatestateupdate(walkingmateId);
        return walkingmatestate;
    }

    @GetMapping("/mypgs/orderList/{page}/{userId}")
    public Page<OrderListResultDto> orderList(@PathVariable("page") int page,@PathVariable("userId") Long userId){
        Pageable pageable =PageRequest.of(page,4);
        Page<OrderListResultDto> result = orderRepositoryCustom.findAllByMyOrderId(pageable,userId) ;
        return result;
    }



    @GetMapping("/mypgs/myreviewlist/{page}/{userId}")
    public Page<OrderItemReviewListDto> myreviewlist(@PathVariable("page")int page,@PathVariable("userId")Long userId){

        Pageable pageable = PageRequest.of(page,5);

        Page<OrderItemReviewListDto> result = orderReviewRepositoryCustom.findAllReview(pageable,userId);


        return result;
    }

    @PostMapping("/mypgs/deletereview/{id}")
    public void removeReview(@PathVariable("id")Long id){
        mypageService.removeReview(id);
    }

    @PostMapping("/mypgs/userRemove/{userId}")
    public void removeUser(@PathVariable("userId")Long userId){
        if(userId == null){
            throw new IllegalArgumentException("회원번호가 없습니다.");
        }
        mypageService.removeUser(userId);
    }

    @GetMapping("/mypgs/myfreeBoardLists/{page}/{userId}")
    public Page<MyFreeBoardResultDto> myfreeBoardList(
            @PathVariable("page") int page,@PathVariable("userId") Long userId) {

        Pageable pageable = PageRequest.of(page, 5);

        Page<MyFreeBoardResultDto> result = freeBoardRepositoryCustom.findByUserId(pageable,userId);


        return result;
    }



}
