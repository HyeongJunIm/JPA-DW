package com.example.dw.repository.user;

import com.example.dw.domain.dto.admin.*;
import com.example.dw.domain.dto.user.QUserPetDto;
import com.example.dw.domain.dto.user.UserPetDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.dw.domain.entity.freeBoard.QFreeBoard.freeBoard;
import static com.example.dw.domain.entity.freeBoard.QFreeBoardComment.freeBoardComment;
import static com.example.dw.domain.entity.goods.QGoods.goods;
import static com.example.dw.domain.entity.order.QOrderItem.orderItem;
import static com.example.dw.domain.entity.order.QOrders.orders;
import static com.example.dw.domain.entity.question.QQuestion.question;
import static com.example.dw.domain.entity.question.QQuestionComment.questionComment;
import static com.example.dw.domain.entity.user.QPet.pet;
import static com.example.dw.domain.entity.user.QPetImg.petImg;
import static com.example.dw.domain.entity.user.QUserFile.userFile;
import static com.example.dw.domain.entity.user.QUsers.users;
import static com.example.dw.domain.entity.walkingMate.QWalkingMate.walkingMate;
import static com.example.dw.domain.entity.walkingMate.QWalkingMateComment.walkingMateComment;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class UsersRepositoryImpl implements UsersRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<UserListDto> findByAll(Pageable pageable, String cate, String keyword, String userState) {

        List<UserListDto> content = getUserList(pageable, cate, keyword ,userState);
        Long counts = getCount(cate, keyword ,userState);

        return new PageImpl<>(content, pageable, counts);
    }
    //관리자페이지 회원 상세보기
    @Override
    public AdminUserDetailResultDto findByUserId(Long userId) {

        List<AdminUserDetailDto> userDetailInfo = jpaQueryFactory.select(
                new QAdminUserDetailDto(
                        users.id,
                        users.userAccount,
                        users.userName,
                        users.userNickName,
                        users.userPhone,
                        users.userEmail,
                        users.userJoinDate,
                        users.address.zipCode,
                        users.address.address,
                        users.address.detail,
                        users.userIntroduction,
                        userFile.id,
                        userFile.route,
                        userFile.uuid,
                        userFile.name,
                        pet.id,
                        pet.birthDate,
                        pet.name,
                        pet.weight,
                        pet.petGender,
                        pet.neutering,
                        pet.petCategory,
                        petImg.id,
                        petImg.petPath,
                        petImg.petUuid,
                        petImg.petFileName
                )

        )       .from(users)
                .leftJoin(users.userFile, userFile)
                .leftJoin(users.pet, pet)
                .leftJoin(pet.petImg, petImg)
                .where(users.id.eq(userId))
                .fetch();

        return Optional.ofNullable(

                new AdminUserDetailResultDto(

                        userDetailInfo.stream().findFirst().map(
                                o-> new AdminUserDetailInfo(o.getId(), o.getUserAccount(), o.getUserName(), o.getUserNickName(), o.getUserPhone(),
                                        o.getUserEmail(), o.getUserJoinDate(), o.getZipCode(), o.getAddress(), o.getDetail(), o.getIntro(),
                                        new AdminUserDetailImgDto(o.getUserImgId(), o.getUserImgPath(), o.getUserImgUuid(), o.getUserImgName()))).get()

                        ,

                        userDetailInfo.stream().collect(mapping(r->new AdminUserPetDetailDto(
                                r.getPetId(), r.getBirthDate(), r.getPetImgName(), r.getWeight(), r.getPetGender(), r.getNeutering(),r.getPetCategory(),
                                new AdminUserPetImgDto(r.getPetImgId(), r.getPetImgPath(), r.getPetImgUuid(), r.getPetImgName())),toList()
                        )))
        ).orElseThrow(()->{
           throw new IllegalArgumentException("정보 없음");
        });

    }

    //관리자 페이지 회원상세-주문내역
    @Override
    public Page<AdminUserDetailOrderResultWithTotalPriceDto> userPaymentList(Pageable pageable, Long userId) {

        //루트 쿼리 조회
        List<AdminUserDetailOrderDto> content = jpaQueryFactory.select(
                new QAdminUserDetailOrderDto(
                        orders.id,
                        orders.orderRegisterDate
                )
        )
                .from(orders)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orders.id.desc())
                .where(orders.users.id.eq(userId))
                .fetch();


        List<AdminUserDetailOrderResultDto> result =
                content.stream().map(
                        o-> {
                            List<AdminUserDetailPaymentListDto> orderItems = jpaQueryFactory.select(
                                    new QAdminUserDetailPaymentListDto(
                                            orderItem.goods.id,
                                            orderItem.goods.goodsName,
                                            orderItem.orderQuantity,
                                            orderItem.orderPrice
                                    )
                            )
                                    .from(orderItem)
                                    .leftJoin(orderItem.orders, orders)
                                    .leftJoin(orderItem.goods, goods)
                                    .where(orderItem.orders.id.eq(o.getOrderId()))
                                    .fetch();

                            List<AdminUserDetailPaymentListDto> orderItemDtoList =
                                    orderItems.stream().map(
                                            orderItemsDto -> new AdminUserDetailPaymentListDto(
                                                    orderItemsDto.getGoodsId(),
                                                    orderItemsDto.getGoodsName(),
                                                    orderItemsDto.getOrderQuantity(),
                                                    orderItemsDto.getOrderPrice()
                                            )
                                    ).collect(toList());

                            return new AdminUserDetailOrderResultDto(
                                    o.getOrderId(),
                                    o.getOrderRd(),
                                    orderItemDtoList

                            );
                        }
                ).collect(toList());

        Long getTotal = jpaQueryFactory.select(
                orders.count()
        )
                .from(orders)
                .where(orders.users.id.eq(userId))
                .fetchOne();


        Integer totalOrderPrice = jpaQueryFactory.select(
                orderItem.orderQuantity.multiply(orderItem.orderPrice).sum()
        )
                .from(orders)
                .leftJoin(orders.orderItems, orderItem)
                .where(orders.users.id.eq(userId))
                .fetchOne();

        System.out.println("[ 합계금액 ] : " + totalOrderPrice);


        return new PageImpl<>(List.of(new AdminUserDetailOrderResultWithTotalPriceDto(totalOrderPrice, result)
)                   , pageable
                    , getTotal);
    }

