package com.example.dw.repository.order;

import com.example.dw.domain.dto.order.*;
import com.example.dw.domain.entity.order.QOrderItem;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.dw.domain.entity.order.QOrderItem.orderItem;
import static com.example.dw.domain.entity.goods.QGoodsMainImg.goodsMainImg;
import static com.example.dw.domain.entity.order.QOrderReview.orderReview;
import static com.example.dw.domain.entity.goods.QGoods.goods;
import static com.example.dw.domain.entity.order.QOrders.orders;
import static com.example.dw.domain.entity.user.QUsers.users;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImp implements OrderItemRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    private final EntityManager em;


}
