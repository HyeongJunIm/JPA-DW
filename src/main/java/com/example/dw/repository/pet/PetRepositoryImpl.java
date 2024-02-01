package com.example.dw.repository.pet;

import com.example.dw.domain.dto.admin.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.example.dw.domain.entity.user.QPet.pet;
import static com.example.dw.domain.entity.user.QPetImg.petImg;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PetRepositoryImpl implements PetRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    //펫 리스트 조회 메소드
    @Override
    public List<PetDetailResultDto> findAllById(Long userId) {
        List<PetDetailDto> list = getPetList(userId);

        List<PetDetailResultDto> contests = list.stream().collect(groupingBy(r->new PetDetailResultDto(
                r.getId(),r.getBirthDate(),r.getName(),r.getWeight(),r.getPetGender(),r.getNeutering(),r.getUserId(),r.getPetCategory()),mapping(r->new PetImgDetailDto(
                        r.getPetImgId(),r.getPetFileName(),r.getPetPath(),r.getPetUuid(),r.getId()),toList()))).entrySet().stream().map(e-> new PetDetailResultDto(
                                e.getKey().getId(),e.getKey().getBirthDate(),e.getKey().getName(),e.getKey().getWeight(),e.getKey().getPetGender(),e.getKey().getNeutering(),
                e.getKey().getUserId(),e.getKey().getPetCategory(),e.getValue()
        )).collect(toList());

        System.out.println(contests.toString()+"pet 출력");

        return contests;
    }

    //해당 유저의 펫 전체 조회
    public List<PetDetailDto> getPetList(Long userId){
        System.out.println(userId+"조회 메소드");
        List<PetDetailDto> content= jpaQueryFactory.select(
                new QPetDetailDto(
                        pet.id,
                        pet.birthDate,
                        pet.name,
                        pet.weight,
                        pet.petGender,
                        pet.neutering,
                        pet.users.id,
                        petImg.id,
                        petImg.petFileName,
                        petImg.petPath,
                        petImg.petUuid,
                        pet.petCategory
                )).from(pet)
                .leftJoin(pet.petImg,petImg)
                .where(pet.users.id.eq(userId))
                .fetch();
        System.out.println(userId+"님의 조회 결과 입니다.");
        content.forEach(r-> System.out.println(r.getUserId()+"님의"+r.getId()+"번 동물조회"));
        System.out.println("왜 안나와?");
        return content;
    }

    /**
     * 펫 내용 수정을 위한 해당유저의 선택 펫 조회 쿼리문
     * @param petId
     * @param userId
     * @return
     */
    @Override
    public Optional<PetDetailResultDto> findByPetIdAndUserId(Long petId, Long userId) {
        PetDto petDtos = jpaQueryFactory
                .select(new QPetDto(
                        pet.id,
                        pet.birthDate,
                        pet.name,
                        pet.weight,
                        pet.petGender,
                        pet.neutering,
                        pet.users.id,
                        pet.petCategory
                ))
                .from(pet)
                .where(pet.id.eq(petId),pet.users.id.eq(userId))
                .fetchOne();


        Optional<PetDetailResultDto> contents =
                Optional.ofNullable(petDtos).map(petDto ->{
                    List<PetImgDetailDto> petImgDetailDtos =jpaQueryFactory
                        .select(new QPetImgDetailDto(
                                petImg.id,
                                petImg.petFileName,
                                petImg.petPath,
                                petImg.petUuid,
                                pet.id
                        ))
                            .from(petImg)
                            .leftJoin(petImg.pet, pet)
                            .where(pet.id.eq(petDto.getId()))
                            .fetch();

                    List<PetImgDetailDto> petimg = petImgDetailDtos.stream()
                            .map(petimgs -> new PetImgDetailDto(
                                    petimgs.getId(),
                                    petimgs.getPetFileName(),
                                    petimgs.getPetPath(),
                                    petimgs.getPetUuid(),
                                    petimgs.getPetId()
                            ))
                            .collect(Collectors.toList());

                    return  new PetDetailResultDto(
                            petDto.getId(),
                            petDto.getBirthDate(),
                            petDto.getName(),
                            petDto.getWeight(),
                            petDto.getPetGender(),
                            petDto.getNeutering(),
                            petDto.getUserId(),
                            petDto.getPetCategory(),
                            petimg
                    );
                });

        System.out.println(contents.toString()+" 조회");

        return contents;
    }

    @Override
    public List<PetImgDto> findAllPetById(Long petId) {
        return jpaQueryFactory
                .select(new QPetImgDto(
                        petImg.id,
                        petImg.petFileName,
                        petImg.petPath,
                        petImg.petUuid,
                        pet.id
                ))
                .from(petImg)
                .where(petImg.pet.id.eq(petId))
                .fetch();
    }
}
