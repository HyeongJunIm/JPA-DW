package com.example.dw.domain.dto.order;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.dw.domain.dto.order.QOrderItemDto is a Querydsl Projection type for OrderItemDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderItemDto extends ConstructorExpression<OrderItemDto> {

    private static final long serialVersionUID = 357909485L;

    public QOrderItemDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<Integer> orderQuantity, com.querydsl.core.types.Expression<Integer> orderPrice, com.querydsl.core.types.Expression<Long> goodsid, com.querydsl.core.types.Expression<String> goodsName, com.querydsl.core.types.Expression<Long> goodsMainImgId, com.querydsl.core.types.Expression<String> goodsMainImgName, com.querydsl.core.types.Expression<String> goodsMainImgPath, com.querydsl.core.types.Expression<String> goodsMainImgUuid, com.querydsl.core.types.Expression<Long> state, com.querydsl.core.types.Expression<Long> orderId) {
        super(OrderItemDto.class, new Class<?>[]{long.class, int.class, int.class, long.class, String.class, long.class, String.class, String.class, String.class, long.class, long.class}, id, orderQuantity, orderPrice, goodsid, goodsName, goodsMainImgId, goodsMainImgName, goodsMainImgPath, goodsMainImgUuid, state, orderId);
    }

}

