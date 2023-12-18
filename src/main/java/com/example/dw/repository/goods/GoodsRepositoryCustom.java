package com.example.dw.repository.goods;

import com.example.dw.domain.dto.admin.GoodsDto;
import com.example.dw.domain.dto.goods.GoodsDetailResultDto;
import com.example.dw.domain.form.SearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GoodsRepositoryCustom {

    Page<GoodsDto> findGoodsAll(Pageable pageable, SearchForm searchForm);

    List<GoodsDetailResultDto> findGoodsById(Long id);




}
