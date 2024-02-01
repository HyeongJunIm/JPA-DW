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

    /**
     * 회원정보 수정 서비스
     * @param userUpdateForm 회원 수정정보
     * @param file 이미지 수정
     * @return
     * @throws IOException
     */
    @Transactional
    public Users modify(UserUpdateForm userUpdateForm, MultipartFile file)
            throws IOException {

        if(!file.isEmpty()) {
            System.out.println("사진 저장");
            fileService.removeUserImg(userUpdateForm.getId());

            fileService.registerUserImg(file, userUpdateForm.getId());
        }else{
            System.out.println("입력된 메인 사진 없음");
        }




        Users users = usersRepository.findById(userUpdateForm.getId()).get();

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
     * 유저에게 등록된 펫 이름 중복확인
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public boolean existsByPetName(String name, Long userId){

        return petRepository.existsByNameAndUsersId(name,userId);
    }

    /**
     * 펫의 정보를 repository 기본 메소들 활용 저장
     * @param petForm
     * @return
     * @throws IOException
     */
    @Transactional
    public Long register(PetForm petForm)throws IOException{
        Pet pet = petRepository.save(petForm.toEntity());
        return pet.getId();

    }

    /**
     * repository 기본 메소드를 활용한 펫 정보 삭제
     * @param petId
     */
    @Transactional
    public void removePet(Long petId){
        if(petId ==null){
            throw new IllegalArgumentException("펫 정보가 없습니다.");
        }
        petRepository.deleteById(petId);

    }

    /**
     * 펫의 새로운 정보로 수정
     * @param petUpdateForm 새로운 펫 정보 저장소
     * @param files 새로운 펫의 이미지 저장소
     * @return
     * @throws IOException
     */
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
