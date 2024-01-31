package com.example.dw.domain.dto.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemDto {


    private Long id;
    private Integer orderQuantity;
    private Integer orderPrice;

    private Long goodsid;
    private String goodsName;
    private Long goodsMainImgId;
    private String goodsMainImgName;
    private String goodsMainImgPath;
    private String goodsMainImgUuid;
    private Long state =0L;
    private Long orderId;


    @QueryProjection

    public OrderItemDto(Long id, Integer orderQuantity, Integer orderPrice, Long goodsid, String goodsName, Long goodsMainImgId, String goodsMainImgName, String goodsMainImgPath, String goodsMainImgUuid, Long state, Long orderId) {
        this.id = id;
        this.orderQuantity = orderQuantity;
        this.orderPrice = orderPrice;
        this.goodsid = goodsid;
        this.goodsName = goodsName;
        this.goodsMainImgId = goodsMainImgId;
        this.goodsMainImgName = goodsMainImgName;
        this.goodsMainImgPath = goodsMainImgPath;
        this.goodsMainImgUuid = goodsMainImgUuid;
        this.state = state;
        this.orderId = orderId;
    }
}
