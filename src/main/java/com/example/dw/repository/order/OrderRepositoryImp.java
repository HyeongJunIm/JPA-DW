package com.example.dw.repository.order;

import com.example.dw.domain.dto.order.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.dw.domain.entity.order.QOrders.orders;
import static com.example.dw.domain.entity.user.QUsers.users;
import static com.example.dw.domain.entity.order.QOrderItem.orderItem;
import static com.example.dw.domain.entity.goods.QGoodsMainImg.goodsMainImg;
import static com.example.dw.domain.entity.goods.QGoods.goods;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImp implements OrderRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    private final EntityManager em;


    @Override
    public Page<OrderListResultDto> findAllByMyOrderId(Pageable pageable,Long userId) {

        List<OrderResultList> orderResultLists = jpaQueryFactory.
                select(new QOrderResultList(
                        orders.id,
                        orders.orderRegisterDate,
                        users.id
                )).from(orders)
                .leftJoin(orders.users, users)
                .where(users.id.eq(userId))
                .orderBy(orders.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        List<OrderItemDto> orderItemDtos = jpaQueryFactory
                .select(new QOrderItemDto(
                        orderItem.id,
                        orderItem.orderPrice,
                        orderItem.orderQuantity,
                        goods.id,
                        goods.goodsName,
                        goodsMainImg.id,
                        goodsMainImg.goodsMainImgName,
                        goodsMainImg.goodsMainImgPath,
                        goodsMainImg.goodsMainImgUuid,
                        orderItem.state,
                        orders.id
                )).from(orderItem)
                .leftJoin(orderItem.goods, goods)
                .leftJoin(goods.goodsMainImg, goodsMainImg)
                .fetch();

        List<OrderListResultDto> result = new ArrayList<>();

        for (OrderResultList orderResultList : orderResultLists) {
            List<OrderItemDto> relatedOrderItems = orderItemDtos.stream()
                    .filter(orderItemDto -> orderItemDto.getOrderId().equals(orderResultList.getOrderId()))
                    .collect(Collectors.toList());

            OrderListResultDto orderListResultDto = new OrderListResultDto(
                    orderResultList.getOrderId(),
                    orderResultList.getOrderDate(),
                    orderResultList.getUserId(),
                    relatedOrderItems
            );

            result.add(orderListResultDto);

        }


        Long count = jpaQueryFactory.select(
                orders.count())
                .from(orders)
                .where(users.id.eq(userId))
                .fetchOne();


        return new PageImpl<>(result,pageable,count);
    }
}
