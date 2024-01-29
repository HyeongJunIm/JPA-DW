package com.example.dw.repository.order;


import com.example.dw.domain.dto.order.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.dw.domain.entity.order.QOrderReview.orderReview;
import static com.example.dw.domain.entity.order.QOrders.orders;
import static com.example.dw.domain.entity.user.QUsers.users;
import static com.example.dw.domain.entity.order.QOrderItem.orderItem;
import static com.example.dw.domain.entity.goods.QGoods.goods;


@Repository
@RequiredArgsConstructor
public class OrderReviewRepositoryImpl implements OrderReviewRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<OrderItemReviewListDto> findAllReview(Pageable pageable, Long id) {

        List<OrderItemReviewListDto> contents= jpaQueryFactory
                .select(new QOrderItemReviewListDto(
                        orderReview.id,
                        orderItem.id,
                        orderReview.title,
                        orderReview.content,
                        orderReview.rating,
                        orderReview.reviewRd,
                        goods.id,
                        goods.goodsName,
                        orders.id,
                        users.id
                ))
                .from(orderReview)
                .leftJoin(orderReview.orderItem,orderItem)
                .leftJoin(orderItem.goods,goods)
                .leftJoin(orderItem.orders,orders)
                .leftJoin(orderItem.orders.users,users)
                .where(orders.users.id.eq(id).and(orderItem.state.eq(1L)))
                .orderBy(orderReview.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long counts = jpaQueryFactory
                .select(orderItem.count())
                .from(orderItem)
                .where(orderItem.orders.users.id.eq(id).and(orderItem.state.eq(1L)))
                .fetchOne();





        return new PageImpl<>(contents,pageable, counts);
    }
}
