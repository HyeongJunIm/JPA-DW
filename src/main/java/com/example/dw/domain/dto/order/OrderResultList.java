package com.example.dw.domain.dto.order;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderResultList {


    private Long orderId;


    private LocalDateTime orderDate;

    private Long userId;



    @QueryProjection
    public OrderResultList(Long orderId, LocalDateTime orderDate, Long userId) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.userId = userId;
    }
}
