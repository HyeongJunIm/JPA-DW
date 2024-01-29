package com.example.dw.controller;


import com.example.dw.domain.dto.admin.PetDetailResultDto;
import com.example.dw.domain.dto.admin.UserDetailListDto;
import com.example.dw.domain.form.OrderReviewForm;
import com.example.dw.domain.form.PetForm;
import com.example.dw.domain.form.PetUpdateForm;
import com.example.dw.domain.form.UserUpdateForm;
import com.example.dw.repository.order.OrderItemRepository;
import com.example.dw.repository.order.OrderItemRepositoryCustom;
import com.example.dw.repository.pet.PetRepositoryCustom;
import com.example.dw.repository.user.UsersRepository;
import com.example.dw.repository.user.UsersRepositoryCustom;
import com.example.dw.service.FileService;
import com.example.dw.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mypg/*")
@RequiredArgsConstructor
public class MypageController {

    private final UsersRepositoryCustom usersRepositoryCustom;
    private final UsersRepository usersRepository;

    private final MypageService mypageService;

    private final PetRepositoryCustom petRepositoryCustom;

    private final FileService fileService;

    private final OrderItemRepository orderItemRepository;
    private final OrderItemRepositoryCustom orderItemRepositoryCustom;


    @GetMapping("/main/{userId}")
    public String mypg(@PathVariable("userId")Long userId, Model model){

        if(userId ==null){
            throw new IllegalArgumentException("조회된 회원이 없습니다!");
        }


        Optional<UserDetailListDto> userDetailDto=usersRepositoryCustom.findOneByUserId(userId);
        userDetailDto.ifPresent(userDetails -> System.out.println(userDetails.toString() + " 회원입니다."));

        userDetailDto.ifPresent( userDetails -> model.addAttribute("userDetail",userDetails));

        return "/mypg/mypgmain";
    }

    @GetMapping("/modify/{userId}")
    public String modifyPage(@PathVariable("userId") Long userId, Model model){
        if(userId ==null){
            throw new IllegalArgumentException("조회된 회원이 없습니다!");
        }

        Optional<UserDetailListDto> userDetailDto=usersRepositoryCustom.findOneByUserId(userId);
        userDetailDto.ifPresent(userDetails -> System.out.println(userDetails.toString() + " 회원입니다."));

        userDetailDto.ifPresent( userDetails -> model.addAttribute("userDetail",userDetails));


        return  "/mypg/userupdate";
    }

    @PutMapping("/modify/{userId}/register")
    public RedirectView modifyUser(@PathVariable("userId") Long userId,
                                   UserUpdateForm userUpdateForm,
                                   @RequestParam("userFile")MultipartFile file)throws IOException {
        userUpdateForm.setId(userId);

        if(userUpdateForm.getUserPassword().equals("")){
           String passward= usersRepository.findById(userId).get().getUserPassword();
           userUpdateForm.setUserPassword(passward);
           mypageService.modify(userUpdateForm,file);
        }else{
            mypageService.modify(userUpdateForm,file);
        }


        return new RedirectView("/mypg/main/{userId}");
    }


    @GetMapping("/mypet/{userId}")
    public String petList(@PathVariable("userId")Long userId,Model model){

        List<PetDetailResultDto> petDetailResultDtoList = petRepositoryCustom.findAllById(userId);

        petDetailResultDtoList.forEach(r-> System.out.println(r.getUserId()+"의"+r.getName()+"입니다."));
        model.addAttribute("pet",petDetailResultDtoList);
        return "/mypg/mypgpet";
    }


    @GetMapping("/mypetregister")
    public String petRegister(){
        return "/mypg/registerpet";
    }


    @PostMapping("/petregister")
    public String register(PetForm petForm,
                                 @RequestParam("petImg")List<MultipartFile> files,
                                 @RequestParam("userId") Long userId
    )throws IOException{


        Long id = mypageService.register(petForm);

        fileService.registerPetImg(files, id);
        return "redirect:/mypg/mypet/" + userId;

    }


    @GetMapping("/petupdate/{petId}")
    public String petupdatePage(@PathVariable("petId") Long petId,
            @RequestParam("userId") Long userId, Model model){

        if(petId ==null && userId == null){
            throw new IllegalArgumentException("조회된 내용이 없습니다!");
        }


       Optional<PetDetailResultDto> petDetail = petRepositoryCustom.findByPetIdAndUserId(petId,userId);


        petDetail.ifPresent(details -> model.addAttribute("detail",details));



        return "/mypg/registerpetupdate";
    }

    @PutMapping("/petmodify/{petId}")
    public RedirectView modifyUser(@PathVariable("petId") Long petId,
                                    PetUpdateForm petUpdateForm,
                                   @RequestParam("petImg")List<MultipartFile> files)throws IOException {
        petUpdateForm.setId(petId);
        Long userId = petUpdateForm.getUserId();

        mypageService.modifyPet(petUpdateForm,files);

        return new RedirectView("/mypg/mypet/"+userId);
    }


    @GetMapping("/writepage/{userId}")
    public String writePage(){
        return "/mypg/mywrite";
    }

    @GetMapping("/myfreeboard/{userId}")
    public String myfreeboard(){
        return "/mypg/myfreeboard";
    }

    @GetMapping("/mywalking/{userId}")
    public String myregisterwalking(){
        return "/mypg/walkmate";
    }


    @GetMapping("/myapplicationwalk/{userId}")
    public String mmyapplicationwalking(){
        return "/mypg/myapplication";
    }

    @GetMapping("/orderpage/{userId}")
    public String orderpage(){

        return "/mypg/buylist";
    }

    @GetMapping("/productreview/{orderItemId}")
    public String productreview(@PathVariable("orderItemId") Long id,Model model){
        Long itemId =id;
        model.addAttribute("orderItem",itemId);
        return "/mypg/productreview";
    }

    @PostMapping("/reviewregister")
    public RedirectView writeregister(OrderReviewForm orderReviewForm,
                                      @RequestParam("reviewImg")List<MultipartFile> files,
                                      @RequestParam("userId") Long userId

    ) throws IOException
    {
        //정보 확인
        files.forEach(r-> System.out.println("[파일목록] : "+r.getOriginalFilename()));

        Long id = mypageService.registerreview(orderReviewForm);

        fileService.registerreviewImg(files, id);
        return new RedirectView("/mypg/myreviewpage/"+userId);
    }


    @GetMapping("/myreviewpage/{userId}")
    public String myreviewpage(){

        return "/mypg/productreviewlist";
    }
}
