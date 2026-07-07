package com.neusoft.elderlycare.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.Outing;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OutingMapper extends BaseMapper<Outing> {
    IPage<Outing> selectOutingPage(Page<Outing> page, @Param("keyword") String keyword);
    @Select("SELECT outing_no FROM outing " +
            "WHERE outing_no LIKE CONCAT(#{prefix}, '%') " +
            "ORDER BY outing_no DESC LIMIT 1")
    String selectMaxByPrefix(@Param("prefix") String prefix);
}
