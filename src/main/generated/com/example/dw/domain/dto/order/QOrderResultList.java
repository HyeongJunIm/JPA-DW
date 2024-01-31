package com.example.dw.domain.dto.order;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.dw.domain.dto.order.QOrderResultList is a Querydsl Projection type for OrderResultList
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderResultList extends ConstructorExpression<OrderResultList> {

    private static final long serialVersionUID = 229894298L;

    public QOrderResultList(com.querydsl.core.types.Expression<Long> orderId, com.querydsl.core.types.Expression<java.time.LocalDateTime> orderDate, com.querydsl.core.types.Expression<Long> userId) {
        super(OrderResultList.class, new Class<?>[]{long.class, java.time.LocalDateTime.class, long.class}, orderId, orderDate, userId);
    }

}

