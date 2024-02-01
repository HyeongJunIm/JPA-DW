package com.example.dw.repository.order;

import com.example.dw.domain.dto.order.OrderListResultDto;
import com.example.dw.domain.dto.order.OrderResultList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface OrderRepositoryCustom {

    Page<OrderListResultDto> findAllByMyOrderId(Pageable pageable,Long userId);


}
