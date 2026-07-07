package com.neusoft.elderlycare.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.elderlycare.entity.Customer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CustomerMapper extends BaseMapper<Customer> {
    @Select("SELECT customer_no FROM customer " +
            "WHERE customer_no LIKE CONCAT(#{prefix}, '%') " +
            "ORDER BY customer_no DESC LIMIT 1")
    String selectMaxByPrefix(@Param("prefix") String prefix);
}
