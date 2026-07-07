package com.neusoft.elderlycare.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.CheckOut;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CheckOutMapper extends BaseMapper<CheckOut> {
    IPage<CheckOut> selectCheckOutPage(Page<CheckOut> page, @Param("keyword") String keyword);
    @Select("SELECT checkout_no FROM check_out " +
            "WHERE checkout_no LIKE CONCAT(#{prefix}, '%') " +
            "ORDER BY checkout_no DESC LIMIT 1")
    String selectMaxByPrefix(@Param("prefix") String prefix);
}
