package com.example.dw.repository.order;


import com.example.dw.domain.dto.order.OrderItemDto;
import com.example.dw.domain.dto.order.OrderReviewImgDto;

import java.util.List;

public interface OrderReviewImgRepositoryCustom {
    List<OrderReviewImgDto> findReviewImgById(Long id);

}