//    private List<Long> toOrderIds(List<AdminUserDetailOrderResultDto> rootQuery){
//
//        return rootQuery.stream().map(o->o.getOrderId()).collect(toList());
//    }
//
//    private Map<Long, List<AdminUserDetailPaymentListDto>> findOrderItemMap(List<Long> orderIds){
//
//        List<AdminUserDetailPaymentListDto> orderItems = jpaQueryFactory.select(
//                new QAdminUserDetailPaymentListDto(
//                        orders.id,
//                orderItem.goods.id,
//                orderItem.goods.goodsName,
//                orderItem.orderQuantity,
//                orderItem.orderPrice
//        ))
//                .from(orderItem)
//                .leftJoin(orderItem.orders, orders)
//                .leftJoin(orderItem.goods, goods)
//                .where(orderItem.orders.id.in(orderIds))
//                .fetch();
//
//        return orderItems.stream().collect(Collectors.groupingBy(
//                AdminUserDetailPaymentListDto::getGoodsId
//        ));
//        //키가 orderId, 값은 orderItems
//
//    }


    //관리자페이지 회원상세-qna리스트
    @Override
    public Page<AdminUserDetailQnaListDto> userDetailQnaList(Pageable pageable, Long userId) {

        List<AdminUserDetailQnaListDto> qnaListResults =
                jpaQueryFactory.select( new QAdminUserDetailQnaListDto(
                        question.id,
                        question.questionTitle,
                        question.questionRd,
                        question.questionViewCount,
                        questionComment.count()
                ))
                        .from(question)
                        .leftJoin(question.questionComment, questionComment)
                        .where(question.users.id.eq(userId))
                        .orderBy(question.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .groupBy(question.id, question.questionTitle ,question.questionRd, question.questionViewCount)
                        .fetch();


        Long getTotal = jpaQueryFactory.select(
                question.count()
        )
                .from(question)
                .where(question.users.id.eq(userId))
                .fetchOne();

        System.out.println(qnaListResults.toString()+"!@#!@#!@#!@#!@#!@");

        return new PageImpl<>(qnaListResults, pageable, getTotal);
    }

    //관리자페이지 회원상세-자유게시판 리스트
    @Override
    public Page<AdminUserDetailFreeBoardListDto> userDetailFreeBoardList(Pageable pageable, Long userId) {
        List<AdminUserDetailFreeBoardListDto> freeListDtoQueryResults =
                jpaQueryFactory.select(new QAdminUserDetailFreeBoardListDto(
                        freeBoard.id,
                        freeBoard.freeBoardTitle,
                        freeBoard.freeBoardRd,
                        freeBoard.freeBoardViewCount,
                        freeBoardComment.count()
                ))
                    .from(freeBoard)
                    .leftJoin(freeBoard.freeBoardComment, freeBoardComment)
                    .where(freeBoard.users.id.eq(userId))
                        .groupBy(freeBoard.id, freeBoard.freeBoardTitle, freeBoard.freeBoardRd, freeBoard.freeBoardViewCount)
                        .orderBy(freeBoard.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                    .fetch();

        Long getTotal = jpaQueryFactory.select(
                freeBoard.count()
        )
                .from(freeBoard)
                .where(freeBoard.users.id.eq(userId))
                .fetchOne();




        return new PageImpl<>(freeListDtoQueryResults, pageable, getTotal);
    }



    //관리자페이지 회원상세-산책리스트
    @Override
    public Page<AdminUserDetailWalkMateDto> userDetailWalkMateList(Pageable pageable, Long userId) {

        List<AdminUserDetailWalkMateDto> walkListDtoQueryResults =
                jpaQueryFactory.select(new QAdminUserDetailWalkMateDto(
                        walkingMate.id,
                        walkingMate.walkingMateTitle,
                        walkingMate.walkingMateRd,
                        walkingMate.walkingMateViewCount,
                        walkingMateComment.count()
                ))
                        .from(walkingMate)
                        .leftJoin(walkingMate.walkingMateComment, walkingMateComment)
                        .where(walkingMate.users.id.eq(userId))
                        .groupBy(walkingMate.id, walkingMate.walkingMateTitle,walkingMate.walkingMateRd, walkingMate.walkingMateViewCount)
                        .orderBy(walkingMate.id.desc())
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset())
                        .fetch();


        Long getTotal = jpaQueryFactory.select(walkingMate.count())
                .from(walkingMate)
                .where(walkingMate.users.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(walkListDtoQueryResults, pageable, getTotal);
    }


    //등록된 펫 정보
    @Override
    public List<UserPetDto> findAllPetByUserId(Long userId) {
        return jpaQueryFactory.select(new QUserPetDto(
                pet.id,
                pet.name
        ))
                .from(pet)
                .where(pet.users.id.eq(userId))
                .fetch();
    }

    @Override
    public List<AdminUserChartDto> findJoinCountByAll() {
        return getDailyJoinCount();
    }

    @Override
    public Map<String, List> newUserStatus() {
        return getUserStatsBy();
    }


    private Long getCount(String cate, String keyword, String userState){
        return jpaQueryFactory.select(users.count())
                .from(users)
                .where(
                        userStateEq(userState),
                        cateEq(cate, keyword)
                )
                .fetchOne();
    }


    //회원리스트
    private List<UserListDto> getUserList(Pageable pageable, String cate, String keyword, String userState){

        List<UserListDto> userStanInfo = jpaQueryFactory.select(new QUserListDto(
                users.id,
                users.userAccount,
                users.userName,
                users.userEmail,
                users.userPhone,
                users.userJoinDate,
                users.userState,
                jpaQueryFactory.select(
                        question.count()
                )
                    .from(question)
                    .where(question.users.id.eq(users.id)),
                jpaQueryFactory.select(
                        freeBoard.count()
                )
                .from(freeBoard)
                .where(freeBoard.users.id.eq(users.id)),
                jpaQueryFactory.select(
                        walkingMate.count()
                )
                .from(walkingMate)
                .where(walkingMate.users.id.eq(users.id))

        ))
                .from(users)
                .where(
                        userStateEq(userState),
                        cateEq(cate, keyword)
                )
                .groupBy(users.id, users.userAccount, users.userName, users.userEmail, users.userPhone, users.userJoinDate, users.userState)
                .orderBy(users.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();




        return userStanInfo;
    }





    //주단위 일별 회원가입자 수
    public List<AdminUserChartDto> getDailyJoinCount() {
        LocalDate nowDate = LocalDate.now();
        LocalDate startDate = nowDate.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)).atStartOfDay().minusWeeks(3).toLocalDate();

        // 일주일 간의 날짜 목록 생성
        List<LocalDate> datesInRange = startDate.datesUntil(nowDate.plusDays(1))
                .collect(Collectors.toList());

        Map<LocalDate, Long> dailyCounts = jpaQueryFactory
                .select(users.userJoinDate, users.count())
                .from(users)
                .where(users.userJoinDate.between(startDate, nowDate))
                .orderBy(users.userJoinDate.desc())
                .groupBy(users.userJoinDate)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        //튜플로 반환되므로 튜플에서 꺼내서 Map으로 감싸준다.
                        tuple -> tuple.get(users.userJoinDate),
                        tuple -> tuple.get(users.count()),
                        (count1, count2) -> count1 + count2 
                        //동일한 날짜값이 있으면 duplicate 에러 발생
                        //count1은 날짜 , count2는 새로운 데이터값이다.
                        //에러 방지를 위해 두 값을 서로 합쳐준다. -> 즉 카운트 합산
                ));

        //위에서 생성한 일주일 간 날짜를 가져온다.(datesInRange)
        // 빈 값을 가진 날짜에 대한 결과 추가
        for (LocalDate date : datesInRange) {
            dailyCounts.putIfAbsent(date, 0L);
        }

        //메소드의 반환타입인 List<AdminUserChartDto>에 맞추기위해 entrySet().map()을 이용하여 변환한다.
        return dailyCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(entry -> new AdminUserChartDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }






    private Map<String, List> getUserStatsBy() {
        LocalDate nowDate = LocalDate.now();

        //신규 가입 회원 현황
        List<UserRecentJoinDto> recentJoinDtoList = jpaQueryFactory.select(new QUserRecentJoinDto(
                users.id,
                users.userAccount,
                users.userName,
                users.userEmail,
                users.userPhone,
                users.userJoinDate

        ))
                .from(users)
                .orderBy(users.id.desc())
                .limit(4)
                .where()
                .fetch();

        //전체 가입자 수
        Long totalCount = jpaQueryFactory.select(
                users.count()
        )
                .from(users)
                .fetchOne();

        //당일 가입 회원 수
        Long joinCount = jpaQueryFactory.select(
                users.count()
        )
                .from(users)
                .where(users.userJoinDate.eq(nowDate).and(
                        users.userState.eq(1)
                ))
                .fetchOne();

        //당일 탈퇴 회원 수
        Long deleteCountByDay = jpaQueryFactory.select(
                users.count()
        )
                .from(users)
                .where(users.userDeleteDate.eq(nowDate).and(
                        users.userState.eq(0)
                ))
                .fetchOne();

        //총 탈퇴회원 수
        Long deleteTotalCount = jpaQueryFactory.select(
                users.count()
        )
                .from(users)
                .where(
                        users.userState.eq(0)
                )
                .fetchOne();

        List<Long> count = new ArrayList<>();
        count.add(totalCount);
        count.add(joinCount);
        count.add(deleteCountByDay);
        count.add(deleteTotalCount);

        Map<String, List> userStats = new HashMap<>();
        userStats.put("userInfoCount", count);
        userStats.put("userRecent", recentJoinDtoList);

        return userStats;
    }





    //회원상태
    private BooleanExpression userStateEq(String userState){

        return StringUtils.hasText(userState) ? users.userState.eq(Integer.valueOf(userState)) : null;
    }


    //회원리스트 - 셀렉트 옵션 동적 메소드
    private BooleanExpression cateEq(String cate, String keyword) {
        if (StringUtils.hasText(cate) && StringUtils.hasText(keyword)) {
            switch (cate) {
                case "userAccount":
                    return users.userAccount.containsIgnoreCase(keyword);
                case "userName":
                    return users.userName.containsIgnoreCase(keyword);
                case "userEmail":
                    return users.userEmail.containsIgnoreCase(keyword);
                case "userPhone":
                    return users.userPhone.containsIgnoreCase(keyword);

                default:
                    break;
            }
        }

        return users.id.isNotNull();
    }

    /**
     * 마이페이지로 이동시 해당 유저 정보 조회
     * @param userId
     * @return
     */
    @Override
    public Optional<UserDetailListDto> findOneByUserId(Long userId) {
    UserDto content = jpaQueryFactory
            .select(new QUserDto(
                    users.id,
                    users.userAccount,
                    users.userName,
                    users.userNickName,
                    users.userPhone,
                    users.userEmail,
                    users.userJoinDate,
                    users.address.zipCode,
                    users.address.address,
                    users.address.detail,
                    users.userIntroduction
            ))
            .from(users)
            .where(users.id.eq(userId))
            .fetchOne();




    Optional<UserDetailListDto> contents =
            Optional.ofNullable(content).map(userDto -> {
                List<UserFileDto> userFileDto = jpaQueryFactory
                        .select(new QUserFileDto(
                                userFile.id,
                                userFile.route,
                                userFile.name,
                                userFile.uuid,
                                users.id
                        ))
                        .from(userFile)
                        .leftJoin(userFile.users, users)
                        .where(users.id.eq(userDto.getId()))
                        .fetch();

                List<UserFileDto> imgDto = userFileDto.stream()
                        .map(imgDtos -> new UserFileDto(
                                imgDtos.getId(),
                                imgDtos.getRoute(),
                                imgDtos.getName(),
                                imgDtos.getUuid(),
                                imgDtos.getUserId()
                        ))
                        .collect(Collectors.toList());


                return new UserDetailListDto(
                        userDto.getId(),
                        userDto.getUserAccount(),
                        userDto.getUserName(),
                        userDto.getUserNickName(),
                        userDto.getUserPhone(),
                        userDto.getUserEmail(),
                        userDto.getUserJoinDate(),
                        userDto.getZipCode(),
                        userDto.getAddress(),
                        userDto.getDetail(),
                        userDto.getIntro(),
                        imgDto
                );
            });

        System.out.println(contents.toString()+"리스트 ");




        return   contents;
}


    /**
     * 유저 이미지 정보 조회
     * @param userId
     * @return
     */
    @Override
    public List<UserFileDto> findAllByUserId(Long userId) {
        return jpaQueryFactory.select(
                new QUserFileDto(
                        userFile.id,
                        userFile.route,
                        userFile.name,
                        userFile.uuid,
                        users.id
                )
        ).from(userFile)
        .leftJoin(userFile.users,users)
        .where(userFile.users.id.eq(userId))
        .fetch();
    }
}
