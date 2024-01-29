package com.example.dw.service;


import com.example.dw.domain.entity.goods.Goods;
import com.example.dw.domain.entity.order.OrderReview;
import com.example.dw.domain.entity.user.Pet;
import com.example.dw.domain.entity.user.Users;
import com.example.dw.domain.form.*;
import com.example.dw.repository.community.WalkingMateRepository;
import com.example.dw.repository.community.WalkingMateStateRepository;
import com.example.dw.repository.order.OrderItemRepository;
import com.example.dw.repository.order.OrderReviewRepository;
import com.example.dw.repository.pet.PetRepository;
import com.example.dw.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MypageService {

    private final FileService fileService;

    private final UsersRepository usersRepository;

    private final PetRepository petRepository;

    private final WalkingMateStateRepository walkingMateStateRepository;
    private final WalkingMateRepository walkingMateRepository;

    private final OrderReviewRepository orderReviewRepository;
    private final OrderItemRepository orderItemRepository;

    //회원정보 수정
    @Transactional
    public Users modify(UserUpdateForm userUpdateForm, MultipartFile file)
            throws IOException {

        //수정된 메인 사진이 있다면 기존 사진 삭제 후 수정된 사진으로 업데이트
        if(!file.isEmpty()) {
            System.out.println("사진 저장");
            //기존 사진 삭제
            fileService.removeUserImg(userUpdateForm.getId());

            //새로 수정된 사진 로컬 서버 저장 및 DB저장
            fileService.registerUserImg(file, userUpdateForm.getId());
        }else{
            System.out.println("입력된 메인 사진 없음");
        }




        Users users = usersRepository.findById(userUpdateForm.getId()).get();

        //상품 기본 내용 업데이트
        users.update(userUpdateForm);
        return Optional.ofNullable(users).orElseThrow(()->{
            throw new IllegalArgumentException("조회 정보 없음");
        });

    }

    //휴대폰 번호 중복체크
    @Transactional(readOnly = true)
    public boolean existsByUserPhone(String userPhone){
        return usersRepository.existsByUserPhone(userPhone);
    }


    //유저 닉네임 중복체크
    @Transactional(readOnly = true)
    public boolean existsByUserNickName(String userNickName){return  usersRepository.existsByUserNickName(userNickName);}

    /**
     * 등록된 펫 이름 중복확인
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public boolean existsByPetName(String name, Long userId){

        return petRepository.existsByNameAndUsersId(name,userId);
    }

    @Transactional
    public Long register(PetForm petForm)throws IOException{

        Pet pet = petRepository.save(petForm.toEntity());


        return pet.getId();

    }

    /**
     * 펫 정보 삭제
     * @param petId
     */
    @Transactional
    public void removePet(Long petId){
        if(petId ==null){
            throw new IllegalArgumentException("펫 정보가 없습니다.");
        }
        petRepository.deleteById(petId);

    }

    @Transactional
    public Pet modifyPet(PetUpdateForm petUpdateForm, List<MultipartFile> files)
            throws IOException {

        if(!files.isEmpty()) {
            System.out.println("사진 저장");
            fileService.removePetImgs(petUpdateForm.getId());

            fileService.registerPetImg(files, petUpdateForm.getId());
        }else{
            System.out.println("입력된 메인 사진 없음");
        }


        Pet pet=petRepository.findById(petUpdateForm.getId()).get();

        //상품 기본 내용 업데이트
        pet.update(petUpdateForm);
        return Optional.ofNullable(pet).orElseThrow(()->{
            throw new IllegalArgumentException("펫 정보 없음");
        });

    }

    // 산책글 신청인원 승인 처리
    @Transactional
    public Integer walkmatestateupdate(Long walkmatestateId){
        System.out.println(walkmatestateId);

        walkingMateStateRepository.upDateWalkMateState(walkmatestateId);

        Integer state = walkingMateStateRepository.findById(walkmatestateId).get().getState();
        System.out.println(state + "로 변경");
        System.out.println(walkmatestateId +"번 승인 완료");

        return state;
    }

    //산책글 신청인원 취소 처리

    @Transactional
    public Integer walkmatestatedown(Long walkmatestateId){

        walkingMateStateRepository.downWalkMateState(walkmatestateId);

        Integer state = walkingMateStateRepository.findById(walkmatestateId).get().getState();


        return state;
    }

    //산책글 모집완료 상태로 변경
    @Transactional
    public Long walkingmatestateupdate(Long walkingmateId){


        walkingMateRepository.updateWalkMateState(walkingmateId);

        Long walkingmatestate = walkingMateRepository.findById(walkingmateId).get().getWalkingMateState();


        return  walkingmatestate;
    }


    @Transactional
    public Long registerreview(OrderReviewForm orderReviewForm)throws IOException{

        OrderReview orderReview = orderReviewRepository.save(orderReviewForm.toEntity());

        orderItemRepository.updatrorderreview(orderReview.getOrderItem().getId());

        return orderReview.getId();

    }

    @Transactional
    public void removeReview(Long id){

        if(id == null){
            throw new IllegalArgumentException("id 정보가 없습니다.");
        }
        fileService.removeReviewImgs(id);
        Long orderItemId =   orderReviewRepository.reviewId(id);
        orderItemRepository.downorderreview(orderItemId);
        orderReviewRepository.deleteById(id);

    }

    //회원 탈퇴
    @Transactional
    public void removeUser(Long userId){

        if(userId == null){
            throw new IllegalArgumentException("회원정보가 없습니다.");
        }

      usersRepository.deleteUsersById(userId);
    }


}
